job "${nomad_namespace}-backup-db" {
  datacenters = ["${datacenter}"]

  # Nomad places all jobs and their derived objects into namespaces. These include jobs, allocations, deployments, and evaluations.  
  namespace = "${nomad_namespace}" # exemple : platines-dev

  type = "batch"
  periodic {
    cron             = "${backup_cron}" # "0 04 * * 0 *"
    prohibit_overlap = true             # Specifies if this job should wait until previous instances of this job have completed. This only applies to this job; it does not prevent other periodic jobs from running at the same time.
  }

  vault {
    policies    = ["${vault_acl_policy_name}"]
    change_mode = "restart" # Specifies the behavior Nomad should take if the Vault token changes
  }

  group "g-backup-db-platines" {
    count = 1 # Specifies the number of instances that should be running under for this group.

    task "dump-db" {
      driver = "docker"

      lifecycle {
        hook    = "prestart" # prestart : Will be started immediately. The main tasks will not start until all prestart tasks with 'sidecar = false' have completed successfully.        
        sidecar = false      # false = is ephemeral ; true = is long-lived
      }

      config {
        image   = "${image}:${tag}"
        command = "bash"
        args    = ["/secrets/backup.sh"]
      }

      resources {
        cpu    = 20
        memory = 512
      }

      template {
        # " noop " - take no action (continue running the task)
        change_mode = "noop"

        destination = "secrets/backup.sh"
        data        = <<EOH
#!/bin/bash
# Variables globales
TODAY="_$(date "+%Y%m%d_%H%M%S")"
HOME_DIR="$(pwd)"
DUMP_DIR="$${NOMAD_ALLOC_DIR}/data"
LOG_DIR="$${HOME_DIR}"
DUMP_FILE="mysqldump_platines.sql"
ZIP_FILE="$${DUMP_FILE}.gz"
LOG_FILE="$${NOMAD_ALLOC_DIR}/logs/platines_dumpdb$${TODAY}.log"
TMP_FILE="$${NOMAD_ALLOC_DIR}/tmp/platines_dumpdb$${TODAY}.tmp"

# récupère l'address de Mariadb dans Consul
{{range service ( print (env "NOMAD_NAMESPACE") "-db") }}
MARIADB_IP={{.Address}}
MARIADB_PORT={{.Port}}
{{end}}

# récupère les secrets dans Vault
{{with secret "${vault_secrets_engine_name}/mariadb"}}
PLATINES_USER={{.Data.data.mariadb_username}}
PLATINES_PASSWD={{.Data.data.mariadb_password}}
{{end}}

PLATINES_DB="platines"

# Generation du dump de la base MARIADB
echo -e "Generation du dump de la base MARIADB \"$${DUMP_DIR}/$${DUMP_FILE}\"..."
mysqldump -v -h $${MARIADB_IP} -P $${MARIADB_PORT} -u $${PLATINES_USER} -p$${PLATINES_PASSWD} $${PLATINES_DB} > "$${DUMP_DIR}/$${DUMP_FILE}" 2>$${TMP_FILE}
RET_CODE=$?
if [ $${RET_CODE} -ne 0 ]
then
    echo -e "[ERROR] - En execution de la commande : mysqldump -h $${MARIADB_IP} -P $${MARIADB_PORT} -u $${PLATINES_USER} -p$${PLATINES_PASSWD} $${PLATINES_DB} > \"$${DUMP_DIR}/$${DUMP_FILE}\" !"
    cat $${TMP_FILE} >> $${LOG_FILE}
    echo -e "Exit code : $${RET_CODE}"
    exit 1
else
    echo "(OK)"
fi

cat $${TMP_FILE} >> $${LOG_FILE}

# Zippage du dump de la base MARIADB
echo "Zippage du dump, l'enregistrer dans : \"$${DUMP_DIR}/$${ZIP_FILE}\"..."
cd "$${DUMP_DIR}" && gzip "$${DUMP_FILE}" > $${TMP_FILE} 2>&1
RET_CODE=$?
cd "$${HOME_DIR}"
if [ $${RET_CODE} -ne 0 ]
then
    echo -e "[ERROR] - En execution de la commande : gzip \"$${DUMP_FILE}\" ! "
    cat $${TMP_FILE} >> $${LOG_FILE}
    echo -e "Exit code : $${RET_CODE}"
    exit 1
else
    echo "(OK)"
fi

cat $${TMP_FILE} >> $${LOG_FILE}

# Compte rendu du fichier dump cree par le traitement
echo -e "(Fin du task 'dump-db')"
EOH

      }
    }

    task "send-dump" {
      driver = "docker"
      config {
        image   = "614q518g.gra7.container-registry.ovh.net/ans/scp-client"
        command = "bash"
        args    = ["/secrets/send.sh"]
      }

      # log-shipper
      leader = true

      template {
        change_mode = "noop"
        destination = "/secrets/id_rsa"
        perms       = "600"
        data        = <<EOH
{{with secret "${vault_secrets_engine_name}/mariadb"}}{{.Data.data.rsa_private_key}}{{end}}
        EOH
      }

      template {
        change_mode = "noop"
        destination = "/secrets/send.sh"
        data        = <<EOH
#!/bin/bash
TODAY="_$(date "+%Y%m%d_%H%M%S")"
DUMP_DIR="$${NOMAD_ALLOC_DIR}/data"
DUMP_FILE="mysqldump_platines$${TODAY}.sql.gz"

echo -e "Envois fichier dump vers server backup et le renommer à \"$${DUMP_FILE}\" ..."
{{with secret "${vault_secrets_engine_name}/mariadb"}}
BACKUP_SERVER={{.Data.data.backup_server}}
TARGET_FOLDER={{.Data.data.backup_folder}}
SSH_USER={{.Data.data.ssh_user}}
{{end}}
scp -o StrictHostKeyChecking=accept-new -i /secrets/id_rsa $${DUMP_DIR}/mysqldump_platines.sql.gz $${SSH_USER}@$${BACKUP_SERVER}:$${TARGET_FOLDER}/$${DUMP_FILE}
RET_CODE=$?
if [ $${RET_CODE} -ne 0 ]
then
    echo -e "[ERROR] - En execution de la commande : scp -o StrictHostKeyChecking=accept-new -i /secrets/id_rsa \"$${DUMP_DIR}\"/mysqldump_platines.sql.gz \"$${SSH_USER}\"@\"$${BACKUP_SERVER}\":\"$${TARGET_FOLDER}\"/\"$${DUMP_FILE}\" ! "
    echo -e "Exit code : $${RET_CODE}"
    exit 1
else
    echo "(OK)"
fi

echo "(Fin du Task 'send-dump')"
EOH
      }
    }

    # task "log-shipper" {
    #   driver = "docker"
    #   config {
    #     image = "${log_shipper_image}:${log_shipper_tag}"
    #   }
    #   resources {
    #     cpu    = 100
    #     memory = 150
    #   }
    #   restart {
    #     interval = "3m"
    #     attempts = 5
    #     delay    = "15s"
    #     mode     = "delay"
    #   }
    #   meta {
    #     INSTANCE = "$${NOMAD_ALLOC_NAME}"
    #   }
    #   template {
    #     destination = "local/file.env"
    #     change_mode = "restart"
    #     env         = true
    #     data        = <<EOH
# REDIS_HOSTS={{ range service "PileELK-redis" }}{{ .Address }}:{{ .Port }}{{ end }}
# PILE_ELK_APPLICATION=${nomad_namespace}
# EOH        
    #   }
    # } #end log-shipper - TEMPORARILY DISABLED

  }
}

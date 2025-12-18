project = "${workspace.name}/platines"

# Labels can be specified for organizational purposes.
labels = { "domaine" = "platines" }

runner {
  enabled = true
  profile = "platines-${workspace.name}"

  data_source "git" {
    url  = "https://github.com/ansforge/dops-platines-application"
    ref  = "main"
    path = "deploiement/waypoint"
  }
  poll {
    enabled = false
  }
}

# MariaDB
app "database" {
  build {
    use "docker-ref" {
      image = var.database_image
      tag   = var.database_tag
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-db/database.nomad.tpl", {
        datacenter                = var.datacenter
        vault_secrets_engine_name = var.vault_secrets_engine_name
        vault_acl_policy_name     = var.vault_acl_policy_name
        nomad_namespace           = var.nomad_namespace
        image                     = var.database_image
        tag                       = var.database_tag
        log_shipper_image         = var.log_shipper_image
        log_shipper_tag           = var.log_shipper_tag
      })
    }
  }
}

app "backup-db" {
  build {
    use "docker-ref" {
      image = var.database_image
      tag   = var.database_tag
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-db/backup-database.nomad.tpl", {
        datacenter                = var.datacenter
        vault_secrets_engine_name = var.vault_secrets_engine_name
        vault_acl_policy_name     = var.vault_acl_policy_name
        nomad_namespace           = var.nomad_namespace
        image                     = var.database_image
        tag                       = var.database_tag
        log_shipper_image         = var.log_shipper_image
        log_shipper_tag           = var.log_shipper_tag
        backup_cron               = var.backup_cron
      })
    }
  }
}

# Webapp application.
#app "webapp" {
  # DOCKER BUILD: Build l'image au lieu de docker-ref
  #build {
    #use "docker" {
     # dockerfile = "${path.app}/Dockerfile"
      #build_args = {
       # ARTIFACTS_VERSION        = var.artifacts_version
        #ARTIFACTS_REPOSITORY_URL = var.artifacts_repository_url
      #}
    #}
    #registry {
     # use "docker" {
      #  image    = "${var.registry_username}/platines-webapp"
       # tag      = var.artifacts_version
        #username = var.registry_username
        #password = var.registry_password
      #}
    #}
  #}
  
  # DOCKER-REF: Ancienne méthode (pull image publique)
  # build {
  #   use "docker-ref" {
  #     image = var.webapp_image
  #     tag   = var.webapp_tag
  #   }
  # }

 # deploy {
  #  use "nomad-jobspec" {
   #   jobspec = templatefile("${path.app}/platines-back/platines.nomad.tpl", {
    #    datacenter                    = var.datacenter
     #   vault_secrets_engine_name     = var.vault_secrets_engine_name
      #  vault_acl_policy_name         = var.vault_acl_policy_name
       # nomad_namespace               = var.nomad_namespace
        #artifacts_repository_url      = var.artifacts_repository_url
        #artifacts_version             = var.artifacts_version
        #job_tmpl_repository           = var.job_tmpl_repository
        #environment_java_tool_options = var.environment_java_tool_options
        ## DOCKER BUILD: Utilise l'image buildée dans Docker Hub
        #image                         = "${var.registry_username}/platines-webapp"
        #tag                           = var.artifacts_version
        # DOCKER-REF: Ancienne méthode
        # image                       = var.webapp_image
        # tag                         = var.webapp_tag
        #log_shipper_image             = var.log_shipper_image
        #log_shipper_tag               = var.log_shipper_tag
      #})
    #}
  #}
#}
app "webapp" {
  build {
    use "docker" {
      dockerfile = "${path.app}/Dockerfile"
      build_args = {
        ARTIFACTS_VERSION        = var.artifacts_version
        ARTIFACTS_REPOSITORY_URL = var.artifacts_repository_url
      }
    }

    registry {
      use "docker" {
        image    = "${var.registry_username}/platines-webapp"
        tag      = var.artifacts_version
        username = var.registry_username
        password = var.registry_password
      }
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-back/platines.nomad.tpl", {
        datacenter                    = var.datacenter
        vault_secrets_engine_name     = var.vault_secrets_engine_name
        vault_acl_policy_name         = var.vault_acl_policy_name
        nomad_namespace               = var.nomad_namespace
        artifacts_repository_url      = var.artifacts_repository_url
        artifacts_version             = var.artifacts_version
        job_tmpl_repository           = var.job_tmpl_repository
        environment_java_tool_options = var.environment_java_tool_options
        image                         = "${var.registry_username}/platines-webapp"
        tag                           = var.artifacts_version
        log_shipper_image             = var.log_shipper_image
        log_shipper_tag               = var.log_shipper_tag
      })
    }
  }
}

# HAProxy
app "reverse-proxy" {
  build {
    use "docker-ref" {
      image = var.rp_image
      tag   = var.rp_tag
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-rp/reverse-proxy.nomad.tpl", {
        datacenter                = var.datacenter
        vault_secrets_engine_name = var.vault_secrets_engine_name
        vault_acl_policy_name     = var.vault_acl_policy_name
        nomad_namespace           = var.nomad_namespace
        image                     = var.rp_image
        tag                       = var.rp_tag
        log_shipper_image         = var.log_shipper_image
        log_shipper_tag           = var.log_shipper_tag
      })
    }
  }
}

############## variables ##############
variable "artifacts_repository_url" {
  type    = string
  default = "https://repo.esante.gouv.fr/repository/asip-releases"
  env     = ["ARTIFACTS_REPOSITORY_URL"]
}

variable "artifacts_version" {
  type    = string
  default = "2.3.2"
}

variable "datacenter" {
  type    = string
  default = "scaleway_test_dc"
  env     = ["NOMAD_DATACENTER"]
}

# Convention :
# [NOM-WORKSPACE] = [waypoint projet name] = [nomad namespace name] = [Vault ACL Policies Name] = [Valut Secrets Engine Name]

variable "nomad_namespace" {
  type    = string
  default = "platines-${workspace.name}"
  env     = ["NOMAD_NAMESPACE"]
}

variable "vault_acl_policy_name" {
  type    = string
  default = "platines-${workspace.name}"
}

variable "vault_secrets_engine_name" {
  type    = string
  default = "platines-${workspace.name}"
}

# MariaDB
variable "database_image" {
  type    = string
  default = "mariadb"
}

variable "database_tag" {
  type    = string
  default = "10.7"
}

# Backup-db
variable "backup_cron" {
  type    = string
  default = "0 04 * * *"
}

# reverse-proxy
variable "rp_image" {
  type    = string
  default = "haproxy"
}

variable "rp_tag" {
  type    = string
  default = "lts"
}

# platines-back (webapp / tomcat)
variable "webapp_image" {
  type    = string
  default = "openjdk"
}

variable "webapp_tag" {
  type    = string
  default = "17-slim-buster"
}

variable "environment_java_tool_options" {
  type    = string
  default = "-Dlogging.level.fr.asipsante.platines.service.impl.nomad=trace"
}

variable "job_tmpl_repository" {
  type    = string
  default = ""
}

# log-shipper
variable "log_shipper_image" {
  type    = string
  default = "614q518g.gra7.container-registry.ovh.net/ans/nomad-filebeat"
}

variable "log_shipper_tag" {
  type    = string
  default = "8.2.3-2.0"
}

# Registry Docker pour push des images buildées
variable "registry_host" {
  type    = string
  default = "614q518g.gra7.container-registry.ovh.net"
  env     = ["REGISTRY_HOST"]
}

variable "registry_username" {
  type      = string
  default   = ""
  env       = ["REGISTRY_USERNAME"]
  sensitive = true
}

variable "registry_password" {
  type      = string
  default   = ""
  env       = ["REGISTRY_PASSWORD"]
  sensitive = true
}

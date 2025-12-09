/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
job "${nomad_namespace}-db" {
  datacenters = [
    "${datacenter}"
  ]
  type      = "service"
  namespace = "${nomad_namespace}"
  update {
    stagger      = "30s"
    max_parallel = 1
  }
  vault {
    policies    = ["platines"]
    change_mode = "restart"
  }
  group "db-servers" {
    count = "1"
    # install only on "data" nodes
    constraint {
      attribute = "$${node.class}"
      value     = "data"
    }
    restart {
      attempts = 3
      delay    = "60s"
      interval = "1h"
      mode     = "fail"
    }
    network {
      port "db" { to = 3306 }
    }

    task "mariadb" {
      driver = "docker"
      config {
        image   = "${image}:${tag}"
        ports   = ["db"]
        volumes = [
          "name=platines_db,io_priority=high,size=20,repl=2:/var/lib/mysql"
        ]
        volume_driver = "pxd"
      }
      template {
        data        = <<EOH
MARIADB_USER="{{with secret "platines/mariadb"}}{{.Data.data.username}}{{end}}"
MARIADB_DATABASE="platines"
MARIADB_ROOT_PASSWORD="{{with secret "platines/mariadb"}}{{.Data.data.rootpassword}}{{end}}"
MARIADB_PASSWORD="{{with secret "platines/mariadb"}}{{.Data.data.password}}{{end}}"
EOH
        destination = "secrets/file.env"
        env         = true
      }
      resources {
        cpu    = 500
        memory = 2048
      }
      service {
        name = "$${NOMAD_JOB_NAME}"
        port = "db"
        check {
          type     = "tcp"
          port     = "db"
          name     = "check_mysql"
          interval = "120s"
          timeout  = "2s"
        }
        check_restart {
          limit           = 3
          grace           = "120s"
          ignore_warnings = true
        }
      }
    }
  }
}

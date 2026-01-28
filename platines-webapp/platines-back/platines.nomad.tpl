/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
job "${nomad_namespace}-webapp" {
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
  group "platines-webapp" {
    count = "1"
    network {
      port "http" {
        to = 8080
      }
    }
    task "platines-webapp" {
      driver = "docker"
      config {
        image = "${image}:${tag}"
        # DOCKER BUILD: entrypoint défini dans le Dockerfile
        # entrypoint = [
        #   "java",
        #   "-jar",
        #   "/usr/app/platines-back.war"
        # ]
        ports = ["http"]
        
        # DOCKER BUILD: WAR inclus dans l'image, plus besoin de mount
        # mount {
        #   type = "bind"
        #   target = "/usr/app/platines-back.war"
        #   source = "local/platines-back-${artifacts_version}.war"
        #   readonly = false
        #   bind_options {
        #     propagation = "rshared"
        #   }
        # }
        
        mount {
          type = "bind"
          target = "/usr/app/workspace"
          source = "tmp"
          readonly = false
          bind_options {
            propagation = "rshared"
          }
        }
      }
      
      # DOCKER BUILD: artifact téléchargé pendant le build de l'image
      # artifact {
      #   source = "${artifacts_repository_url}/fr/ans/platines/platines-back/${artifacts_version}/platines-back-${artifacts_version}.war"
      # }
      
      env {
        TOMCAT_ADDR = "$${NOMAD_ADDR_http}"
      }
      template {
        destination = "secrets/application.properties"
        change_mode = "restart"
        data        = <<EOH
# Wht a good idea d'écrasr le conf de l'application
spring.main.allow-bean-definition-overriding=true
logging.level.org.springframework.web=DEBUG
logging.level.root=INFO
server.servlet.context-path=/
# Truststore configuration
{{with secret "platines/tomcat"}}
default.truststore.external.url={{.Data.data.default_truststore_external_url}}{{end}}
# MariaDB
{{range service ( print (env "NOMAD_NAMESPACE") "-db") }}
spring.datasource.url=jdbc:mariadb://{{.Address}}:{{.Port}}/platines{{end}}
{{with secret "platines/mariadb"}}
spring.datasource.username=root
spring.datasource.password={{.Data.data.rootpassword}}{{end}}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.testWhileIdle=true
spring.datasource.validationQuery=SELECT 1
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=create-drop
# Mail
{{ with secret "platines/tomcat" }}
spring.mail.alias={{ .Data.data.mail_alias }}
spring.mail.personal-name={{ .Data.data.mail_personal_name }}
spring.mail.host={{ .Data.data.mail_server_host }}
spring.mail.port={{ .Data.data.mail_server_port }}
spring.mail.username={{ .Data.data.mail_username }}
spring.mail.password={{ .Data.data.mail_password }}{{ end }}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
# Security
security.ignored="*.bundle.*"
server.servlet.session.timeout=24h
server.servlet.session.cookie.secure=true
# Using a profile instead of trashig the application's base configuation might be a good idea.
generic.ssl.password=${GENERIC_PASSWORD_SSL}
EOH
      }
      template {
        data        = <<EOH
JAVA_TOOL_OPTIONS="-Xms1g -Xmx6g -Duser.timezone=Europe/Paris -Dspring.config.location=/secrets/application.properties"
PROXY_HOST="10.0.49.163"
PROXY_PORT="3128"
GENERIC_PASSWORD_SSL="{{with secret "platines/tomcat"}}{{.Data.data.password}}{{end}}"
EXTERNAL_PROTOCOL="https"
FRONT_SERVER_TASK_GROUP="fabio"
API_DOMAIN="{{with secret "platines/front"}}{{.Data.data.platines_public_hostname}}{{end}}"
MOCK_SERVICE_DOMAIN="{{with secret "platines/tomcat"}}{{.Data.data.platines_mock_service_domain}}{{end}}"
{{range service "http.nomad"}}
NOMAD_API_PORT="{{.Port}}"{{end}}
EOH
        destination = "secrets/file.env"
        change_mode = "restart"
        splay       = "30s"
        env         = true
      }
      resources {
        cpu    = 500
        memory = 7000
      }
      service {
        name = "$${NOMAD_JOB_NAME}"
        port = "http"
        check {
          type     = "http"
          port     = "http"
          path     = "/"
          name     = "check_tomcat"
          interval = "40s"
          timeout  = "10s"
        }
      }
    }

#    task "log-shipper" {
#      driver = "docker"
#      restart {
#        interval = "30m"
#        attempts = 5
#        delay    = "15s"
#        mode     = "delay"
#      }
#      meta {
#        INSTANCE = "$\u007BNOMAD_ALLOC_NAME\u007D"
#      }
#      template {
#        data        = <<EOH
#LOGSTASH_HOST = {{ range service "${nomad_namespace}-logstash" }}{{ .Address }}:{{ .Port }}{{ end }}
#ENVIRONMENT = "${datacenter}"
#EOH
#        destination = "local/file.env"
#        env         = true
#      }
#      config {
#        image = "prosanteconnect/filebeat:7.17.0"
#      }
#    }
  }
}

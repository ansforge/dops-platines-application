job "${nomad_namespace}-webapp" {
  datacenters = ["${datacenter}"]
  type        = "service"
  namespace   = "${nomad_namespace}"
  update {
    stagger      = "30s"
    max_parallel = 1
    auto_revert  = true
  }
  vault {
    policies    = ["${vault_acl_policy_name}"]
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

      # log-shipper
      leader = true

      config {
        image = "${image}:${tag}"
        entrypoint = [
          "java",
          "-jar",
          "/usr/app/platines-back.war"
        ]
        ports = ["http"]

        mount {
          type     = "bind"
          target   = "/usr/app/platines-back.war"
          source   = "local/platines-back-${artifacts_version}.war"
          readonly = false
          bind_options {
            propagation = "rshared"
          }
        }

        mount {
          type     = "bind"
          target   = "/usr/app/workspace"
          source   = "tmp"
          readonly = false
          bind_options {
            propagation = "rshared"
          }
        }
      }

      artifact {
        source = "${artifacts_repository_url}/fr/ans/platines/platines-back/${artifacts_version}/platines-back-${artifacts_version}.war"
      }

      env {
        TOMCAT_ADDR         = "$${NOMAD_ADDR_http}"
        JOB_TMPL_REPOSITORY = "${job_tmpl_repository}"
      }

      template {
        destination = "secrets/application-ext.properties"
        change_mode = "restart"
        data        = <<EOH
{{with secret "${vault_secrets_engine_name}/tomcat"}}
generic.ssl.password={{.Data.data.generic_ssl_password}}
mockservices.domain={{.Data.data.platines_mock_service_domain}}
mock.libs.url=${artifacts_repository_url}/fr/ans/platines/soapui-mockservice-libs/${artifacts_version}/soapui-mockservice-libs-${artifacts_version}.zip
default.truststore.external.url={{.Data.data.default_truststore_external_url}}
# Default values :
# workspace=/usr/app/workspace
# spring.main.allow-bean-definition-overiding=true
# server.servlet.context-path=/
{{end}}
tomcat.addr={{env "NOMAD_ADDR_http"}}
logging.level.root=INFO

# MariaDB
{{range service ( print (env "NOMAD_NAMESPACE") "-db") }}
spring.datasource.url=jdbc:mariadb://{{.Address}}:{{.Port}}/platines{{end}}
{{with secret "${vault_secrets_engine_name}/mariadb"}}
spring.datasource.username={{.Data.data.mariadb_username}}
spring.datasource.password={{.Data.data.mariadb_password}}{{end}}
# Default values :
# spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
# spring.datasource.testWhileIdle=true
# spring.datasource.validationQuery=SELECT 1
# spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
# spring.jpa.hibernate.ddl-auto=create-drop

# Mail
{{ with secret "${vault_secrets_engine_name}/tomcat" }}
spring.mail.alias={{ .Data.data.mail_alias }}
spring.mail.personal-name={{ .Data.data.mail_personal_name }}
spring.mail.host={{ .Data.data.mail_server_host }}
spring.mail.port={{ .Data.data.mail_server_port }}
spring.mail.username={{ .Data.data.mail_username }}
spring.mail.password={{ .Data.data.mail_password }}
spring.mail.properties.mail.smtp.auth={{.Data.data.mail_properties_mail_smtp_auth}}
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.protocols=TLSv1.2

# Default values :
# Security
# security.ignored="*.bundle.*"
# server.servlet.session.timeout=24h
# server.servlet.session.cookie.secure=true
{{end}}
EOH
      }

      template {
        destination = "secrets/file.env"
        change_mode = "restart"
        splay       = "30s"
        env         = true
        data        = <<EOH
JAVA_TOOL_OPTIONS="-Xms1g -Xmx6g -Duser.timezone=Europe/Paris -Dspring.config.location=classpath:application.properties,/secrets/application-ext.properties ${environment_java_tool_options}"
{{with secret "${vault_secrets_engine_name}/tomcat"}}
PROXY_HOST={{.Data.data.proxy_host}}
PROXY_PORT={{.Data.data.proxy_port}}
GENERIC_PASSWORD_SSL={{.Data.data.generic_ssl_password}}
MOCK_SERVICE_DOMAIN={{.Data.data.platines_mock_service_domain}}
{{end}}
EXTERNAL_PROTOCOL="https"
FRONT_SERVER_TASK_GROUP="fabio"
API_DOMAIN="{{with secret "${vault_secrets_engine_name}/front"}}{{.Data.data.platines_public_hostname}}{{end}}"
{{range service "http.nomad"}}
NOMAD_API_PORT="{{.Port}}"{{end}}
EOH
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

    task "log-shipper" {
      driver = "docker"
      config {
        image = "${log_shipper_image}:${log_shipper_tag}"
      }
      resources {
        cpu    = 100
        memory = 150
      }
      restart {
        interval = "3m"
        attempts = 5
        delay    = "15s"
        mode     = "delay"
      }
      meta {
        INSTANCE = "$${NOMAD_ALLOC_NAME}"
      }
      template {
        destination = "local/file.env"
        change_mode = "restart"
        env         = true
        data        = <<EOH
REDIS_HOSTS={{ range service "PileELK-redis" }}{{ .Address }}:{{ .Port }}{{ end }}
PILE_ELK_APPLICATION=${nomad_namespace}
EOH        
      }
    } #end log-shipper
  }
}

/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
job "" {
  datacenters = []
  type = "service"

  group "server-mock" {
    network {
      port "httpsmock" {
        to = 8443
      }
      port "httpmock" {
        to = 8080
      }
	}
    task "tomcat-mock" {
      driver = "docker"

     
      template {
        change_mode = "restart"
        destination = "secrets/server.xml"
        data = <<EOT
<?xml version="1.0" encoding="UTF-8"?>
<Server port="8005" shutdown="SHUTDOWN">
  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
  <GlobalNamingResources>
    <Resource name="UserDatabase" auth="Container"
              type="org.apache.catalina.UserDatabase"
              description="User database that can be updated and saved"
              factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
              pathname="conf/tomcat-users.xml" />
  </GlobalNamingResources>
  <Service name="Catalina">
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               relaxedQueryChars="${TOMCAT_RELAXED_QUERY_CHARS}">
        <UpgradeProtocol className="org.apache.coyote.http2.Http2Protocol" />
        <SSLHostConfig certificateVerification="${MTLS}" sslProtocol="TLS"
                       truststoreType="JKS" 
                       truststoreFile="/secrets/${TRUSTSTORE_FILE}"
                       truststorePassword="changeit">
            <Certificate certificateKeystoreType="PKCS12"
                         certificateKeystorePassword="${KEYSTORE_PASSWORD}"
                         certificateKeystoreFile="/secrets/${KEYSTORE_FILE}"
                         type="RSA" />
        </SSLHostConfig>
    </Connector>
    <Engine name="Catalina" defaultHost="localhost">
      <Realm className="org.apache.catalina.realm.LockOutRealm">
        <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
               resourceName="UserDatabase"/>
      </Realm>
      <Host name="localhost"  appBase="webapps"
            unpackWARs="true" autoDeploy="true">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="localhost_access_log" suffix=".txt"
               pattern="%h %l %u %t &quot;%r&quot; %s %b" />
      </Host>
    </Engine>
  </Service>
</Server>
EOT
      }
      template {
        change_mode = "restart"
        destination = "secrets/logging.properties"
        data = <<EOT
handlers = java.util.logging.ConsoleHandler

.handlers = java.util.logging.ConsoleHandler

############################################################
# Handler specific properties.
# Describes specific configuration info for Handlers.
############################################################

java.util.logging.ConsoleHandler.level = FINE
java.util.logging.ConsoleHandler.formatter = org.apache.juli.OneLineFormatter
java.util.logging.ConsoleHandler.encoding = UTF-8

############################################################
# Facility specific properties.
# Provides extra control for each logger.
############################################################

# To see debug messages for HTTP/2 handling, uncomment the following line:
#org.apache.coyote.http2.level = FINE

org.apache.tomcat.util.net.NioEndpoint.handshake.level = FINE
org.apache.tomcat.util.net.Nio2Endpoint.handshake.level = FINE
org.apache.tomcat.util.net.openssl.OpenSSLEngine.level = FINE
org.apache.tomcat.util.net.openssl.OpenSSLContext.level = FINE
org.apache.tomcat.util.net.SSLUtilBase.level = FINE
jdk.internal.event.EventHelper.logX509CertificateEvent.level = FINE
EOT
      }

      config {
        image = "tomcat:9-jdk17"
        ports = [
          "httpsmock",
          "httpmock"
         ]
         mount {
           type = "bind"
           target = "/usr/local/tomcat/webapps"
           source = "local"
           readonly = false
           bind_options {
             propagation = "rshared"
           }
         }
         mount {
           type = "bind"
           target = "/usr/local/tomcat/webapps/mockservice/WEB-INF/lib"
           source = "tmp/lib"
           readonly = false
           bind_options {
             propagation = "rshared"
           }
         }
         mount {
           type = "bind"
           target = "/usr/local/tomcat/conf/server.xml"
           source = "secrets/server.xml"
           readonly = false
           bind_options {
             propagation = "rshared"
           }
         }
         mount {
           type = "bind"
           target = "/root/soapui-settings.xml"
           source = "secrets/soapui-settings.xml"
           readonly = false
           bind_options {
             propagation = "rshared"
           }
         }
         mount {
           type = "bind"
           target = "/usr/local/tomcat/conf/logging.properties"
           source = "secrets/logging.properties"
           readonly = false
           bind_options {
             propagation = "rshared"
           }
         }
         
      }

      resources {
        cpu    = 500
        memory = 1024
      }
	  service {
        name = "mock-service"
        canary_tags = []
        tags = []
        port = "httpsmock"
        check {
          name = "https port alive"
          type = "tcp"
          args = []
          interval = "30s"
          timeout = "2s"
          failures_before_critical = 5
        }
      }
    }
  }
}

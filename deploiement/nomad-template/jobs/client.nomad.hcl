job "" {
  datacenters = []
  type = "batch"
  vault {
    policies    = ["platines"]
    change_mode = "restart"
  }
  group "" {
    task "readyapi-runner" {
	  artifact {
		source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/asip-snapshots/fr/asipsante/platines/testdriver/2.0.5-SNAPSHOT/testdriver-2.0.5-SNAPSHOT-dist.zip"
      }
      driver = "docker"
      config {
        image = "eclipse-temurin:17.0.5_8-jre"
        command = "java"
		args = ["-jar", "/local/testdriver-2.0.5-SNAPSHOT.jar"]
        mount {
          type = "bind"
          target = "/root/soapui-settings.xml"
          source = "secrets/soapui-settings.xml"
          readonly = false
          bind_options {
            propagation = "rshared"
          }
        }
      }
      template {
        destination = "secrets/file.env"
        env = true
        data = <<EOT
JAVA_TOOL_OPTIONS="-Xmx1638m -Xms512m -Duser.timezone=Europe/Paris -Dfile.encoding=UTF8 {{with secret "platines/tomcat"}}-Dhttp.proxyHost={{.Data.data.proxy_host}} -Dhttp.proxyPort={{.Data.data.proxy_port}} -Dhttps.proxyHost={{.Data.data.proxy_host}} -Dhttps.proxyPort={{.Data.data.proxy_port}}{{end}}"
EOT
      }
      template {
         change_mode = "restart"
         destination = "secrets/soapui-settings.xml"
         data = <<EOT
<?xml version="1.0" encoding="UTF-8"?>
<con:soapui-settings xmlns:con="http://eviware.com/soapui/config">
  <con:setting id="ProxySettings@autoProxy">true</con:setting>
  <con:setting id="ProxySettings@enableProxy">true</con:setting>
{{with secret "platines/tomcat"}}
  <con:setting id="ProxySettings@host">{{.Data.data.proxy_host}}</con:setting>
  <con:setting id="ProxySettings@port">{{.Data.data.proxy_port}}</con:setting>
{{end}}
  <con:setting id="WsdlSettings@strict-schema-types">false</con:setting>
</con:soapui-settings>
EOT
      }
      resources {
        cpu    = 500
        memory = 2048
      }
    }
  }
}

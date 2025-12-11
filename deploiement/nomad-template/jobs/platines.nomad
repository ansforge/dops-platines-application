job "platines" {
	datacenters = ["ror_qualif2"]
	type = "service"
	update {
		stagger = "30s"
		max_parallel = 1
	}

	group "db-servers" {
		count = "1"
		ephemeral_disk {
			migrate = true
			size = 2048
			sticky = true
		}
		task "mariadb" {
		
			driver = "docker"
				config {
					image = "rhscl/mariadb-101-rhel7:latest"
					volumes = ["local:/var/lib/mysql/data"]
					port_map {
						db = 3306
					}
				}
			env {
				"MYSQL_USER"     = "user1_ror"
				"MYSQL_PASSWORD" = "password"
				"MYSQL_DATABASE" = "db_ror"
			}
			resources {
				cpu = 500  
				memory = 1024
				network {
					port "db" {}
					mbits = 20
				}
			}
			service {
				name = "mariadb-server"
				tags = ["asipsante.platines"]
				port = "db"
				check{
					type = "tcp"
					port = "db"
					name = "check_mysql"
					interval = "40s"
					timeout = "1s"
				}
			}
		}
	}

	group "apps-servers" {
		count = "1"
		ephemeral_disk {
			size = 200
		}
		task "tomcat" {
		
			artifact {
				source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/asip-snapshots/fr/asipsante/ror/platines-back-web/1.0.1-SNAPSHOT/platines-back-web-1.0.1-SNAPSHOT.war"
				mode = "file"
				destination = "local/platines-api.war"
			}
			artifact {
				source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/asip-snapshots/fr/asipsante/ror/platines/1.0.2-SNAPSHOT/platines-1.0.2-SNAPSHOT.war"
				mode = "file"
				destination = "local/platines.war"
			}
			artifact {
				source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/nomad-tmpl/fr/asipsante/ror/mock.nomad.json"
				mode = "file"
				destination = "local/mock.nomad.json"
			}
			
			driver = "docker"
				config {
					image = "asipsante/tomcat7:latest"
					volumes = ["local:/opt/webserver/webapps"]
					port_map {
						http = 8080
					}
				}
				
			env	{
				TOMCAT_ADDR="${NOMAD_ADDR_http}"
			}
			
			template {
				data = <<EOH
				GENERIC_PASSWORD_SSL="BZPJtgI8Cpdarm8z1iSZiA=="
				PROTOCOL_API="http"
				SECURE_PROTOCOL_API="https"
				PORT_API=":8080"
				CONTEXT_API="platines-api"
				API_FOLDER="/deployments/platines-api"
				API_DOMAIN="platines.qualif1.henix.asipsante.fr"
				MOCK_SERVICE_DOMAIN="mock.platines.henix.asipsante.fr"
				SERVER_API="localhost"
				PROXY_HOST="10.0.49.163"
				PROXY_PORT="3128"
				SMTP_HOST="smtpout.francenet.fr"
				SMTP_PORT="25"
				SMTP_PROXY_HOST=""
				SMTP_PROXY_PORT=""
				DB_SERVICE_PREFIX_MAPPING="MARIADB"
				SIZE_MEMORY_JVM="256M"
				JOB_MOCK_SERVICE_TMPL="/deployments/mock.nomad.json"
				DB_SERVICE_PREFIX_MAPPING="MARIADB"
				{{range service "http.nomad"}}
				NOMAD_API_PORT="{{.Port}}"{{end}}
				{{range service "mariadb-server"}}
				MARIADB_SERVICE_HOST="{{.Address}}"
				MARIADB_SERVICE_PORT="{{.Port}}"{{end}}
				MARIADB_JNDI="jdbc/rorJNDI"
				MARIADB_USERNAME="user1_ror"
				MARIADB_PASSWORD="password"
				MARIADB_DATABASE="db_ror"
				EOH
				destination = "local/env"
				change_mode = "restart"
				env         = true
			}
	
			resources {
				cpu = 500
				memory = 2048
				network {
					port "http" {}
					mbits = 10
				}
			}
			service {
				name = "tomcat-server"
				tags = ["asipsante.platines"]
				port = "http"
				check {
					type = "http"
					port = "http"
					path = "/platines"
					name = "check_tomcat"
					interval = "40s"
					timeout = "10s"
				}
			}
		}
	}


	group "front-servers" {
		count = "1"
		task "haproxy" {
		
				driver = "docker"
				config {
					image = "haproxytech/haproxy:latest"
					volumes = ["local:/etc/haproxy"]
					port_map {
						ha_https = 8443
						ha_http = 8080
					}
				}
				
				env {
					"PUBLIC_HOSTNAME" = "platines.qualif1.henix.asipsante.fr"
					"MOCK_SERVICE_DOMAIN" = "mock.platines.henix.asipsante.fr"
				}
				
				template {
data = <<EOH
-----BEGIN CERTIFICATE-----
MIIIvjCCBqagAwIBAgIQHV3iXj3BS94FEdKtUv8mCTANBgkqhkiG9w0BAQsFADCB
ijELMAkGA1UEBhMCRlIxEzARBgNVBAoMCkFTSVAtU0FOVEUxFzAVBgNVBAsMDjAw
MDIgMTg3NTEyNzUxMRcwFQYDVQQLDA5JR0MtU0FOVEUgVEVTVDE0MDIGA1UEAwwr
VEVTVCBBQyBJR0MtU0FOVEUgRUxFTUVOVEFJUkUgT1JHQU5JU0FUSU9OUzAeFw0x
NzA3MzEwOTIxNTVaFw0yMDA3MzEwOTIxNTVaMH8xCzAJBgNVBAYTAkZSMRMwEQYD
VQQIDApQYXJpcyAoNzUpMRMwEQYDVQQKDApBU0lQLVNBTlRFMRgwFgYDVQQLDA8z
MTg3NTEyNzUxMDAwMjAxLDAqBgNVBAMMI3BsYXRpbmVzLnF1YWxpZjEuaGVuaXgu
YXNpcHNhbnRlLmZyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApIWo
zBSm+skih03uQJIC3cnE/r1uvBWYNSiAGFeEfRYUu8XVRRCiZJHne12AhYLu020U
/TmtdzrK6OYl+YCQeekeCHuOU+Sajdv9a/jQNmkt/Ggq7gawxsbaIAr6VOb/HaAi
Yr90w5RYGi8RyJFO/9yIn0rGVMWmMHuumetEpXY00YFu7mDjtUr8WWGdHoCLFJQj
YxU4SiZ4vmbxPCR3tE5e7dvjB1FNgf9lCHPHTQYR03+Bw6jWb8zOQhc45J0dMuUc
LAPCqDjr+1NI4zi2uPRdIHeW1s4eETytHtEUZQaWYOzz6fjtUf+z3PjMMyqDN1s3
x+NnI+EL9P+BZwmdqQIDAQABo4IEKDCCBCQwCQYDVR0TBAIwADAdBgNVHQ4EFgQU
5HzbHjGlNxdEJ+vvVOx+1EnTji0wHwYDVR0jBBgwFoAUIS6EfTse9RLeUaIhXK6T
qO9b4/UwDgYDVR0PAQH/BAQDAgWgMFMGA1UdIARMMEowSAYNKoF6AYFVAQcCAQEB
ATA3MDUGCCsGAQUFBwIBFilodHRwOi8vaWdjLXNhbnRlLmVzYW50ZS5nb3V2LmZy
L1BDJTIwVEVTVDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwUwYDVR0R
BEwwSoIjcGxhdGluZXMucXVhbGlmMS5oZW5peC5hc2lwc2FudGUuZnKCI3BsYXRp
bmVzLnF1YWxpZjIuaGVuaXguYXNpcHNhbnRlLmZyMIIBVQYDVR0fBIIBTDCCAUgw
PaA7oDmGN2h0dHA6Ly9pZ2Mtc2FudGUuZXNhbnRlLmdvdXYuZnIvQ1JML0FDSS1F
TC1PUkctVEVTVC5jcmwwggEFoIIBAaCB/oaB+2xkYXA6Ly9hbm51YWlyZS1pZ2Mu
ZXNhbnRlLmdvdXYuZnIvY249VEVTVCUyMEFDJTIwSUdDLVNBTlRFJTIwRUxFTUVO
VEFJUkUlMjBPUkdBTklTQVRJT05TLG91PVRFU1QlMjBBQyUyMFJBQ0lORSUyMElH
Qy1TQU5URSUyMEVMRU1FTlRBSVJFLG91PUlHQy1TQU5URSUyMFRFU1Qsb3U9MDAw
MiUyMDE4NzUxMjc1MSxvPUFTSVAtU0FOVEUsYz1GUj9jZXJ0aWZpY2F0ZXJldm9j
YXRpb25saXN0O2JpbmFyeT9iYXNlP29iamVjdENsYXNzPXBraUNBMIIBDgYDVR0u
BIIBBTCCAQEwgf6ggfuggfiGgfVsZGFwOi8vYW5udWFpcmUtaWdjLmVzYW50ZS5n
b3V2LmZyL2NuPVRFU1QlMjBBQyUyMElHQy1TQU5URSUyMEVMRU1FTlRBSVJFJTIw
T1JHQU5JU0FUSU9OUyxvdT1URVNUJTIwQUMlMjBSQUNJTkUlMjBJR0MtU0FOVEUl
MjBFTEVNRU5UQUlSRSxvdT1JR0MtU0FOVEUlMjBURVNULG91PTAwMDIlMjAxODc1
MTI3NTEsbz1BU0lQLVNBTlRFLGM9RlI/ZGVsdGFyZXZvY2F0aW9ubGlzdDtiaW5h
cnk/YmFzZT9vYmplY3RDbGFzcz1wa2lDQTCBgQYIKwYBBQUHAQEEdTBzMCYGCCsG
AQUFBzABhhpodHRwOi8vb2NzcC5lc2FudGUuZ291di5mcjBJBggrBgEFBQcwAoY9
aHR0cDovL2lnYy1zYW50ZS5lc2FudGUuZ291di5mci9BQyUyMFRFU1QvQUNJLUVM
LU9SRy1URVNULmNlcjAPBggqgXoBRwECBQQDBAGDMA0GCSqGSIb3DQEBCwUAA4IC
AQANg4eI/ggXmJrMPlNq+wtL7jgd7e7E3sGVRtNShvHfHx0oUAdvwXg0lBqShc+b
cHviWEEmU9ySC9vlUb6Jfn+nnGANvCCfbEDWalIozxYO+G7HElyr0ob87Bp4Nbu3
EQmT29S5HyVEh1PqYNJ/cnaDcyuB9Q/avLvzGuXFTWjlA/G0EzNRakZZ7TTR+cL/
hY2E3IJ1jI4CcSpiM0I5JmSutQUVTek8VKC7GVpkFBnAIdndrx0AKfPWizLgKqID
DVrcIvHyMg3PqTWWrew1qCUib8pVtgrcSivQbM+Q4Rff7KPzPc8r9I8sQ1RyT3LQ
rsyNZhtIt7jOfc434z2DbGPhWLHqWF11WR3LAKemlMPqypc8+/s2z6UAcrXfW7wB
/GqMgFsTueNMNV+rTAZqmd43bi0UxflJHNVlatUL/1yT0ffYzVNGblCTS61zTlue
U7eR5S/v3IF+ObbDN25yhk9G1X0eIGYIalm4BSW+e5x6Xf1SIjECgzzG3hV6/wmE
xaFJlnLFj+dqdOqpCpPzh4FO28zCXAnUbKmXWz/B0hjxhDOQVKFphYOtS9ANipmr
kxV+HJKM120PXFhMp4nupvTGvtyuhDPA+/lbnO+Hik14FlLP6EunbbMXhQlvtauf
CfDrFvP56M+paogAqU7T51fMQYdsGzG4/j2ZV8vEeTpI+w==
-----END CERTIFICATE-----
-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEApIWozBSm+skih03uQJIC3cnE/r1uvBWYNSiAGFeEfRYUu8XV
RRCiZJHne12AhYLu020U/TmtdzrK6OYl+YCQeekeCHuOU+Sajdv9a/jQNmkt/Ggq
7gawxsbaIAr6VOb/HaAiYr90w5RYGi8RyJFO/9yIn0rGVMWmMHuumetEpXY00YFu
7mDjtUr8WWGdHoCLFJQjYxU4SiZ4vmbxPCR3tE5e7dvjB1FNgf9lCHPHTQYR03+B
w6jWb8zOQhc45J0dMuUcLAPCqDjr+1NI4zi2uPRdIHeW1s4eETytHtEUZQaWYOzz
6fjtUf+z3PjMMyqDN1s3x+NnI+EL9P+BZwmdqQIDAQABAoIBABc8FyAnRIPDdyBj
taOh20zGS49sYmCmrhXHz/tgFMJzpV71bA6ycDTC1E28YUO5v4/VZnLyu9hMUc69
fhxz9zN4ZDjvdiTL8rXZHTur6n/cr9VnQrTV9g5ElBaVZlAqRVpCPMj0YAGlCCI+
hoTsXJtO4m6YqVTciwFq3jg3nTy+sGkMrfmLl8dPiY88+ms3ztQTDwtxxXNNGncj
yDDr5dbwFsGCW/ZSttFB8atWMiumcj5HB81/0VlE8gWhp8jrnaVWWy4TNqMR00ET
G5H6OsqEajr82ob7VWpenvv2oIcI+Jp5lQCWJgcH+iJdvCWperhXQ4epyleBIhIJ
0XE6ZrkCgYEAxWEHx9/hygzyFo9cb6OBQnvjYExAonUWr5OnjXupMGOSWgQp1KvU
pU4k58QTiVnQHS9XDLTZPw51fHAkthALeDPv/+Y0wfPfYl2TPx5Cq0iYKLUKfYES
FkDh2/AVxyekXa7e8Y2f8oS7dNH+Yt5PG/MvmGRFxfJ0F564fPYdbr0CgYEA1WJ6
JlaTFsGMxq3AXviiCwQ0fGDWeBGl0OCPCDxWTgb7q+qlhvlnBg0U8vodWRL87Nzh
KdLs4vv9Un7huWZglstRT991ZRJvBRNxqWb7B513eExOpU8Zodeu9eXYtCfBEzDs
J6saYjqBr8lG/FRM5HiEFJVTCFcK+DReyn5ln10CgYAmcEYOo8gaDDldMWAkEaiL
pQrb5x2zRbA8UzoPMKT7WnjW9PachzYrVzOSuzm+xvHjrSN7wwCH6i3xJPnRRwCk
0SCNKmzd1LuNMYIKib8cElAnpVI8rNUz5kDWD0R+e3bkE7kxO46JsVW8Y2TyJi8r
10lOmUCJUzVOoEd3doq21QKBgQC6Rb8P3J+GS+uSPHcj8LO0HjWbJgXSxsUTNBRo
QhOtBZeJGcrpqqnutFjHSuN3sIWuV5sPIZu9AfC/pRdy+xXyTQZsTUtzscXMjo64
LLeSjDmQgDxfPk/H4jpPWhf0HIgFMr3UWx8rBJvVUkVvXD/LahESzs+vBh0DUuQ0
rGYV2QKBgDFmqGfR03dEyxBb2bwvsG7D3pYsfJYgH1Ro1F6EvgIbnudcQaQq5Wqh
s7FSsivT3WIOIDIelfXHyJo77f91E6rVXbHaPhINiWCxnN5owo+r76Ekcxd9vsPZ
Q5exaWDEPT9YEPr959mwqgOU4NQwkUj1yBhUnR+ay2uwsH8X8tSC
-----END RSA PRIVATE KEY-----
EOH
				destination = "local/platines.pem"
			}

			artifact {
				source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/nomad-tmpl/fr/asipsante/platines/1.0.0/haproxy.tmpl"
				destination = "/tmp"
			}

			template {
				source = "tmp/haproxy.tmpl"
				destination = "local/haproxy.cfg"
				change_mode = "restart"
			}
			
			resources {
				cpu = 500
				memory = 1024
				network {
					port "ha_https" {static = "8443"}
					port "ha_http"  {static = "8080"}
					mbits = 10
				}
			}
			
			service {
				name = "haproxy"
				tags = ["asipsante.platines"]
				port = "ha_https"
				check {
					type = "tcp"
					port = "ha_https"
					name = "check_haproxy"
					interval = "40s"
					timeout = "1s"
				}
			}
		}
	}
}

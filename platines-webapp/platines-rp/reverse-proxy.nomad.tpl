/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
job "${nomad_namespace}-rp" {
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
  group "reverse-proxy" {
    count = "1"

    restart {
      attempts = 3
      delay    = "60s"
      interval = "1h"
      mode     = "fail"
    }

    network {
      port "https" { to = 8443 }
      port "http" { to = 8080 }
    }

    task "haproxy" {
      driver = "docker"
      config {
        image   = "${image}:${tag}"
        volumes = ["local/haproxy.cfg:/usr/local/etc/haproxy/haproxy.cfg"]
        ports   = ["https", "http"]
      }

      template {
        data        = <<EOH
{{with secret "platines/haproxy"}}{{ .Data.data.certificate }}
{{ .Data.data.private_key }}{{end}}
EOH
        destination = "secrets/platines.pem"
      }

      template {
        data        = <<EOH
PUBLIC_HOSTNAME="{{with secret "platines/front"}}{{.Data.data.platines_public_hostname}}"
ALLOWED_IPS="{{.Data.data.platines_allowed_ips}}{{end}}"
EOH
        destination = "local/file.env"
        env         = true
      }

      template {
        data        = <<EOH
global
    maxconn                     4096
    log                         stdout format raw  local0  info
    pidfile                     /var/lib/haproxy/haproxy.pid
    tune.ssl.default-dh-param   2048
    ssl-default-bind-options    no-tls-tickets ssl-min-ver TLSv1.2
    ssl-default-bind-ciphers    ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA384:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA256

defaults
    mode                        http
    log                         global
    option                      httplog
    option                	    logasap
    option                      dontlognull
    option                      http-server-close
    no option                   socket-stats
        retries                 3
        timeout http-request    10s
        timeout queue           1m
        timeout connect         10s
        timeout client          1m
        timeout server          1m
        timeout http-keep-alive 10s
        maxconn                 3000

#---------------------------------------------------------------------
frontend        main
        log                       global
        option                    httplog
        option                    forwardfor         # except 127.0.0.0/8
        bind *:8080
        bind *:8443

# -- Active HSTS pour un an & pour les sous-domaines aussi
        http-response add-header Strict-Transport-Security max-age=31536000;includeSubDomains
        http-response add-header Referrer-Policy no-referrer-when-downgrade

# -- Contrôle de l'url
#        acl is_authorized path_reg /\/(|assets|secure|insecure|session).*

# -- Redirection vers https désactivée (SSL terminé par le LB Scaleway)
#        redirect scheme https if !{ ssl_fc }

#        use_backend platines if is_authorized

#        default_backend         cosmos
        default_backend platines
#---------------------------------------------------------------------
backend platines
        log                     global
        {{range service "${nomad_namespace}-webapp" }}
        server  {{ .Node }}     {{.Address}}:{{.Port}}{{end}}
#---------------------------------------------------------------------
backend cosmos
        log                     global
        http-request            deny if TRUE
#---------------------------------------------------------------------
EOH
        destination = "local/haproxy.cfg"
        change_mode = "restart"
      }

      resources {
        cpu    = 200
        memory = 512
      }
      service {
        name = "$${NOMAD_JOB_NAME}-haproxy"
        tags = ["urlprefix-$${PUBLIC_HOSTNAME}/ allow=ip:$${ALLOWED_IPS}"]
        port = "https"
        check {
          type     = "tcp"
          port     = "https"
          name     = "check_haproxy"
          interval = "40s"
          timeout  = "1s"
        }
      }
    }
  }
}

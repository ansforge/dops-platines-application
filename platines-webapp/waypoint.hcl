/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
project = "${workspace.name}/platines"

# Labels can be specified for organizational purposes.
labels = {
  "domaine" = "platines"
}

runner {
  enabled = true
  profile = "platines-${workspace.name}"
  data_source "git" {
    url = "https://rhodecode.proxy-dev-forge.asip.hst.fluxus.net/ApplicationsANS/Transverse/platines/application/platines-webapp.git"
    ref = "${workspace.name}"
  }
  poll {
    enabled = false
  }
}

# MariaDB
app "database" {
  build {
    use "docker-ref" {
      image              = var.database_image
      tag                = var.database_tag
    }
  }
  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-db/database.nomad.tpl", {
	    image           = var.database_image
		tag             = var.database_tag
        nomad_namespace = var.nomad_namespace
        datacenter      = var.datacenter
      })
    }
  }
}

# Webapp application.
app "webapp" {
  build {
    use "docker-ref" {
      image              = var.webapp_image
      tag                = var.webapp_tag
    }
  }

  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-back/platines.nomad.tpl", {
	  	image  = var.webapp_image
		tag    = var.webapp_tag
        datacenter      = var.datacenter
        nomad_namespace = var.nomad_namespace
		artifacts_repository_url = var.artifacts_repository_url
		artifacts_version = var.artifacts_version
      })
    }
  }
}
# HAProxy
app "reverse-proxy" {
  build {
    use "docker-ref" {
      image              = var.rp_image
      tag                = var.rp_tag
    }
  }
  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/platines-rp/reverse-proxy.nomad.tpl", {
	    image  = var.rp_image
		tag    = var.rp_tag
        nomad_namespace = var.nomad_namespace
        datacenter      = var.datacenter
      })
    }
  }
}

variable "artifacts_repository_url" {
  type    = string
  default = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/repo-snapshots"
  env     = ["ARTIFACTS_REPOSITORY_URL"]
}

variable "artifacts_version" {
  type    = string
  default = "2.0.4-SNAPSHOT"
}

variable "datacenter" {
  type    = string
  default = ""
  env     = ["NOMAD_DATACENTER"]
}

variable "nomad_namespace" {
  type    = string
  default = ""
  env     = ["NOMAD_NAMESPACE"]
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

variable "proxy_address" {
  type    = string
  default = ""
}

variable "database_image" {
  type    = string
  default = "mariadb"
}

variable "database_tag" {
  type    = string
  default = "10.7"
}

variable "rp_image" {
  type    = string
  default = "haproxy"
}

variable "rp_tag" {
  type    = string
  default = "lts"
}

variable "webapp_image" {
  type = string
  default = "openjdk"
}

variable "webapp_tag" {
  type = string
  default = "17-slim-buster"
}

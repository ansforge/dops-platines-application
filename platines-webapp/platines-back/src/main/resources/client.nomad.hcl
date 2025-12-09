/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
job "" {
  datacenters = []
  type = "batch"
  group "" {
    task "readyapi-runner" {
	  artifact {
		source = "http://repo.proxy-dev-forge.asip.hst.fluxus.net/artifactory/@platines.artifact.repository@/fr/asipsante/platines/testdriver/@project.version@/testdriver-@project.version@-dist.zip"
      }
      driver = "docker"
      config {
        image = "eclipse-temurin:17.0.5_8-jre"
        command = "java"
        args = ["-jar", "/local/testdriver-@project.version@.jar"]
        work_dir = "/local/"
      }

      resources {
        cpu    = 500
        memory = 2048
      }
    }
  }
}

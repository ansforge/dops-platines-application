#
# = Class: platines::conf
#
#   Classe qui configure les différentes politiques
#
# === Parameters
#
# === Variables
#
# === Authors
#
# ANS
#
class platines::conf inherits platines {

  # Création des politiques d'accès à vault
  exec { "setup_policies ${platines::platines_job_name}":
    command     => "${platines::nomad_job_dir}/${platines::setup_policies_script}",
    subscribe   => File["${platines::nomad_job_dir}/${platines::setup_policies_script}"],
    refreshonly => true,
  }
}

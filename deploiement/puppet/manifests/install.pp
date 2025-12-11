#
# = Class: platines::install
#
#   Cette classe dépose le job et met à jour les paramètres par le template
#
# === Parameters
#
# === Variables
#
# === Authors
#
# ANS
#
class platines::install inherits platines {

  # Copie du job de la DB platines à partir du template avec la mise en place des variables
  file { "${platines::nomad_job_dir}/${platines::platines_db_job_filename}":
    ensure  => present,
    content => template('platines/db-platines.nomad.erb'),
    mode    => '0644',
  }

  # Copie du job platines à partir du template avec la mise en place des variables
  file { "${platines::nomad_job_dir}/${platines::platines_job_filename}":
    ensure  => present,
    content => template('platines/platines.nomad.erb'),
    mode    => '0644',
  }

  # Vault policy
  file { "${platines::nomad_job_dir}/${platines::setup_policies_script}":
    ensure  => present,
    content => template('platines/setup_policies.sh.erb'),
    mode    => '0755',
  }
}

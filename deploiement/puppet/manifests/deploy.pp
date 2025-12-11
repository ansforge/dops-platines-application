#
# = Class: platines::deploy
#
#   Classe qui exécute le job nomad applicatif (template platines.nomad.erb) 
#
# === Parameters
#
# === Variables
#
# === Authors
#
# Asip Santé
#
class platines::deploy inherits platines {

  # Commande "plan" lancée avant le run du job de la DB platines
  exec { "nomad plan ${platines::platines_db_job_filename}":
    cwd         => $platines::nomad_job_dir,
    path        => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    subscribe   => File["${platines::nomad_job_dir}/${platines::platines_db_job_filename}"],
    returns     => 1,
    onlyif      => ["test $(nomad plan ${platines::platines_db_job_filename} >/dev/null 2>&1;echo $?) -eq 1",
      "test $(nomad plan ${platines::platines_db_job_filename} | grep -ic warning) -lt 1"
    ],
    notify      => Exec["debug_trace ${platines::platines_db_job_filename}"],
    refreshonly => true,
  }

  # Commande qui permet de vérifier que lorsque des warnings apparaisent lors du "plan",
  # les logs apparaissent dans la console
  exec { "debug_trace ${platines::platines_db_job_filename}":
    cwd       => $platines::nomad_job_dir,
    command   => "nomad plan ${platines::platines_db_job_filename}",
    path      => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    returns   => 1,
    logoutput => true,
    onlyif    => "test $(nomad plan ${platines::platines_db_job_filename} | grep -ic warning) -gt 0",
  }

  # lancement du job nomad applicatif après sa création
  exec { "nomad run ${platines::platines_db_job_filename}":
    cwd         => $platines::nomad_job_dir,
    path        => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    subscribe   => Exec["nomad plan ${platines::platines_db_job_filename}"],
    refreshonly => true,
  }

# Commande "plan" lancée avant le run du job platines
  ->  exec { "nomad plan ${platines::platines_job_filename}":
    cwd         => $platines::nomad_job_dir,
    path        => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    subscribe   => File["${platines::nomad_job_dir}/${platines::platines_job_filename}"],
    returns     => 1,
    onlyif      => ["test $(nomad plan ${platines::platines_job_filename} >/dev/null 2>&1;echo $?) -eq 1",
      "test $(nomad plan ${platines::platines_job_filename} | grep -ic warning) -lt 1"
    ],
    notify      => Exec["debug_trace ${platines::platines_job_filename}"],
    refreshonly => true,
  }

# Commande qui permet de vérifier que lorsque des warnings apparaisent lors du "plan",
# les logs apparaissent dans la console
  exec { "debug_trace ${platines::platines_job_filename}":
    cwd       => $platines::nomad_job_dir,
    command   => "nomad plan ${platines::platines_job_filename}",
    path      => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    returns   => 1,
    logoutput => true,
    onlyif    => "test $(nomad plan ${platines::platines_job_filename} | grep -ic warning) -gt 0",
  }

# lancement du job nomad applicatif après sa création
  exec { "nomad run ${platines::platines_job_filename}":
    cwd         => $platines::nomad_job_dir,
    path        => ['/usr/bin', '/usr/sbin', '/usr/local/bin'],
    subscribe   => Exec["nomad plan ${platines::platines_job_filename}"],
    refreshonly => true,
  }
}

#
# = Class: platines::init
#
# Classe qui ordonnance les différentes classes du module platines.
#
# == Parameters
#
# See params.pp.
#
class platines () inherits platines::params {

  include platines::install
  include platines::conf
  include platines::deploy

  Class['platines::install']
  -> Class['platines::conf']
  -> Class['platines::deploy']

}

# = Class: params
#
# Cette classe gère les paramètres du module platines.
# Pour les descriptions des paramètres lookup, se référer au fichier README
#
class platines::params {

  #########################################
  # global environment yaml parameters    #
  #########################################
  $url_artifactory = lookup(global::url_artifactory, String, 'first')

  $proxy_host = lookup(global::proxy_host, String, 'first', '')
  $proxy_port = lookup(global::proxy_port, String, 'first', '')
  $smtp_host = lookup(global::smtp_host, String, 'first', 'localhost')
  $smtp_port = lookup(global::smtp_port, String, 'first', '')
  $smtp_proxy_host = lookup(global::smtp_proxy_host, String, 'first', '')
  $smtp_proxy_port = lookup(global::smtp_proxy_port, String, 'first', '')
  $logstash_host = lookup(global::logstash_host, String, 'first')

  #########################################
  # platines environment yaml parameters  #
  #########################################

  $platines_job_name = lookup(platines_job_name, String, 'first', 'platines')
  $platines_mysql_user = lookup(platines_mysql_user, String, 'first')
  $platines_mysql_database = lookup(platines_mysql_database, String, 'first')
  $platines_public_hostname = lookup(platines_public_hostname, String, 'first')
  $platines_mock_service_domain = lookup(platines_mock_service_domain, String, 'first')
  $platines_distrib_version = lookup(platines_distrib_version, String, 'first')
  $platines_front_distrib_version = lookup(platines_front_distrib_version, String, 'first')
  $platines_mock_distrib_version = lookup(platines_front_distrib_version, String, 'first')
  $platines_allowed_ips = lookup(platines_allowed_ips, Array[String], 'first')
  $mariadb_image_rhel = lookup(mariadb_image_rhel, String, 'first', 'latest')
  $mariadb_size = lookup(mariadb_size, String, 'first', '5')

  $platines_version_nomad_tmpl = lookup(platines_version_nomad_tmpl, String, 'first', 'latest')
  $platines_repo = lookup(platines_repo, String, 'first', 'repo-releases')
  $platines_repo_dist = lookup(platines_repo_dist, String, 'first', 'repo-distributions')

  $platines_dbserver_mem_size = lookup(platines_dbserver_mem_size, String, 'first', '1024')
  $platines_appserver_mem_size = lookup(platines_appserver_mem_size, String, 'first', '3072')
  $platines_rpserver_mem_size = lookup(platines_rpserver_mem_size, String, 'first', '512')

  $platines_java_initial_mem_ratio = lookup(platines_java_initial_mem_ratio, String, 'first', '100')
  $platines_java_max_mem_ratio = lookup(platines_java_max_mem_ratio, String, 'first', '80')
  $platines_gc_max_metaspace_size = lookup(platines_gc_max_metaspace_size, String, 'first', '256')

  ########################
  # platines parameters  #
  ########################

  $repo_nomad_tmpl_path = 'artifactory/nomad-tmpl/fr/asipsante/platines'
  $job_repository_tmpl = "${repo_nomad_tmpl_path}/${platines_version_nomad_tmpl}"

  $repo_appli_path = "artifactory/$platines_repo/fr/asipsante/platines"
  $repo_archives_path = "artifactory/$platines_repo_dist/fr/asipsante/platines"
  $back_artifact = "${repo_appli_path}/platines-back/${platines_distrib_version}/platines-back-${platines_distrib_version}.war"
  $front_artifact = "${repo_appli_path}/platines-front/${platines_front_distrib_version}/platines-front-${platines_front_distrib_version}.war"
  $mockws_artifact = "${repo_archives_path}/platines-mockws/${platines_mock_distrib_version}/platines-mockws-${platines_mock_distrib_version}-webdir.tar.gz"

  ###############################
  # Nomad parameters            #
  ###############################

  $nomad_job_dir = '/opt/nomad/jobs'
  $platines_job_filename = "${platines_job_name}.nomad"
  $platines_db_job_filename = "db_${platines_job_name}.nomad"
  $setup_policies_script = "${platines_job_name}_setup.sh"
}

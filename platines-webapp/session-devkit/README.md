# mini-Nomad

## TOC

### [Rôle](#role)

### [Comment déployer le mini-nomad](#howto-deploy)

#### [Cas d'un poste linux](#deploy-linux)

#### [Cas d'un poste windows](#deploy-windows)

### [Comment démarrer](#howto-start)

#### [Cas linux](#start-linux)
#### [Cas windows](#start-windows)

<a name="role" />

## Rôle

### Objectifs
Le but de ce module est de mettre en place un serveur nomad minimal pour travailler en développement sur l'exécution de sessions de test.

### Limites
Le cluser mis en place contient le strict minimum nécessaire pour tester les exécutions de sessions utilisateur. Il ne permet PAS de tester le déploiment de platines.

<a name="howto-deploy" />

## Comment déployer le mini-nomad

<a name="deploy-linux"/>

### Cas d'un poste linux

#### Mode de déploiment
Le mini-nomad peut être déployé nativement sur le poste.

#### Pré-requis

* Docker doit être installé.
  Se reporter [au site de docker](https://docs.docker.com/desktop/install/linux-install/) pour l'installation de docker.

* unzip doit être installé.
  Se reporter à la documentation de la distribution (pour debian, `apt install unzip`)


#### Procédure d'installation

Exécuter les commandes suivantes :

```shell
# Lancement de vault
docker run --name "vault-dev" --publish 127.0.0.1:8200:8200 --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' hashicorp/vault:1.10.0

mkdir -p ~/.config/nomad
cat <<EOF> ~/.config/nomad/config.hcl
{
    "allowed_policies": "nomad-server,platines",
    "token_explicit_max_ttl": 0,
    "name": "nomad-cluster",
    "orphan": true,
    "token_period": 259200,
    "renewable": true,
}
EOF

mkdir -p ~/.local/bin
wget -O ~/.local/bin/nomad_1.2.6_linux_amd64.zip https://releases.hashicorp.com/nomad/1.2.6/nomad_1.2.6_linux_amd64.zip
cd ~/.local/bin
unzip nomad_1.2.6_linux_amd64.zip
mv ./nomad ./nomad-1.2.6
wget -O ~/.local/bin/consul_1.11.4_linux_amd64.zip https://releases.hashicorp.com/consul/1.11.4/consul_1.11.4_linux_amd64.zip
unzip consul_1.11.4_linux_amd64.zip
mv ~/.local/bin/consul ~/.local/bin/consul-1.11.4

${HOME}/.local/bin/consul-1.11.4 agent -dev
sudo /bin/bash -c "VAULT_TOKEN=myroot ${HOME}/.local/bin/nomad-1.2.6 agent -dev -vault-enabled=true -vault-address=http://localhost:8200 ${HOME}.config/nomad/config.hcl"

docker exec --user vault -ti vault-dev /bin/ash

export VAULT_ADDR=http://127.0.0.1:8200
vault secrets enable -path platines kv-v2
vault kv put platines/tomcat proxy_host=
vault kv patch platines/tomcat proxy_port=
cat <<EOF | vault policy write platines -
path "platines/*" {
  capabilities = ["create", "read", "update", "patch", "delete", "list"]
}
EOF

${HOME}/.local/bin/nomad-1.2.6 namespace apply -description "Namespace pour l'exécution de platines" platines

exit
```

<a name="deploy-windows" />

### Cas d'un poste windows

### Mode de déploiement
Docker linux est indispensable pour réaliser les tests. Dans le cas de windows, il faut donc déployer le mini-nomad dans une machine virtuelle linux.
Ensuite, iol faudra exposer nomad sur un sous-réseau accessible depuis windows pour que l'instance platines de dev puisse y accéder.

### Pré-requis
Le poste windows doit disposer d'un système de machine virtuelles:

* installation d'un gestionnaire comme **virtualbox**
* activation et mise en oeuvre d'**HyperV**

#### Procédure d'installation

Un fois une distribution linux installée sur la machine virtuelle, se reporter à la section [Cas d'un poste linux](#poste-linux)


### Redémarrage

<a name="start-linux" />

#### Cas linux

Exécuter les commandes suivantes :

```shell
docker start vault-dev

${HOME}/.local/bin/consul-1.11.4 agent -dev
sudo /bin/bash -c "VAULT_TOKEN=myroot ${HOME}/.local/bin/nomad-1.2.6 agent -dev -vault-enabled=true -vault-address=http://localhost:8200 ${HOME}.config/nomad/config.hcl"
```

<a name="start-windows" />

#### Cas windows

* Démarrer la machine virtuelle linux
* se logger dans la VM linux
* se reporter au [cas linux](#start-linux)
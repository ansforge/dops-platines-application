FROM eclipse-temurin:17-jre-jammy

# Arguments de build pour la version et l'URL du repository
ARG ARTIFACTS_VERSION=2.3.8
ARG ARTIFACTS_REPOSITORY_URL=https://repo.esante.gouv.fr/repository/asip-releases

# Copier le certificat CA IGC-Santé
COPY ACI-EL-ORG.crt /usr/local/share/ca-certificates/

# Mettre à jour les certificats CA du système
RUN update-ca-certificates

# Force rebuild - 2026-02-03
# Créer les répertoires nécessaires et télécharger le WAR depuis le repository Maven
RUN mkdir -p /usr/app/workspace && \
    apt-get update && apt-get install -y wget ca-certificates && \
    wget -O /usr/app/platines-back.war \
    "${ARTIFACTS_REPOSITORY_URL}/fr/ans/platines/platines-back/${ARTIFACTS_VERSION}/platines-back-${ARTIFACTS_VERSION}.war" && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Créer le script d'entrypoint qui ajoute l'entrée hosts puis lance l'application
RUN echo '#!/bin/bash' > /usr/app/entrypoint.sh && \
    echo 'echo "172.16.0.4 repo.proxy-dev-forge.asip.hst.fluxus.net" >> /etc/hosts' >> /usr/app/entrypoint.sh && \
    echo 'exec su daemon -s /bin/bash -c "java -jar /usr/app/platines-back.war"' >> /usr/app/entrypoint.sh && \
    chmod +x /usr/app/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/app/entrypoint.sh"]

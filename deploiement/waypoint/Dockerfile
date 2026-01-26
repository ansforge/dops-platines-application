FROM eclipse-temurin:17-jre-jammy

# Arguments de build pour la version et l'URL du repository
ARG ARTIFACTS_VERSION=2.3.2
ARG ARTIFACTS_REPOSITORY_URL=https://repo.esante.gouv.fr/repository/asip-releases

# Créer les répertoires nécessaires et télécharger le WAR depuis le repository Maven
RUN mkdir -p /usr/app/workspace /usr/app/truststore && \
    apt-get update && apt-get install -y wget && \
    wget --no-check-certificate -O /usr/app/platines-back.war \
    "${ARTIFACTS_REPOSITORY_URL}/fr/ans/platines/platines-back/${ARTIFACTS_VERSION}/platines-back-${ARTIFACTS_VERSION}.war" && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copier le truststore IGC-Santé depuis le workspace local
COPY truststore.jks /usr/app/truststore/truststore.jks

USER daemon
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/app/platines-back.war"]

FROM eclipse-temurin:17-jre-jammy

# Copier le WAR depuis le dépôt local
COPY platines-back-2.3.2.war /usr/app/platines-back.war

# Créer le répertoire workspace
RUN mkdir -p /usr/app/workspace

# Créer le script d'entrypoint qui ajoute l'entrée hosts puis lance l'application
RUN echo '#!/bin/bash' > /usr/app/entrypoint.sh && \
    echo 'echo "172.16.0.4 repo.proxy-dev-forge.asip.hst.fluxus.net" >> /etc/hosts' >> /usr/app/entrypoint.sh && \
    echo 'exec su daemon -s /bin/bash -c "java -jar /usr/app/platines-back.war"' >> /usr/app/entrypoint.sh && \
    chmod +x /usr/app/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/app/entrypoint.sh"]

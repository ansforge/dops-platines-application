FROM eclipse-temurin:17-jre-jammy

# Copier le WAR depuis le dépôt local
COPY platines-back-2.3.2.war /usr/app/platines-back.war

# Créer le répertoire workspace
RUN mkdir -p /usr/app/workspace

USER daemon
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/app/platines-back.war"]

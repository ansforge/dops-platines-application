FROM maven:3.9-eclipse-temurin-17 AS builder

# Copier le code source du workspace root
WORKDIR /build
COPY ../../platines-webapp ./platines-webapp
COPY ../../components ./components

# Compiler le projet
WORKDIR /build/platines-webapp
RUN mvn clean package -DskipTests -Dlicense.skip=true -pl platines-back -am

FROM eclipse-temurin:17-jre-jammy

# Copier le WAR compilé depuis le builder
COPY --from=builder /build/platines-webapp/platines-back/target/platines-back-*.war /usr/app/platines-back.war

RUN mkdir -p /usr/app/workspace

USER daemon
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/app/platines-back.war"]

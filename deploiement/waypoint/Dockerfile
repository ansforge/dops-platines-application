FROM eclipse-temurin:17-jdk-jammy AS builder

# Copier le code source
COPY ../../platines-webapp /build
WORKDIR /build

# Compiler le projet
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

# Copier le WAR compilé depuis le builder
COPY --from=builder /build/platines-back/target/platines-back-*.war /usr/app/platines-back.war

RUN mkdir -p /usr/app/workspace

USER daemon
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/app/platines-back.war"]

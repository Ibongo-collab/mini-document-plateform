# ============================================================
# config-server — build multi-stage
# Contexte de build : la racine du monorepo.
#   docker build -f config-server/Dockerfile -t config-server .
# ============================================================

# ---- STAGE 1 : build ----
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY document-service/pom.xml document-service/pom.xml
COPY config-server/pom.xml config-server/pom.xml

RUN mvn -B dependency:go-offline -pl config-server -am

COPY config-server/src config-server/src

RUN mvn -B -pl config-server -am package -DskipTests

# ---- STAGE 2 : runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/config-server/target/config-server-*.jar app.jar

# Port ecoute par le Config Server (voir SERVER_PORT dans application.yml)
EXPOSE 8888

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================================================
# api-gateway — build multi-stage
# Contexte de build : la racine du monorepo (voir document-service/Dockerfile
# pour l'explication complete du pourquoi).
#   docker build -f api-gateway/Dockerfile -t api-gateway .
# ============================================================

# ---- STAGE 1 : build ----
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY document-service/pom.xml document-service/pom.xml
COPY config-server/pom.xml config-server/pom.xml

RUN mvn -B dependency:go-offline -pl api-gateway -am

COPY api-gateway/src api-gateway/src

RUN mvn -B -pl api-gateway -am package -DskipTests

# ---- STAGE 2 : runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/api-gateway/target/api-gateway-*.jar app.jar

# Port ecoute par la gateway (voir SERVER_PORT dans application.yml)
EXPOSE 8080

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

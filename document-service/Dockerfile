# ============================================================
# document-service — build multi-stage
#
# Contexte de build attendu : la RACINE du monorepo (pas ce dossier).
# Depuis mini-document-platform/, lancer :
#   docker build -f document-service/Dockerfile -t document-service .
#
# Pourquoi la racine et pas document-service/ ? Parce que ce module
# depend du pom.xml PARENT (heritage Maven) : sans lui, `mvn package`
# a l'interieur du conteneur ne saurait pas resoudre les versions
# gerees par le parent (Spring Boot BOM, Spring Cloud BOM, AWS SDK BOM).
# ============================================================

# ---- STAGE 1 : build ----
# Image lourde (contient Maven + JDK complet), utilisee UNIQUEMENT
# pour compiler. Elle ne fait jamais partie de l'image finale.
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# On copie d'abord SEULEMENT les pom.xml (parent + modules).
# Astuce de cache Docker : tant que les pom.xml ne changent pas,
# Docker reutilise le cache de "mvn dependency:go-offline" ci-dessous,
# meme si le code Java change ensuite. Ca accelere enormement les
# rebuilds pendant le developpement.
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY document-service/pom.xml document-service/pom.xml
COPY config-server/pom.xml config-server/pom.xml

# Telecharge toutes les dependances necessaires a document-service
# (et transitive via -am, "also make", au cas ou une dependance
# inter-module existerait un jour).
RUN mvn -B dependency:go-offline -pl document-service -am

# Maintenant seulement, on copie le code source reel du module.
# Si seul le code change (pas les pom.xml), Docker invalide le cache
# a partir d'ici uniquement — le telechargement des dependances
# au-dessus reste en cache.
COPY document-service/src document-service/src

# Build du module document-service uniquement (-pl), avec ses
# modules "amont" necessaires (-am). Tests sautes ici : ils tournent
# deja en CI (etape 9 de la roadmap), pas besoin de les rejouer
# a chaque build d'image.
RUN mvn -B -pl document-service -am package -DskipTests

# ---- STAGE 2 : runtime ----
# Image finale : uniquement un JRE (pas un JDK complet, pas Maven).
# Beaucoup plus legere et avec une surface d'attaque reduite.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# On ne recupere QUE le .jar final depuis le stage builder.
# Tout le reste (sources, cache Maven, JDK complet) disparait :
# il n'a jamais existe dans cette image.
COPY --from=builder /build/document-service/target/document-service-*.jar app.jar

# Port ecoute par Spring Boot (voir SERVER_PORT dans application.yml)
EXPOSE 8081

# Bonne pratique : ne pas lancer le process en root dans le conteneur.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

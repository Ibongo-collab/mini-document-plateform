# Mini Document Platform

Projet de portfolio personnel pedagogique visant a monter en competence
progressivement sur : **Java / Spring Boot, Spring Cloud, Domain-Driven
Design, Architecture Hexagonale, Docker, GitHub Actions, CI/CD, Render, puis
AWS et Kubernetes.**

Ce depot ne contient volontairement que le **squelette** du projet. La
logique metier, la persistance, le stockage S3, le conteneurisation et le
deploiement sont laisses vides ou minimaux, avec des `TODO` explicites, pour
etre implementes par l'auteur du projet.

---

## Presentation

Mini Document Platform est une petite plateforme de gestion de documents
permettant de :

- uploader un document ;
- consulter la liste des documents ;
- consulter les metadonnees d'un document ;
- telecharger un document ;
- supprimer un document.

Un document possede : `id`, `filename`, `size`, `storageKey`, `status`,
`createdAt`.

Le fichier binaire sera stocke a terme dans **AWS S3**, et les metadonnees
dans **PostgreSQL**.

Le projet est concu pour etre deploye en deux temps :

1. **Niveau 1 — Render**, pour apprendre Docker, Docker Compose, GitHub
   Actions, CI/CD, la gestion des secrets et le deploiement Docker sur
   Render.
2. **Niveau 2 — AWS**, en reprenant exactement le meme code, sans modifier
   le domaine ni la logique metier, pour apprendre ECR, EC2, S3, RDS, IAM,
   VPC, CloudWatch, puis EKS / Kubernetes.

---

## Architecture

```text
                         Internet
                            |
                            v
                     +--------------+
                     | api-gateway  |   (Spring Cloud Gateway, WebFlux)
                     +--------------+
                            |
                    /api/documents/**
                            |
                            v
                   +-------------------+
                   | document-service  |   (Spring MVC, hexagonal + DDD)
                   +-------------------+
                       |            |
                       v            v
                 PostgreSQL      AWS S3
                 (metadata)     (fichiers)

        +----------------+        +----------------------+
        | config-server  | <----- | config-repository     |
        +----------------+        | (Git : *.yml)          |
                ^                 +----------------------+
                |
        lu par api-gateway et document-service au demarrage
```

### Niveau 1 — chaine de deploiement Render

```text
Code -> GitHub -> GitHub Actions -> Tests -> Build Maven -> Docker -> Render
     -> Application accessible sur Internet
```

### Niveau 2 — chaine de deploiement AWS

```text
GitHub -> GitHub Actions -> Docker -> AWS ECR -> AWS EC2
                                            |
                                            v
                                        AWS EKS -> Kubernetes
```

---

## Microservices

Le projet contient volontairement **trois** applications Spring Boot, pas
plus :

### `api-gateway`

- Point d'entree HTTP unique.
- Route `/api/documents/**` vers `document-service` (URL configurable via
  `DOCUMENT_SERVICE_URL`).
- Aucune logique metier, aucune authentification.
- Spring Cloud Gateway (donc WebFlux — c'est normal et attendu pour la
  gateway elle-meme).

### `document-service`

- Coeur du projet : gestion des documents.
- Architecture hexagonale stricte + DDD (voir section dediee ci-dessous).
- Spring MVC (Spring Web) — **pas** WebFlux : la nature reactive de la
  gateway ne se propage pas au service metier.

### `config-server`

- Spring Cloud Config Server.
- Sert la configuration centralisee (`application.yml`,
  `document-service.yml`, `api-gateway.yml`) depuis le repository Git
  `config-repository`.

---

## Architecture hexagonale

`document-service` respecte la separation stricte suivante :

```text
domain/            -> Java pur aggregate Root, Value Objects, exceptions metier.

application/
  port/in/         -> Contrats des use cases.
  port/out/        -> Contrats vers l'infrastructure.
  service/         -> Implementation des use cases, orchestre domaine + ports out.

adapter/
  in/rest/         -> Controller REST + DTO. Traduit HTTP <-> use cases.
  out/persistence/ -> Implementation de DocumentRepositoryPort.
  out/storage/     -> Implementation de DocumentStoragePort (AWS S3).

infrastructure/    -> Configuration technique transverse.
```

Regle de dependance (Dependency Inversion) :

```text
Domain
   ^
Application
   ^
Adapters
   ^
Infrastructure
```

---

## Contraintes à anticiper pour la containerisation / Render

Ce README documente les contraintes, sans fournir les fichiers eux-memes :

- Chaque module (`api-gateway`, `document-service`, `config-server`)
  dispose de son propre Dockerfile.
- Toute la configuration doit venir de variables d'environnement — aucune
  valeur sensible en dur.
- `document-service` et `api-gateway` exposent deja un endpoint de health
  check via **Spring Boot Actuator** : `/actuator/health`.
- Le port d'écoute de chaque service est configurable via `SERVER_PORT`, ce
  qui correspond au fonctionnement de Render (port dynamique).
- `config-server` doit etre accessible par les deux autres services avant
  leur propre demarrage complet (ou en mode degrade via
  `spring.config.import=optional:configserver:...`, deja active).

---

## Configuration

Variables d'environnement principales :

```text
SERVER_PORT
CONFIG_SERVER_URL
DOCUMENT_SERVICE_URL

DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

AWS_REGION
AWS_S3_BUCKET

CONFIG_REPO_URI
CONFIG_REPO_BRANCH
```

Aucune clé AWS réelle ne doit jamais être committée. Ne jamais mettre
`AWS_ACCESS_KEY_ID` ou `AWS_SECRET_ACCESS_KEY` en dur dans le depot ou dans
`config-repository`.

---

### Fondations & Render

1. Implementer le domaine (Aggregate Root, Value Objects, invariants).
2. Implementer PostgreSQL (JPA entities, repository, mapper).
3. Implementer S3 (AWS SDK, credentials, bucket).
4. Tester localement (Postgres + service en local).
5. Dockeriser chaque module.
6. Docker Compose (orchestration locale).
7. Pousser le projet sur GitHub.
8. Ecrire les workflows GitHub Actions.
9. Mettre en place l'integration continue (CI).
10. Mettre en place le deploiement continu (CD).
11. Deployer sur Render.

---

## Structure du dépot

```text
mini-document-platform/
├── api-gateway/            # Spring Cloud Gateway — routage HTTP
├── document-service/       # Coeur metier — hexagonal + DDD
├── config-server/          # Spring Cloud Config Server
├── config-repository/      # Fichiers de configuration servis par config-server
└── .github/workflows/      # workflows CI/CD 
```

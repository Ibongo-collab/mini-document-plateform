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
domain/            -> Java pur. Aucune dependance Spring / JPA / AWS / HTTP.
                       Aggregate Root, Value Objects, exceptions metier.

application/
  port/in/         -> Contrats des use cases (ce que l'exterieur peut demander).
  port/out/        -> Contrats vers l'infrastructure (ce dont l'application a besoin).
  service/         -> Implementation des use cases, orchestre domaine + ports out.

adapter/
  in/rest/         -> Controller REST + DTO. Traduit HTTP <-> use cases.
  out/persistence/ -> Implementation de DocumentRepositoryPort (JPA / PostgreSQL).
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

Le domaine ne connait rien du monde exterieur ; l'application depend
d'abstractions (les ports) ; les adapters implementent ces ports.

---

## Ce qui est volontairement NON implemente

Pour rester un exercice d'apprentissage, ce squelette ne contient
**aucune** implementation des elements suivants — a ecrire soi-meme :

- logique metier complete (regles, invariants, transitions de statut) ;
- persistance PostgreSQL / JPA reelle ;
- integration AWS SDK / S3 reelle ;
- Dockerfile, docker-compose, `render.yaml` ;
- GitHub Actions / CI-CD ;
- infrastructure AWS (EC2, ECR, RDS, IAM, VPC, EKS, Terraform, Helm) ;
- authentification et securite avancee.

Chaque fichier concerne contient un commentaire `TODO — business
implementation` a l'endroit exact ou completer le code.

---

## Contraintes a anticiper pour la containerisation / Render

Ce README documente les contraintes, sans fournir les fichiers eux-memes
(Dockerfile, docker-compose, GitHub Actions et `render.yaml` sont a ecrire
par l'auteur du projet) :

- Chaque module (`api-gateway`, `document-service`, `config-server`) devra
  disposer de son propre Dockerfile (build multi-stage Maven recommande).
- Toute la configuration doit venir de variables d'environnement — aucune
  valeur sensible en dur (voir section Configuration).
- `document-service` et `api-gateway` exposent deja un endpoint de health
  check via **Spring Boot Actuator** : `/actuator/health`. Render (et plus
  tard Kubernetes) pourra s'appuyer dessus pour les probes de sante.
- Le port d'ecoute de chaque service est configurable via `SERVER_PORT`, ce
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

Aucune cle AWS reelle ne doit jamais etre committee. Ne jamais mettre
`AWS_ACCESS_KEY_ID` ou `AWS_SECRET_ACCESS_KEY` en dur dans le depot ou dans
`config-repository`.

---

## Roadmap d'apprentissage

### Niveau 1 — Fondations & Render

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

### Niveau 2 — AWS

12. Configurer AWS IAM (roles, policies minimales).
13. Brancher AWS S3 en remplacement/complement du stockage local.
14. Provisionner AWS RDS (PostgreSQL manage).
15. Pousser les images Docker vers AWS ECR.
16. Deployer sur AWS EC2.
17. Automatiser le deploiement vers EC2.

### Niveau 3 — Kubernetes

18. Installer Kubernetes en local (kind / minikube).
19. Ecrire les manifests Kubernetes (Deployment, Service).
20. Gerer les Secrets Kubernetes.
21. Gerer les ConfigMaps.
22. Mettre en place un Ingress.
23. Pratiquer les rolling updates.
24. Provisionner AWS EKS.
25. Brancher le CI/CD vers EKS.

### Niveau 4 — Bonus

26. Terraform (infrastructure as code).
27. Helm (packaging Kubernetes).
28. Prometheus (metriques).
29. Grafana (dashboards).
30. AWS CloudWatch (logs & alarmes).

---

## Structure du depot

```text
mini-document-platform/
├── api-gateway/            # Spring Cloud Gateway — routage HTTP
├── document-service/       # Coeur metier — hexagonal + DDD
├── config-server/          # Spring Cloud Config Server
├── config-repository/      # Fichiers de configuration servis par config-server
├── docker/                 # (vide) — Dockerfiles / docker-compose a ecrire
├── k8s/                    # (vide) — manifests Kubernetes a ecrire
└── .github/workflows/      # (vide) — workflows CI/CD a ecrire
```

# POC Training — Développement agentique pour backend Java / Spring Boot

Projet support d'une formation d'initiation au **développement assisté par agent IA** (Claude Code) pour des développeurs backend Java / Spring Boot.

Le projet contient :

- **`shop-api`** : une API e-commerce Spring Boot 4 réaliste (catalogue, clients, commandes, frais de port) qui contient **volontairement** des bugs, de la dette technique, des failles de sécurité et un problème de performance ;
- **`docs/`** : les règles métier et un backlog de tickets, comme dans un vrai projet ;
- **`formation/`** : le programme, le support de cours, les labs pas à pas, le guide formateur et les solutions.

> 💡 **Solutions** : `formation/solutions/` contient les corrigés (CLAUDE.md de référence, agents, skills, hooks, code de référence des démos). Essayez d'abord par vous-même ; si vous êtes bloqué ou en retard, partez de la solution pour aborder le lab suivant sur une base propre (voir [Repartir d'une solution](#repartir-dune-solution)).

## Démarrer (stagiaires)

```bash
git clone <url-du-depot>
cd <dossier-du-depot>
./scripts/check-setup.sh          # Windows : powershell -ExecutionPolicy Bypass -File scripts\check-setup.ps1
claude                            # lancer Claude Code à la racine du projet
```

Puis suivez le [programme](formation/README.md), lab par lab. Pensez à travailler sur votre propre branche : `git checkout -b mon-nom`.

## 🤖 Les agents IA du projet

👉 **Mode d'emploi complet : [GUIDE-AGENTS.md](GUIDE-AGENTS.md)**

| Agent | Fichier | Rôle | Commande à taper dans Claude Code |
|---|---|---|---|
| 🏛️ architecte | [.claude/agents/architecte.md](.claude/agents/architecte.md) | écrit le plan, ne code pas | `@agent-architecte conçois le ticket SHOP-301` |
| 👩‍💻 developpeur | [.claude/agents/developpeur.md](.claude/agents/developpeur.md) | implémente le plan, build vert | `@agent-developpeur implémente SHOP-301 selon docs/plans/SHOP-301.md` |
| 🔍 qa | [.claude/agents/qa.md](.claude/agents/qa.md) | teste le ticket, verdict VALIDÉ / REJETÉ | `@agent-qa vérifie le ticket SHOP-101` |
| 🧐 reviewer | [.claude/agents/reviewer.md](.claude/agents/reviewer.md) | revue de code en lecture seule | `@agent-reviewer relis le code customer` |
| 🎬 toute l'équipe | [.claude/skills/equipe/SKILL.md](.claude/skills/equipe/SKILL.md) | architecte → dev → QA + reviewer | `/equipe SHOP-301` |

Autres façons : nommer l'agent dans une phrase (« Utilise l'agent qa pour… »), ou lancer toute une session en tant qu'agent depuis le terminal (`claude --agent reviewer`).

Les agents sont chargés **au démarrage** d'une session Claude Code ouverte **à la racine du projet** : après une modification, démarrez une nouvelle conversation. Vérification : « Quels agents sont disponibles dans ce projet ? ».

## Démarrage rapide

Prérequis : JDK 21+, Git, un compte Claude (Pro, Max, Team, Enterprise ou Console API). Maven n'est pas nécessaire (wrapper fourni).

```bash
# Vérifier l'environnement
./scripts/check-setup.sh          # Windows : powershell -ExecutionPolicy Bypass -File scripts\check-setup.ps1

# Construire et tester
./mvnw verify                     # Windows : mvnw.cmd verify

# Lancer l'application
./mvnw spring-boot:run
curl http://localhost:8080/api/products
```

## L'application

| Endpoint | Description |
|---|---|
| `GET /api/products`, `GET /api/products/{id}`, `POST /api/products` | Catalogue |
| `GET /api/customers/{id}`, `GET /api/customers/search?name=`, `POST /api/customers` | Clients |
| `GET /api/orders`, `GET /api/orders/{id}`, `POST /api/orders` | Commandes |
| `GET /actuator/health` | Santé |

Base H2 en mémoire, rechargée à chaque démarrage depuis `src/main/resources/data.sql`.

### Architecture hexagonale (ports & adapters)

```
src/main/java/fr/formation/shop/
├── domain/                        Java pur : modèles et règles métier (catalog, customer, order, shipping legacy)
├── application/
│   ├── port/in/                   use cases (interfaces) + commandes
│   ├── port/out/                  ports sortants (interfaces de repository, notification...)
│   └── service/                   implémentation des use cases
└── adapter/
    ├── in/web/                    controllers REST + DTO
    └── out/persistence/           entités JPA, Spring Data, adapters de persistance
```

```
        ┌──────────── adapter.in.web ────────────┐
        │  ProductController ──▶ FindProductUseCase (port.in)
        │                               ▲
        │                     ProductService (application)
        │                               │ utilise
        │                     ProductRepository (port.out)
        │                               ▲ implémente
        └──────── adapter.out.persistence: ProductPersistenceAdapter ──▶ Spring Data / H2
                       Tout le monde dépend du domaine ; le domaine ne dépend de personne.
```

Les règles de dépendance sont **exécutables** : `HexagonalArchitectureTest` (ArchUnit) casse le build en cas de violation. Le contexte `catalog` est la référence des conventions dans chaque couche.

## La formation

Commencer par [formation/README.md](formation/README.md).

**Démo pour initiés : l'équipe d'agents testée en live** : [formation/demo/demo-initiation.md](formation/demo/demo-initiation.md).

**Démo avancée** : [formation/demo/README.md](formation/demo/README.md). Un agent implémente le use case SHOP-201 (avis clients) en traversant toutes les couches de l'hexagone, sous la surveillance d'ArchUnit et des hooks.

| Lab | Thème | Ticket |
|---|---|---|
| [00](formation/labs/lab-00-installation.md) | Installation et premier contact | — |
| [01](formation/labs/lab-01-contexte-claude-md.md) | Contexte et mémoire projet (`CLAUDE.md`) | — |
| [02](formation/labs/lab-02-bug-tdd.md) | Corriger un bug en boucle TDD | SHOP-101 |
| [03](formation/labs/lab-03-feature-plan-mode.md) | Développer une feature avec le Plan mode | SHOP-102 |
| [04](formation/labs/lab-04-tests.md) | Générer des tests utiles | SHOP-103 |
| [05](formation/labs/lab-05-refactoring-legacy.md) | Refactorer du code legacy sous filet de sécurité | SHOP-104 |
| [06](formation/labs/lab-06-personnalisation.md) | Personnaliser l'agent : skills, subagents, hooks | — |
| [07](formation/labs/lab-07-revue-securite-perf.md) | Revue de code, sécurité et performance | SHOP-105 |
| [08](formation/labs/lab-08-mcp-headless.md) | MCP, mode headless et CI | — |
| [Final](formation/labs/challenge-final.md) | Challenge en autonomie | SHOP-106 |

## Repartir d'une solution

| Besoin | Commande (depuis la racine du projet) |
|---|---|
| `CLAUDE.md` de référence (après le lab 01) | `cp formation/solutions/CLAUDE.md .` |
| Skills, agents et hooks de référence (après le lab 06) | `cp -r formation/solutions/.claude/. .claude/` |
| Code de référence de la démo SHOP-201 (avis clients) | `cp -r formation/solutions/demo-shop-201/src/. src/` |
| Code de référence de la démo SHOP-301 (stock bas) | `cp -r formation/solutions/equipe-shop-301/. .` |
| Revenir à l'état initial du dépôt | `git checkout main && git reset --hard origin/main && git clean -fd` |

Sous Windows (PowerShell), remplacez `cp -r a/. b/` par `Copy-Item a\* b -Recurse -Force`.

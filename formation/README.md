# Programme — Initiation au développement agentique (Java / Spring Boot)

## Public et prérequis

- Développeurs backend Java ayant une pratique de Spring Boot (REST, JPA, tests).
- Aucune expérience préalable des agents IA n'est nécessaire.
- Poste : JDK 21+, Git, IDE (IntelliJ IDEA ou VS Code), accès Internet, compte Claude.

## Objectifs pédagogiques

À l'issue de la formation, le participant sera capable de :

1. Expliquer ce qu'est un agent de code et en quoi il diffère d'un assistant de complétion ou d'un chat.
2. Donner à l'agent le **bon contexte** (`CLAUDE.md`, specs, tickets) et mesurer l'effet de ce contexte.
3. Piloter l'agent avec un workflow fiable : **explorer → planifier → implémenter → vérifier**.
4. Utiliser les tests comme **critère de vérification** de l'agent (TDD, tests de caractérisation).
5. Personnaliser l'agent pour son équipe : permissions, skills, subagents, hooks, serveurs MCP.
6. Faire respecter une **architecture hexagonale** par l'agent grâce au contexte (`CLAUDE.md`, contexte de référence) et à des garde-fous exécutables (ArchUnit, hooks).
7. Relire de façon critique le code produit et identifier les limites et risques (hallucinations, sécurité, confidentialité).

## Déroulé sur 2 jours (14 h)

### Jour 1 : les fondamentaux

| Horaire | Séquence | Support |
|---|---|---|
| 09:00 – 09:40 | **Démo** : une équipe d'agents (architecte, dev, QA, reviewer) testée en live | [demo/demo-initiation.md](demo/demo-initiation.md) |
| 09:40 – 10:30 | Concepts : LLM, agent, boucle agentique, outils, contexte, agents et hexagonal | [01-concepts.md](01-concepts.md) |
| 10:30 – 10:45 | *Pause* | |
| 10:45 – 11:30 | Lab 00 : installation, premier contact, exploration du code | [lab-00](labs/lab-00-installation.md) |
| 11:30 – 12:30 | Lab 01 : contexte et `CLAUDE.md` (expérience A/B) | [lab-01](labs/lab-01-contexte-claude-md.md) |
| 12:30 – 13:30 | *Déjeuner* | |
| 13:30 – 14:30 | Lab 02 : corriger un bug en boucle TDD | [lab-02](labs/lab-02-bug-tdd.md) |
| 14:30 – 16:00 | Lab 03 : feature complète avec le Plan mode | [lab-03](labs/lab-03-feature-plan-mode.md) |
| 16:00 – 16:15 | *Pause* | |
| 16:15 – 17:15 | Lab 04 : générer des tests utiles, mesurer la couverture | [lab-04](labs/lab-04-tests.md) |
| 17:15 – 17:30 | Débrief du jour 1 | |

### Jour 2 : industrialiser

| Horaire | Séquence | Support |
|---|---|---|
| 09:00 – 10:15 | Lab 05 : refactoring legacy sous filet de sécurité | [lab-05](labs/lab-05-refactoring-legacy.md) |
| 10:15 – 10:30 | *Pause* | |
| 10:30 – 12:00 | Lab 06 : personnaliser l'agent (skills, subagents, hooks) | [lab-06](labs/lab-06-personnalisation.md) |
| 12:00 – 13:00 | *Déjeuner* | |
| 13:00 – 14:15 | Lab 07 : revue de code, sécurité, performance | [lab-07](labs/lab-07-revue-securite-perf.md) |
| 14:15 – 15:15 | Lab 08 : MCP, mode headless, intégration CI | [lab-08](labs/lab-08-mcp-headless.md) |
| 15:15 – 15:30 | *Pause* | |
| 15:30 – 16:45 | Challenge final en autonomie | [challenge-final](labs/challenge-final.md) |
| 16:45 – 17:30 | Restitution, bonnes pratiques, rétrospective | [bonnes-pratiques.md](bonnes-pratiques.md) |

### Variante 1 jour (7 h)

Démo équipe d'agents (35 min) → Concepts (45 min) → Lab 00 (30 min) → Lab 01 (45 min) → Lab 02 (1 h) → Lab 03 (1 h 30) → Lab 06, skills et hooks seulement (1 h) → Lab 07 (1 h) → Débrief (30 min).

## Ressources

- [Aide-mémoire Claude Code](cheatsheet.md)
- [Bonnes pratiques et points de vigilance](bonnes-pratiques.md)
- Règles métier : [../docs/specs/regles-metier.md](../docs/specs/regles-metier.md)
- Backlog : [../docs/tickets/](../docs/tickets/)
- Documentation officielle : https://docs.claude.com/en/docs/claude-code/overview

## Pour le formateur

- [guide-formateur.md](guide-formateur.md) : liste des défauts cachés, corrections attendues, timing, pièges fréquents, questions de débrief.
- [demo/README.md](demo/README.md) : script minuté de la démo SHOP-201, préparation en une commande, plan B.

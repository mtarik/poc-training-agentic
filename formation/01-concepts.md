# Concepts : du LLM à l'agent de code

## 1. Trois façons d'utiliser l'IA pour coder

| | Complétion (Copilot-like) | Chat | **Agent** |
|---|---|---|---|
| Unité de travail | une ligne, une fonction | une réponse | une **tâche** |
| Accès au projet | fichier courant | ce que vous collez | tout le dépôt, terminal, outils |
| Agit ? | non, propose | non, propose | **oui** : lit, modifie, exécute |
| Se vérifie ? | non | non | **oui**, s'il en a les moyens (tests, compilation) |
| Rôle du dev | taper plus vite | copier-coller | **spécifier, cadrer, relire** |

## 2. La boucle agentique

Un agent est un LLM placé dans une boucle, avec des outils :

```
           ┌──────────────────────────────────────────┐
           ▼                                          │
  Objectif ──▶ Raisonner ──▶ Appeler un outil ──▶ Observer le résultat
                  │          (Read, Grep, Edit,         │
                  │           Bash: ./mvnw test...)     │
                  │                                     │
                  └───── tâche terminée ? ◀─────────────┘
                              │ oui
                              ▼
                          Rapport
```

Conséquences pratiques :

- **L'agent est aussi bon que sa boucle de feedback.** Avec des tests et un build, il se corrige seul ; sans eux, il « croit » avoir réussi.
- Chaque outil appelé consomme du **contexte** (sa sortie est relue par le modèle).
- L'agent ne « sait » rien de votre projet tant qu'il ne l'a pas lu.

## 3. La fenêtre de contexte

Le contexte, c'est tout ce que le modèle « voit » à un instant donné : instructions système, `CLAUDE.md`, historique de la conversation, fichiers lus, sorties de commandes.

- Il est **limité**. Quand il se remplit, la conversation est résumée (compaction) et des détails peuvent se perdre.
- Un contexte pollué (longues sorties de logs, tâches précédentes sans rapport) **dégrade** la qualité.
- Hygiène : une tâche = une session (`/clear` entre deux tâches), `/context` pour voir le remplissage, `/compact` pour résumer.

> **Context engineering** : l'essentiel du travail du développeur consiste à fournir le bon contexte au bon moment, ni trop ni trop peu.

## 4. Anatomie de Claude Code

| Brique | Rôle | Où |
|---|---|---|
| **Outils intégrés** | Read, Edit, Write, Grep, Glob, Bash, WebFetch… | — |
| **Permissions** | ce que l'agent peut faire sans demander / jamais | `.claude/settings.json` |
| **Mémoire (`CLAUDE.md`)** | conventions, commandes, architecture, chargées à chaque session | racine du projet, `~/.claude/CLAUDE.md` |
| **Plan mode** | l'agent explore et propose un plan **sans rien modifier** | `Shift+Tab` |
| **Skills** | procédures réutilisables (`/fix-ticket SHOP-101`) | `.claude/skills/<nom>/SKILL.md` |
| **Subagents** | agents spécialisés avec leur propre contexte et leurs propres outils | `.claude/agents/<nom>.md` |
| **Hooks** | scripts déclenchés sur des événements (avant/après un outil, fin de tâche…) : **déterministes** | `.claude/settings.json` |
| **MCP** | connecter des outils externes (Jira, BDD, docs, navigateur…) | `.mcp.json`, `claude mcp add` |
| **Headless** | exécuter l'agent sans interface (scripts, CI) | `claude -p "..."` |

**Instruction ≠ garantie.** Une consigne dans `CLAUDE.md` est suivie *la plupart du temps*. Une règle qui doit *toujours* s'appliquer passe par une **permission** ou un **hook**.

## 5. Le workflow recommandé

1. **Explorer** : « Lis X et Y, explique-moi comment fonctionne Z. Ne modifie rien. »
2. **Planifier** : Plan mode. Relire, challenger, amender le plan.
3. **Implémenter** : par petites étapes vérifiables (tests, compilation).
4. **Vérifier** : relire le diff, lancer les tests, tester à la main.
5. **Committer** : petits commits, message clair, qui peuvent être annulés.

## 6. Écrire un bon prompt de tâche

| ❌ Vague | ✅ Précis |
|---|---|
| « Corrige le bug des commandes » | « Lis `docs/tickets/SHOP-101.md`. Écris d'abord un test du domaine qui reproduit le bug, vérifie qu'il échoue, puis applique la correction minimale. Lance `./mvnw test`. » |
| « Ajoute des tests » | « Teste `OrderService.place` : client inconnu, produit inconnu, sauvegarde via le port. Imite `ProductServiceTest`. Ne modifie pas le code de production. » |
| « Ajoute les avis clients » | « `/new-use-case SHOP-201`. Commence par le plan par couche et attends ma validation. » |
| « Améliore la perf » | « `GET /api/orders` émet trop de requêtes SQL. Mesure leur nombre avec les logs Hibernate, propose 2 options avec leurs compromis, n'implémente rien. » |

Ingrédients : **objectif**, **contexte** (fichiers, ticket), **contraintes** (ce qu'il ne faut pas toucher), **critère de fin vérifiable**.

## 7. Agents et architecture hexagonale

L'architecture hexagonale (ports & adapters) sépare le **cœur métier** de l'**infrastructure** :

```
     adapters entrants              cœur (hexagone)                adapters sortants
   ┌──────────────────┐    ┌──────────────────────────────┐    ┌───────────────────┐
   │ REST controller  │──▶ │ port.in (use case)           │    │ JPA, JDBC         │
   │ (DTO, validation)│    │      ▲ implémente            │    │ email, Kafka, HTTP│
   └──────────────────┘    │ application.service ──▶ port.out ◀──│ (implémentent les │
                           │      │ utilise               │    │  ports sortants)  │
                           │      ▼                       │    └───────────────────┘
                           │ domain (Java pur)            │
                           └──────────────────────────────┘
```

Pourquoi c'est un **excellent terrain pour un agent** :

| Propriété de l'hexagonal | Bénéfice pour l'agent |
|---|---|
| Une place pour chaque chose | des consignes simples et vérifiables : « la règle métier va dans le domaine » |
| Domaine en Java pur | des tests rapides (millisecondes), sans Spring, donc une boucle de feedback très courte |
| Ports = contrats explicites | l'agent peut implémenter une couche à la fois, les autres étant mockées |
| Dépendances orientées vers le centre | des règles **exécutables** avec ArchUnit : l'agent est jugé par un test, pas par une relecture |
| Un contexte de référence (`catalog`) | l'agent imite un exemple complet, couche par couche |

Mais attention :

- L'agent a tendance à **prendre des raccourcis** (le service qui appelle directement le repository JPA, une annotation `@Entity` sur le modèle du domaine). Sans ArchUnit, la dérive est silencieuse.
- L'hexagonal **multiplie les fichiers** (une vingtaine pour un use case). C'est fastidieux pour un humain, trivial pour un agent… mais la **revue** devient l'étape critique.
- Une structure respectée n'est pas un bon modèle : un domaine anémique qui sert de DTO passe ArchUnit (voir le contexte `customer`).

> La démo [SHOP-201](demo/README.md) illustre tout cela en direct.

## 8. Limites à garder en tête

- **Hallucinations** : API inexistantes, annotations d'une autre version de Spring (Spring Boot 4 a déplacé plusieurs packages de test !).
- **Complaisance** : l'agent peut modifier un test pour le faire passer, ou affirmer « tous les tests passent » sans les avoir lancés.
- **Sur-ingénierie** : il ajoute volontiers des abstractions non demandées.
- **Sécurité et confidentialité** : ne jamais exposer de secrets ou de données réelles. L'agent exécute des commandes sur **votre** machine.
- **Responsabilité** : le code fusionné reste **le vôtre**.

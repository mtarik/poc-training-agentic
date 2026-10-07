# Guide des agents IA du projet

## 1. Où sont les agents ?

Un agent est un **simple fichier texte** placé dans le dossier `.claude/agents/` à la racine du projet (dossier caché : son nom commence par un point).

### Les agents actifs (utilisables tout de suite)

| Agent | Fichier | Rôle | Droits |
|---|---|---|---|
| 🏛️ `architecte` | [.claude/agents/architecte.md](.claude/agents/architecte.md) | conçoit la solution et écrit un plan dans `docs/plans/<ticket>.md` | lit le code, écrit **uniquement** le plan |
| 👩‍💻 `developpeur` | [.claude/agents/developpeur.md](.claude/agents/developpeur.md) | implémente le plan, écrit les tests, ne s'arrête que quand le build est vert | tous |
| 🔍 `qa` | [.claude/agents/qa.md](.claude/agents/qa.md) | teste à partir des critères d'acceptation du ticket, cherche les cas limites | écrit des tests, **jamais** le code de production |
| 🧐 `reviewer` | [.claude/agents/reviewer.md](.claude/agents/reviewer.md) | relit le code comme un senior exigeant | **lecture seule** |

Et un **chef d'orchestre**, qui est un *skill* (une procédure), pas un agent :

| Skill | Fichier | Rôle |
|---|---|---|
| `/equipe` | [.claude/skills/equipe/SKILL.md](.claude/skills/equipe/SKILL.md) | fait traiter un ticket par toute l'équipe : architecte → développeur → QA + reviewer en parallèle → corrections |

### Les agents « corrigés » (inactifs)

Les fichiers de [formation/solutions/.claude/](formation/solutions/.claude/) (`spring-reviewer`, `test-writer`, skills `fix-ticket` et `new-use-case`) sont les **solutions du lab 06**. Ils ne sont **pas** chargés par Claude Code tant qu'ils restent dans ce dossier. Pour les activer, copiez-les dans `.claude/` :

```bash
cp -r formation/solutions/.claude/agents/. .claude/agents/
cp -r formation/solutions/.claude/skills/. .claude/skills/
```

## 2. Avant de les utiliser

1. **Ouvrez le dossier racine du projet** (`poc-training-ai agent`), pas un sous-dossier.
2. **Démarrez une nouvelle session** : les agents sont lus **au démarrage** de la session.
   - Terminal : `claude`, lancé depuis la racine du projet.
   - VS Code (extension Claude Code) : ouvrez une **nouvelle conversation**.
3. **Vérifiez** en tapant :

   ```text
   Quels agents et quels skills sont disponibles dans ce projet ?
   ```

   Vous devez voir `architecte`, `developpeur`, `qa`, `reviewer` et le skill `equipe`.

## 3. Utiliser un agent : les 3 façons de le désigner

| Façon | Ce que vous tapez | Quand l'utiliser |
|---|---|---|
| **① Mention `@agent-`** (la plus sûre) | `@agent-reviewer relis le code customer` | pour **forcer** un agent précis : c'est garanti que celui-là sera utilisé |
| **② En le nommant dans une phrase** | `Utilise l'agent reviewer pour relire le code customer` | le plus naturel ; Claude délègue en général à l'agent nommé |
| **③ Toute la session en tant qu'agent** | dans le terminal : `claude --agent reviewer` | pour que la conversation entière se comporte comme cet agent (mêmes droits, même rôle) |

**① Mention `@agent-`.** Tapez `@` dans la zone de saisie et choisissez l'agent dans la liste (il apparaît avec la mention *(agent)*), ou écrivez directement `@agent-` suivi de son nom :

```text
@agent-architecte conçois le ticket SHOP-301
@agent-developpeur implémente SHOP-301 selon docs/plans/SHOP-301.md
@agent-qa vérifie le ticket SHOP-301
@agent-reviewer fais la revue du code produit pour SHOP-301
```

Cette syntaxe ne fonctionne qu'en mode interactif (terminal ou VS Code), pas avec `claude -p`.

**③ Session dédiée.** Utile en démo pour montrer un agent « en direct », avec ses restrictions. Par exemple, le reviewer ne peut rien modifier, même si on le lui demande :

```bash
claude --agent reviewer
claude -p --agent qa "Vérifie le ticket SHOP-101"     # aussi en mode script (headless)
```

Dans tous les cas, l'agent travaille avec **ses propres droits** (définis dans son fichier), puis son rapport revient dans la conversation.

Source : [documentation officielle des subagents](https://code.claude.com/docs/en/sub-agents).

### 🧐 reviewer : faire relire du code

```text
Utilise l'agent reviewer pour faire la revue du code des clients (tout ce qui concerne customer).
```

**Résultat attendu** : un tableau des problèmes classés par gravité (🔴 injection SQL dans `CustomerSearchJdbcAdapter`, 🔴 `internalNotes` exposé par l'API…) et une ligne `VERDICT REVIEW : CHANGEMENTS DEMANDÉS`. Aucun fichier modifié.

### 🔍 qa : vérifier un ticket

```text
Utilise l'agent qa pour vérifier le ticket SHOP-101.
```

**Résultat attendu** : un test d'acceptation créé dans `src/test/java/.../acceptance/`, qui échoue (`attendu 108.00, obtenu 36.00`), un tableau critère / test / résultat et `VERDICT QA : REJETÉ`. Le code de production n'est pas touché.

### 👩‍💻 developpeur : corriger ou implémenter

```text
Transmets le rapport de la QA à l'agent developpeur pour qu'il corrige SHOP-101.
```

**Résultat attendu** : une correction d'une ligne dans `domain/order/OrderLine.java`, puis `./mvnw verify` vert, et la liste des fichiers modifiés.

### 🏛️ architecte : concevoir avant de coder

```text
Utilise l'agent architecte pour concevoir le ticket SHOP-301.
```

**Résultat attendu** : un nouveau fichier `docs/plans/SHOP-301.md` (tableau des fichiers par couche, règles métier, points d'attention). Aucun code écrit.

## 4. Faire travailler toute l'équipe

```text
/equipe SHOP-301
```

Déroulé :

1. 🏛️ l'**architecte** écrit le plan → Claude vous **demande de le valider** ;
2. 👩‍💻 le **développeur** implémente et lance le build ;
3. 🔍 la **QA** et 🧐 le **reviewer** vérifient **en parallèle** ;
4. si l'un d'eux rejette, le développeur corrige et ils revérifient (2 tours maximum) ;
5. bilan final : tableau agent / travail / verdict, et une commande `curl` pour tester.

Rien n'est commité : c'est à vous de relire (`git status`, `git diff`).

Vérification de la fonctionnalité :

```bash
./mvnw spring-boot:run
curl -s "http://localhost:8080/api/products/low-stock"
```

On attend 3 produits : Jeu de société Agile (0), Ecran 27 pouces (3), Casque audio (5).

Vous pouvez aussi enchaîner les agents **à la main**, étape par étape :

```text
Utilise l'agent architecte pour concevoir SHOP-301.
Utilise l'agent developpeur pour implémenter SHOP-301 selon docs/plans/SHOP-301.md.
Lance en parallèle l'agent qa sur SHOP-301 et l'agent reviewer sur le code produit.
```

## 5. Créer ou modifier un agent

Un agent = un fichier `.claude/agents/<nom>.md` :

```markdown
---
name: securite                      # identifiant de l'agent
description: Audite la sécurité du code. À utiliser avant une mise en production.   # quand l'utiliser
tools: Read, Grep, Glob             # ses droits (ici : lecture seule)
model: sonnet                       # optionnel : opus, sonnet, haiku
---

Tu es l'expert sécurité de l'équipe.   ← sa « fiche de poste » : rôle, méthode, format de rapport
...
```

- Le plus simple : **demandez-le à Claude** (« crée un agent de projet *securite* qui audite… et qui n'a que des droits de lecture »).
- La `description` est importante : c'est elle qui permet à Claude de choisir l'agent tout seul, sans que vous le nommiez.
- Après création ou modification, **démarrez une nouvelle session** pour que l'agent soit pris en compte.

## 6. Ça ne marche pas ?

| Symptôme | Cause probable | Solution |
|---|---|---|
| Claude ne connaît pas l'agent | session démarrée avant la création des fichiers, ou dans un autre dossier | nouvelle session, depuis la racine du projet |
| Je ne vois pas le dossier `.claude` | dossier caché par l'explorateur de fichiers | VS Code l'affiche ; sinon `ls -a` dans un terminal |
| `/agents` affiche « wizard has been removed » | cet assistant n'existe plus dans Claude Code | normal : les agents se gèrent par fichiers, ou en le demandant à Claude |
| `/equipe` est inconnu | skill non chargé | nouvelle session depuis la racine ; vérifier que `.claude/skills/equipe/SKILL.md` existe |
| Claude fait le travail lui-même sans déléguer | demande trop vague | nommez l'agent explicitement : « **Utilise l'agent qa** pour… » |
| L'agent demande des permissions | comportement normal pour les commandes non autorisées dans `.claude/settings.json` | acceptez, ou ajoutez la commande à la liste `allow` |

Démo complète pas à pas pour un public : [formation/demo/demo-initiation.md](formation/demo/demo-initiation.md).

# Démo : une équipe d'agents IA (architecte, dev, QA, reviewer) testée en live

⏱ 30 à 40 min · 👥 Public débutant, aucun prérequis IA · 🎯 Un seul message à faire passer :

> **Un agent IA, c'est un rôle décrit dans un simple fichier texte. Plusieurs agents, c'est une équipe qui lit le code, agit, se contrôle mutuellement… et l'humain garde la main.**

## L'équipe

Le projet contient 4 agents dans `.claude/agents/`. Chacun tient en 15 à 25 lignes de texte, que vous pouvez montrer à l'écran.

| Agent | Rôle | Droits | Livre |
|---|---|---|---|
| 🏛️ **architecte** | conçoit, ne code pas | lecture + écriture dans `docs/plans/` uniquement | un plan d'une page |
| 👩‍💻 **developpeur** | implémente le plan | lecture, écriture, terminal | du code + des tests, build vert |
| 🔍 **qa** | cherche ce qui ne marche pas, à partir du ticket | tests uniquement, **jamais** le code de production | `VERDICT QA : VALIDÉ / REJETÉ` |
| 🧐 **reviewer** | relit comme un senior exigeant | lecture seule | `VERDICT REVIEW : APPROUVÉ / CHANGEMENTS DEMANDÉS` |

Plus un **chef de projet** : le skill `/equipe` (`.claude/skills/equipe/SKILL.md`) qui enchaîne architecte → développeur → QA et reviewer en parallèle → corrections si besoin.

## Préparation (la veille, 10 min)

```bash
git status                                  # projet propre
./mvnw verify                               # tout est vert (21 tests)
git add -A && git commit -m "Avant démo"    # point de retour
```

- Lancez `claude` **depuis la racine du projet** (les agents sont chargés au démarrage de la session) et vérifiez : `Quels agents sont disponibles dans ce projet ?` → les 4 doivent apparaître.
- Deux terminaux côte à côte : à gauche Claude Code, à droite l'appli et les `curl`.
- **Faites une répétition complète**, puis `git reset --hard && git clean -fd`.

---

## Partie 1 : présenter l'équipe (3 min)

Ouvrez `.claude/agents/qa.md` au vidéoprojecteur.

💬 *« Voilà un agent QA. Ce n'est pas du code : c'est une fiche de poste. Un nom, une mission, des droits (il n'a pas le droit de toucher au code de production) et un format de rapport. »*

---

## Partie 2 : tester chaque agent seul (15 min)

### 🧐 Le reviewer trouve des failles (4 min)

```text
Utilise l'agent reviewer pour faire la revue du code des clients (tout ce qui concerne customer).
```

👀 **Attendu** : un tableau avec en 🔴 une **injection SQL** (`CustomerSearchJdbcAdapter`) et la **fuite de données internes** (`internalNotes` renvoyé par l'API). Verdict : `CHANGEMENTS DEMANDÉS`.

💬 *« Il n'a modifié aucun fichier : il n'en a pas le droit. Il a lu, compris et signalé. »*

### 🔍 La QA prouve un bug (4 min)

```text
Utilise l'agent qa pour vérifier le ticket SHOP-101.
```

👀 **Attendu** : la QA écrit un test d'acceptation à partir du ticket (3 livres en promotion), le lance, le test est **rouge** (`attendu 108.00, obtenu 36.00`). Verdict : `REJETÉ`.

💬 *« Elle n'a pas corrigé le bug : ce n'est pas son rôle. Elle l'a **prouvé**. »*

### 👩‍💻 Le développeur corrige (4 min)

```text
Transmets le rapport de la QA à l'agent developpeur pour qu'il corrige SHOP-101.
```

👀 **Attendu** : il trouve la ligne fautive (`OrderLine.of`), corrige en une ligne, relance les tests : le test de la QA passe au **vert**.

💬 *« La QA a trouvé, le dev a corrigé, les tests ont tranché. Comme dans une vraie équipe. »*

### 🏛️ L'architecte conçoit (3 min)

```text
Utilise l'agent architecte pour concevoir le ticket SHOP-301.
```

👀 **Attendu** : un fichier `docs/plans/SHOP-301.md` apparaît, avec le tableau des fichiers à créer ou modifier, couche par couche. **Aucune ligne de code** n'a été écrite. Ouvrez le plan à l'écran.

---

## Partie 3 : toute l'équipe sur une feature (12 à 15 min) ⭐

Remettez à zéro ce qui a été fait en partie 2 (`git reset --hard && git clean -fd`), puis `/clear` dans Claude Code.

```text
/equipe SHOP-301
```

Le ticket [SHOP-301](../../docs/tickets/SHOP-301.md) : `GET /api/products/low-stock?threshold=5`, la liste des produits à réapprovisionner. Simple, mais avec des pièges pour la QA (borne incluse, seuil hors limites, tri).

👀 **Commentez au fil de l'eau** :

1. **L'architecte** écrit le plan → le chef de projet vous **demande de le valider**. Lisez-le à voix haute et validez : *« l'humain décide »*.
2. **Le développeur** code et lance le build.
3. **La QA et le reviewer travaillent en parallèle** : deux agents en même temps.
4. Si l'un d'eux rejette, **le développeur corrige** et ils revérifient (boucle automatique, 2 tours maximum).
5. **Le bilan** : un tableau « agent | ce qu'il a fait | verdict ».

Puis, dans le terminal de droite :

```bash
./mvnw spring-boot:run
curl -s "http://localhost:8080/api/products/low-stock"              # 3 produits : Jeu (0), Ecran (3), Casque (5)
curl -s "http://localhost:8080/api/products/low-stock?threshold=10" # 5 produits
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/api/products/low-stock?threshold=101"  # 400
```

Terminez par `git status` : le plan, le code et les tests sont là. **Rien n'est commité** : c'est à l'humain de relire et de valider.

---

## Conclusion (2 min)

| Ce qu'on a vu | Pourquoi c'est puissant |
|---|---|
| Des agents = des fiches de poste en texte | n'importe qui dans l'équipe peut en créer un en 10 minutes |
| Des droits différents par agent | la QA et le reviewer ne peuvent pas « tricher » en modifiant le code |
| Des agents qui se contrôlent entre eux | les erreurs du dev sont attrapées avant l'humain |
| Des tests comme juge de paix | la vérité ne dépend ni de l'agent ni de son ton assuré |
| L'humain valide le plan et relit le résultat | **l'agent fait le travail, le développeur garde le contrôle** |

## Plan B

| Problème | Que faire |
|---|---|
| L'agent part dans une mauvaise direction | `Esc`, puis recadrez à voix haute : c'est aussi une leçon |
| Il demande une permission | acceptez en expliquant : « il ne fait rien sans mon accord » |
| C'est trop long | sautez la partie 2 et passez directement à `/equipe` |
| Plus de réseau / démo ratée | `cp -r formation/solutions/equipe-shop-301/. .` installe le plan et le code de référence (vérifiés : 29 tests verts) ; montrez-les et lancez les `curl` |
| Les agents n'apparaissent pas | relancez `claude` depuis la racine du projet (ils sont lus au démarrage) |

## Après la démo

```bash
git reset --hard && git clean -fd
```

## Pour aller plus loin (questions du public)

- **« On peut choisir un modèle différent par agent ? »** Oui : ajoutez `model: opus` (plus puissant, plus cher) ou `model: haiku` (rapide, économique) dans l'en-tête du fichier. Par exemple, opus pour l'architecte et haiku pour le reviewer.
- **« Comment créer mon propre agent ? »** Demandez-le : « crée un agent de projet *sécurité* qui… ». Ou copiez un fichier de `.claude/agents/` et adaptez-le.
- **« C'est réservé à Java ? »** Non : les fiches de poste sont en français ; seules les commandes (`./mvnw`) sont propres au projet.

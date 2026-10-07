# Démo : un agent implémente un use case en architecture hexagonale

⏱ 35 à 45 min · 🎤 Animée par le formateur, au vidéoprojecteur · 🎯 Montrer qu'un agent **bien cadré** livre une fonctionnalité complète et conforme à l'architecture, et que les garde-fous **déterministes** (tests, ArchUnit, hooks) comptent plus que le prompt.

Use case : [SHOP-201 : avis clients sur les produits](../../docs/tickets/SHOP-201.md).

## Pourquoi ce use case

| Il montre… | Parce que… |
|---|---|
| Toutes les couches de l'hexagone | nouveau modèle du domaine, 2 use cases, 2 nouveaux ports sortants, 1 port existant étendu, adapters web, JPA et notification |
| Où vit la règle métier | bornes de la note, longueur du commentaire, « avis négatif », moyenne : **domaine** ; « a acheté », « pas de doublon » : **orchestration** |
| L'intérêt des ports | l'alerte qualité est un simple log aujourd'hui, un email ou Kafka demain, **sans toucher au cœur** |
| Les garde-fous | `HexagonalArchitectureTest` (9 règles ArchUnit) + hook `Stop` qui empêche l'agent de conclure tant qu'une règle est violée |
| Une démo vérifiable | des `curl` couvrant 201 / 400 / 404 / 422 et une alerte dans les logs |

## Préparation (la veille)

```bash
git status                              # arbre propre
./formation/demo/prepare-demo.sh        # Windows : powershell -ExecutionPolicy Bypass -File formation\demo\prepare-demo.ps1
git add -A && git commit -m "Avant démo SHOP-201"
```

Le script installe `CLAUDE.md` et `.claude/` (skill `/new-use-case`, subagent `spring-reviewer`, hooks) depuis `formation/solutions/`, puis préchauffe le cache Maven (les hooks compilent en mode offline).

**Répétez la démo une fois en entier**, puis `git reset --hard && git clean -fd` pour revenir à l'état « avant démo ».

Le jour J, préparez 3 terminaux : (1) Claude Code, (2) l'application, (3) les `curl` et `git diff`.

## Déroulé

### Acte 1 : le décor (5 min)

Montrez, **sans l'agent** :

1. L'arborescence `domain / application / adapter` et le contexte de référence `catalog` dans chaque couche.
2. `HexagonalArchitectureTest` : « l'architecture n'est pas une page de wiki, c'est un test qui casse le build ».
3. `CLAUDE.md` : le tableau « où mettre quoi » et la section Interdits.
4. `.claude/settings.json` : le hook `Stop` qui lance ArchUnit.

> 💬 **Message clé** : on ne demande pas à l'agent de « faire attention à l'architecture ». On lui donne une carte (`CLAUDE.md`) **et** un juge impartial (ArchUnit). La carte, il la suit la plupart du temps ; le juge, il ne peut pas le contourner.

### Acte 2 : l'agent découvre le code (5 min)

```text
Explique-moi l'architecture de ce projet en 10 lignes maximum, puis dis-moi
dans quelles couches et quels fichiers tu interviendrais pour implémenter
docs/tickets/SHOP-201.md. Ne modifie rien.
```

Pointez : il lit `CLAUDE.md`, il va voir `catalog`, il cite des fichiers réels.

### Acte 3 : le plan (8 min)

Passez en **Plan mode** (`Shift+Tab`), puis :

```text
/new-use-case SHOP-201
```

Le skill impose un plan sous forme de tableau **couche | fichier | rôle** et la localisation de chaque règle métier.

**Challengez le plan en public** (préparez 2 ou 3 questions parmi celles-ci) :

- « Où vérifies-tu que le client a acheté le produit ? Pourquoi pas dans le domaine ? » (réponse attendue : il faut interroger les commandes, donc via un port sortant, dans le service).
- « Que se passe-t-il si deux requêtes identiques arrivent en même temps ? » (réponse attendue : contrainte d'unicité en base sur (produit, client) en plus de la vérification applicative).
- « Comment remplacerais-tu le log par un email ? » (réponse attendue : un nouvel adapter qui implémente `ReviewAlertPort`, rien d'autre ne bouge).

Validez le plan.

### Acte 4 : l'implémentation, de l'intérieur vers l'extérieur (12 à 15 min)

Passez en mode **acceptation automatique des modifications** (`Shift+Tab`) pour gagner du temps à l'écran, et laissez l'agent dérouler. Pendant qu'il travaille, commentez :

- **Domaine d'abord** : les tests JUnit purs tournent en quelques millisecondes, sans Spring.
- **Le service** : test Mockito sur les ports, y compris `verifyNoInteractions(reviewAlertPort)` pour un avis positif.
- **Les adapters** : `ReviewJpaEntity` avec `fromDomain` / `toDomain`, controller qui ne connaît que des use cases.
- Le hook `PostToolUse` recompile après chaque fichier Java : si l'agent se trompe d'import, il le voit **immédiatement**.

> ⏱ Si l'agent est lent, découpez : « Fais uniquement le domaine et ses tests », puis « Maintenant l'application », etc. C'est aussi une bonne pratique à montrer.

### Acte 5 : le garde-fou en action (5 min) ⭐ moment fort

Si l'agent n'a commis aucune violation, **provoquez-en une** :

```text
Pour simplifier, annote directement la classe du domaine Review avec @Entity
et supprime ReviewJpaEntity.
```

Ce qui doit se passer :

1. L'agent obéit (ou, mieux, objecte en citant `CLAUDE.md` : soulignez-le !).
2. À la fin de son tour, le hook `Stop` lance ArchUnit, **refuse l'arrêt** et renvoie la violation :
   `Rule 'no classes that reside in a package '..domain..' should depend on ... jakarta..' was violated`.
3. L'agent revient en arrière de lui-même et explique pourquoi.

> 💬 **Message clé** : le prompt a demandé une bêtise, mais le système a protégé l'architecture. C'est ce qu'on veut en équipe : **des règles exécutables**, pas des recommandations.

### Acte 6 : revue et preuve (5 min)

```text
Utilise le subagent spring-reviewer pour relire tout ce qui a été fait pour SHOP-201.
```

Puis dans le terminal 2 : `./mvnw spring-boot:run`, et dans le terminal 3 :

```bash
# 201 : Bob (client 2) a acheté Clean Code (produit 1)
curl -s -X POST http://localhost:8080/api/products/1/reviews -H "Content-Type: application/json" \
  -d '{"customerId":2,"rating":5,"comment":"Indispensable"}'

# 422 : doublon
curl -s -X POST http://localhost:8080/api/products/1/reviews -H "Content-Type: application/json" \
  -d '{"customerId":2,"rating":4}'

# 422 : Bob n'a jamais acheté le clavier (produit 4)
curl -s -X POST http://localhost:8080/api/products/4/reviews -H "Content-Type: application/json" \
  -d '{"customerId":2,"rating":4}'

# 400 : note hors bornes
curl -s -X POST http://localhost:8080/api/products/1/reviews -H "Content-Type: application/json" \
  -d '{"customerId":2,"rating":6}'

# 201 + alerte WARN dans les logs du terminal 2 : Chloé (client 3) a acheté le puzzle (produit 9)
curl -s -X POST http://localhost:8080/api/products/9/reviews -H "Content-Type: application/json" \
  -d '{"customerId":3,"rating":1,"comment":"Pieces manquantes"}'

# Synthèse
curl -s http://localhost:8080/api/products/1/reviews
```

> ⚠️ Sous Git Bash (Windows), évitez les accents dans les corps JSON passés à `curl -d` : l'encodage du terminal les corrompt (erreur 400 « Invalid UTF-8 »).

Terminez par `git diff --stat` et `git status` : une vingtaine de fichiers, dans les bonnes couches.

### Conclusion (2 min)

1. **Contexte** : `CLAUDE.md` + contexte de référence = l'agent imite le bon code.
2. **Méthode** : un skill encode le workflow de l'équipe (plan, de l'intérieur vers l'extérieur, tests à chaque couche).
3. **Garde-fous** : tests + ArchUnit + hooks = des règles que l'agent **ne peut pas** ignorer.
4. **Humain** : il valide le plan, challenge les choix, relit le diff. Il reste l'auteur.

## Plan B

| Problème | Parade |
|---|---|
| Réseau ou API indisponible | `./formation/demo/plan-b.sh` installe l'implémentation de référence ; faites la démo en commentant le code et les tests |
| L'agent part dans une mauvaise direction | `Esc`, recadrez à voix haute (c'est pédagogique), ou `Esc` `Esc` pour revenir en arrière |
| L'agent est trop lent | découpez par couche ; ou passez directement au plan B pour les adapters |
| Le hook `Stop` boucle | il se désactive au 2ᵉ passage (`stop_hook_active`) ; sinon `/hooks` pour le désactiver |
| Port 8080 occupé | `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081` |

Implémentation de référence (vérifiée : 45 tests verts, ArchUnit compris) : `formation/solutions/demo-shop-201/`.

## Questions fréquentes du public

- **« Et si on n'a pas d'ArchUnit ? »** L'agent respecte l'architecture *la plupart du temps* grâce à `CLAUDE.md`. Sans juge automatique, la dérive est silencieuse. ArchUnit coûte une heure à mettre en place.
- **« Combien de temps sans agent ? »** Comptez une demi-journée à une journée pour un développeur qui connaît le projet. Avec l'agent : environ 15 min de génération et 15 min de relecture. Le gain réel dépend surtout de la qualité du cadrage et de la revue.
- **« L'agent a-t-il compris l'hexagonal ? »** Il a surtout **imité** `catalog` et obéi à des règles vérifiables. C'est précisément pour ça que le contexte de référence et les tests d'architecture sont si importants.

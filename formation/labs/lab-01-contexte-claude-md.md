# Lab 01 : Contexte et mémoire projet (`CLAUDE.md`)

⏱ 1 h 15 · 🎯 Mesurer l'effet du contexte sur la qualité du code produit, et rédiger un `CLAUDE.md` utile.

## Principe : une expérience A/B

Vous allez demander **la même tâche** deux fois : sans `CLAUDE.md`, puis avec. Puis vous comparerez.

La tâche :

```text
Ajoute un endpoint GET /api/customers/{id}/orders qui renvoie les commandes d'un client.
```

## Étape A : sans contexte (15 min)

1. Vérifiez que l'arbre de travail est propre : `git status`.
2. Lancez `claude`, envoyez la tâche **telle quelle**, sans rien ajouter. Acceptez les modifications.
3. Une fois terminé, sauvegardez le résultat sur une branche :

```bash
git checkout -b essai-A && git add -A && git commit -m "Essai A sans CLAUDE.md" && git checkout -
```

Notez sur la grille ci-dessous.

## Étape B : générer puis améliorer `CLAUDE.md` (30 min)

1. `/clear`, puis `/init` : l'agent analyse le projet et propose un `CLAUDE.md`.
2. **Relisez-le d'un œil critique.** Ce qui est généré décrit souvent *ce qui existe*, y compris les mauvaises pratiques. Complétez-le avec ce que l'agent ne peut pas deviner :
   - l'**architecture hexagonale** : rôle de chaque couche, règles de dépendance, « où mettre quoi » (règle métier, orchestration, validation, mapping) ;
   - le contexte de **référence** à imiter (`catalog`) et ceux à **ne pas** imiter ;
   - les conventions : DTO records, pas d'objet du domaine exposé par l'API, `@Valid`, `BigDecimal`, gestion d'erreurs ;
   - le type de test attendu **par couche** (domaine en JUnit pur, service avec Mockito sur les ports…) ;
   - les commandes exactes (build, test unitaire, lancement) ;
   - la convention de nommage des tests ;
   - un lien vers les règles métier : `@docs/specs/regles-metier.md` (syntaxe d'import) ;
   - les interdits (`application-prod.yml`, dépendances…).
3. Visez **court et actionnable** : une soixantaine de lignes maximum. Chaque ligne doit changer le comportement de l'agent.

## Étape C : rejouer la tâche (15 min)

```bash
git add CLAUDE.md && git commit -m "Ajout CLAUDE.md"
```

`/clear`, puis envoyez **exactement** la même tâche. Commitez sur `essai-B`.

## Grille de comparaison

| Critère | Essai A | Essai B |
|---|---|---|
| Renvoie un DTO (ni entité JPA, ni objet du domaine) | | |
| Nouveau use case (port entrant) + méthode de port sortant, pas de raccourci | | |
| Logique dans le service / domaine, pas dans le controller | | |
| `HexagonalArchitectureTest` vert | | |
| Pas de N+1 / requête adaptée | | |
| 404 si le client n'existe pas (`NotFoundException`) | | |
| Tests ajoutés, au style de `ProductControllerTest` | | |
| Tests lancés par l'agent | | |
| Fichiers modifiés hors périmètre | | |

```bash
git diff essai-A essai-B --stat
```

## Pour aller plus loin

- Ajoutez une préférence personnelle dans `CLAUDE.local.md` (non versionné), par ex. « réponds-moi de façon concise ».
- Testez `/memory` pour éditer les différents niveaux de mémoire.

## ✅ Critères de réussite

- [ ] Un `CLAUDE.md` relu, amendé et commité.
- [ ] La grille remplie, avec au moins 2 différences constatées.

## 💬 À débriefer

- Qu'est-ce que `/init` a raté ? Pourquoi ne pouvait-il pas le deviner ?
- Quels risques présente un `CLAUDE.md` trop long ? Ou obsolète ?
- `CLAUDE.md` est-il une garantie ? (indice : refaites l'essai B plusieurs fois)

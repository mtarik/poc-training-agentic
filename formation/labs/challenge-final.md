# Challenge final : une feature en autonomie

⏱ 1 h 15 · 🎯 Mettre en œuvre tout le workflow, seul ou en binôme, sans guide pas à pas.

Ticket : [SHOP-106](../../docs/tickets/SHOP-106.md) (recherche paginée de produits).

## Règles du jeu

- Vous partez de votre branche après les labs (avec votre `CLAUDE.md`, vos skills, votre subagent et vos hooks).
- Vous utilisez l'agent comme vous le souhaitez, mais **vous** restez responsable du résultat.
- Vous tenez un **journal** court (`journal.md`) : prompts clés, recadrages, erreurs de l'agent, décisions.

## Livrables

1. Le code sur une branche `challenge/<votre-nom>`.
2. Une description de PR générée puis **relue et corrigée par vous**.
3. Le rapport du subagent `spring-reviewer` sur votre code, et votre réponse à chaque point.
4. Le journal.

## Grille d'évaluation (sur 20)

| Critère | Points |
|---|---|
| Critères d'acceptation de SHOP-106 remplis | 6 |
| Qualité des tests (cas limites, lisibilité, mutants tués) | 4 |
| Respect des conventions du projet | 3 |
| Workflow : plan validé avant le code, commits atomiques | 3 |
| Esprit critique : erreurs de l'agent détectées et corrigées (journal) | 2 |
| Restitution (5 min) : ce qui a marché, ce qui n'a pas marché | 2 |

## Restitution

5 minutes par binôme :

1. Démo `curl` de la recherche.
2. Le moment où l'agent vous a fait gagner le plus de temps.
3. Le moment où il vous en a fait perdre (ou aurait pu introduire un bug).
4. Une règle que vous ajouteriez au `CLAUDE.md` de votre projet réel.

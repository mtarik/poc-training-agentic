---
name: fix-ticket
description: Traite un ticket de bug du dossier docs/tickets (ex. SHOP-101) en suivant une boucle TDD stricte - reproduire par un test rouge, corriger, vérifier. À utiliser quand l'utilisateur demande de corriger un bug référencé par un identifiant SHOP-XXX.
argument-hint: "[SHOP-XXX]"
---

# Corriger le ticket $ARGUMENTS

Suis ces étapes dans l'ordre, sans en sauter.

1. **Comprendre** : lis `docs/tickets/$ARGUMENTS.md` et les règles métier concernées dans `docs/specs/regles-metier.md`. Reformule le bug en une phrase.
2. **Localiser** : identifie le code fautif. Cite le fichier et la ligne. N'écris encore aucun code.
3. **Reproduire** : écris un test qui échoue et qui démontre le bug. Lance-le avec `./mvnw test -Dtest=<ClasseDeTest>` et montre qu'il est **rouge** pour la bonne raison (assertion, pas erreur de compilation).
4. **Corriger** : applique la correction **minimale**. Ne refactore rien d'autre.
5. **Vérifier** : relance le test (vert) puis toute la suite `./mvnw test`.
6. **Résumer** au format :

```
## $ARGUMENTS — résumé
- Cause racine : ...
- Correction : fichier:ligne — ...
- Test ajouté : ...
- Critères d'acceptation : [x] ... / [ ] ...
- Risques / points à vérifier en revue : ...
```

Ne crée pas de commit : l'humain relit d'abord le diff.

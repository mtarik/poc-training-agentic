# SHOP-104 — [TECH] Refactoring du calcul des frais de port

**Type** : Dette technique · **Priorité** : Moyenne · **Composant** : shipping

## Contexte

`domain/shipping/ShippingCostCalculator` date de 2016 : méthode statique unique, `if` imbriqués, nombres magiques, `double` pour des montants, `String` pour des enums. Elle est appelée directement par l'agrégat `Order`. Personne n'ose y toucher et il n'existe aucun test.

## Attendu

1. **Avant toute modification**, écrire des *tests de caractérisation* qui figent le comportement **actuel** (y compris s'il semble faux).
2. Refactorer vers un code lisible et extensible, **qui reste dans le domaine** (Java pur, aucune dépendance à Spring : `HexagonalArchitectureTest` doit rester vert) :
   - un objet instanciable et testable (fini le `static`), par exemple une politique de frais de port passée à `Order.computeTotals(...)` par le service applicatif ;
   - `BigDecimal` pour les montants, `LoyaltyLevel` au lieu de `String` ;
   - les zones et tarifs ne sont plus des nombres magiques ;
   - ajouter une zone ne doit pas obliger à modifier une cascade de `if`.
3. Les tests de caractérisation restent verts **sans modification de leurs valeurs attendues**.
4. Lister dans la PR les **écarts constatés** entre le code et `docs/specs/regles-metier.md` §4 — ils feront l'objet de tickets séparés, **pas** de corrections dans ce refactoring.

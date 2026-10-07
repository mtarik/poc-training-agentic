# SHOP-103 — [TECH] Couvrir les commandes par des tests

**Type** : Tâche technique · **Priorité** : Moyenne · **Composant** : order

## Contexte

Le calcul d'une commande (agrégat `domain/order/Order`, `OrderLine`) et son orchestration (`application/service/OrderService`) n'ont **aucun test**. Le rapport JaCoCo (`target/site/jacoco/index.html`) le confirme.

## Attendu

- [ ] Tests du **domaine** (`Order`, `OrderLine`) en JUnit pur, sans Spring ni Mockito, sur le modèle de `ProductTest` : lignes, promotion, remise GOLD, poids total, frais de port, total.
- [ ] Tests de **l'application** (`OrderService`) avec Mockito sur les ports, sur le modèle de `ProductServiceTest` : client inconnu, produit inconnu, commande sauvegardée.
- [ ] Couverture de lignes de `Order`, `OrderLine` et `OrderService` ≥ 90 %.
- [ ] Les noms de tests décrivent le comportement (`computeTotals_shouldApplyGoldDiscount_whenCustomerIsGold`).
- [ ] Aucun test ne doit dépendre de l'ordre d'exécution ni des données de `data.sql`.

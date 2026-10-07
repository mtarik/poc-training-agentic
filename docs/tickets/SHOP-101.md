# SHOP-101 — [BUG] Montant faux pour les produits en promotion commandés en plusieurs exemplaires

**Type** : Bug · **Priorité** : Haute · **Composant** : order

## Description

Le service client signale qu'une cliente a commandé **3 exemplaires** de « Effective Java » (en promotion à −20 %) et n'a été facturée que d'un seul exemplaire.

## Reproduction

```http
POST /api/orders
Content-Type: application/json

{"customerId": 3, "country": "FR", "lines": [{"productId": 3, "quantity": 3}]}
```

- **Obtenu** : `lineTotal = 36.00`, `subtotal = 36.00`
- **Attendu** : `lineTotal = 108.00` (45,00 × 0,80 × 3), `subtotal = 108.00`

## Critères d'acceptation

- [ ] Un test automatisé reproduit le bug (rouge avant correction, vert après).
- [ ] Le total d'une ligne en promotion tient compte de la quantité.
- [ ] Les lignes hors promotion ne sont pas impactées.
- [ ] Tous les tests passent (`./mvnw test`).

# SHOP-301 — [FEATURE] Produits à réapprovisionner

**Type** : Story · **Priorité** : Moyenne · **Composant** : catalog

## User story

**En tant que** gestionnaire de stock, **je veux** la liste des produits dont le stock est bas, **afin de** les recommander à temps.

## Spécification

`GET /api/products/low-stock?threshold=5`

- Renvoie les produits dont le stock est **inférieur ou égal** au seuil (`threshold`).
- `threshold` est optionnel : **5** par défaut. Il doit être compris entre **0 et 100**, sinon `400`.
- Les produits sont triés par stock **croissant** (les plus urgents en premier), puis par nom.
- Chaque élément contient : `id`, `sku`, `name`, `stock`.

Exemple avec le jeu de données de démonstration et le seuil par défaut :

```json
[
  { "id": 10, "sku": "TOY-0002", "name": "Jeu de société Agile", "stock": 0 },
  { "id": 6,  "sku": "ELE-0003", "name": "Ecran 27 pouces",      "stock": 3 },
  { "id": 5,  "sku": "ELE-0002", "name": "Casque audio",         "stock": 5 }
]
```

## Critères d'acceptation

- [ ] Sans paramètre, le seuil est 5 (exemple ci-dessus : 3 produits, dans cet ordre).
- [ ] `threshold=0` ne renvoie que les produits en rupture.
- [ ] `threshold=10` renvoie aussi « Clavier mécanique » (8) et « Domain-Driven Design » (10) : la borne est **incluse**.
- [ ] `threshold=-1` ou `threshold=101` → `400`.
- [ ] Si aucun produit ne correspond, la réponse est une liste vide `[]` (pas une erreur).
- [ ] Architecture hexagonale respectée (`HexagonalArchitectureTest` vert).

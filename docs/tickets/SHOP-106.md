# SHOP-106 — [FEATURE] Recherche paginée de produits (challenge final)

**Type** : Story · **Priorité** : Moyenne · **Composant** : catalog

## User story

**En tant que** client, **je veux** rechercher des produits par critères avec pagination, **afin de** trouver rapidement ce que je cherche dans un catalogue de plusieurs milliers de références.

## Spécification

`GET /api/products/search`

| Paramètre | Type | Obligatoire | Description |
|---|---|---|---|
| `q` | string | non | texte contenu dans le nom (insensible à la casse) |
| `category` | enum | non | `BOOKS`, `ELECTRONICS`, `HOME`, `TOYS` |
| `minPrice` / `maxPrice` | décimal | non | bornes **incluses** |
| `onPromotion` | booléen | non | uniquement les produits en promotion |
| `inStock` | booléen | non | uniquement les produits avec stock > 0 |
| `page` / `size` | entier | non | défaut 0 / 20, `size` max 100 |
| `sort` | string | non | `price,asc`, `price,desc`, `name,asc` (défaut `name,asc`) |

Réponse :

```json
{
  "content": [ { "...": "ProductResponse" } ],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

## Critères d'acceptation

- [ ] Tous les critères sont combinables.
- [ ] `400` si `minPrice > maxPrice`, si `size > 100` ou si `sort` n'est pas autorisé.
- [ ] Pas de requête SQL construite par concaténation.
- [ ] Tests : adapter de persistance (`@DataJpaTest`), service (Mockito sur les ports), controller (`@WebMvcTest`).
- [ ] Architecture hexagonale respectée : critères de recherche et page de résultats exprimés en types **du domaine ou de l'application** (pas de `Pageable` / `Page` Spring Data dans les ports), `HexagonalArchitectureTest` vert.
- [ ] Le code respecte les conventions du projet (voir `CLAUDE.md`).

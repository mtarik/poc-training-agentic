# Plan SHOP-301 : produits à réapprovisionner

## Résumé
Nouvel endpoint de lecture `GET /api/products/low-stock` dans le contexte `catalog`. Un nouveau use case, une méthode de plus sur le port `ProductRepository`, une requête Spring Data dérivée. Aucune modification du domaine.

## Fichiers
| Couche | Fichier | Action | Rôle |
|---|---|---|---|
| application | `port/in/catalog/FindLowStockProductsUseCase.java` | créer | use case `findLowStock(int threshold)` |
| application | `port/out/ProductRepository.java` | modifier | ajouter `findByStockAtMost(int threshold)` |
| application | `service/ProductService.java` | modifier | implémenter le use case (délégation au port) |
| adapter.out | `persistence/catalog/ProductJpaRepository.java` | modifier | `findByStockLessThanEqualOrderByStockAscNameAsc` |
| adapter.out | `persistence/catalog/ProductPersistenceAdapter.java` | modifier | implémenter `findByStockAtMost` |
| adapter.in | `web/catalog/LowStockProductResponse.java` | créer | DTO `id, sku, name, stock` |
| adapter.in | `web/catalog/ProductController.java` | modifier | `GET /low-stock`, `@RequestParam(defaultValue = "5") @Min(0) @Max(100)` |
| test | `ProductControllerTest` | modifier | mock du nouveau use case + cas 200 et 400 |

## Règles métier et où elles vivent
| Règle | Couche | Comment la tester |
|---|---|---|
| stock ≤ seuil (borne incluse) | adapter.out (requête) | `@DataJpaTest` ou test d'acceptance avec `threshold=10` |
| tri par stock croissant puis nom | adapter.out (requête) | test d'acceptance sur l'ordre des SKU |
| seuil par défaut 5, entre 0 et 100 | adapter.in (validation) | `@WebMvcTest` : 400 pour -1 et 101 |

## Points d'attention
- `/low-stock` doit être déclaré comme chemin littéral : il est prioritaire sur `/{id}` (vérifier que `GET /api/products/1` fonctionne toujours).
- `ProductControllerTest` cassera tant que le nouveau use case n'est pas mocké (`@MockitoBean`).
- Question ouverte : le seuil est-il une règle métier (à mettre dans le domaine) ou un simple paramètre de requête ? Ici, simple paramètre.

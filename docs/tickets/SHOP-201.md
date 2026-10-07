# SHOP-201 — [FEATURE] Avis clients sur les produits (use case de démo)

**Type** : Story · **Priorité** : Haute · **Composant** : nouveau (review)

## User stories

1. **En tant que** client, **je veux** noter un produit que j'ai acheté (1 à 5 étoiles) avec un commentaire optionnel, **afin de** partager mon expérience.
2. **En tant que** visiteur, **je veux** voir la note moyenne et les avis d'un produit, **afin de** choisir en confiance.
3. **En tant que** responsable qualité, **je veux** être alerté de chaque avis négatif (note ≤ 2), **afin de** réagir vite.

## Spécification

### Déposer un avis

`POST /api/products/{productId}/reviews`

```json
{ "customerId": 2, "rating": 5, "comment": "Indispensable !" }
```

| Cas | Réponse |
|---|---|
| Avis valide | `201` + l'avis créé (`id`, `productId`, `customerId`, `rating`, `comment`, `createdAt`) |
| `rating` hors 1..5, `comment` > 500 caractères, `customerId` absent | `400` |
| Produit ou client inexistant | `404` |
| Le client n'a **jamais acheté** ce produit (commande non annulée contenant le produit) | `422` |
| Le client a **déjà** donné un avis sur ce produit | `422` |

Si la note est ≤ 2, le service qualité est **notifié**. Pour l'instant, la notification est une simple ligne de log `WARN` ; demain ce sera un email ou un message Kafka, **sans toucher au cœur métier**.

### Consulter les avis

`GET /api/products/{productId}/reviews`

```json
{
  "productId": 1,
  "averageRating": 4.5,
  "count": 2,
  "reviews": [ { "id": 100, "productId": 1, "customerId": 2, "rating": 5, "comment": "...", "createdAt": "..." } ]
}
```

- `averageRating` arrondi à 1 décimale (HALF_UP), `null` s'il n'y a aucun avis.
- `404` si le produit n'existe pas.

## Contraintes d'architecture

- Respect de l'architecture hexagonale : `HexagonalArchitectureTest` doit rester vert.
- Les règles métier (bornes de la note, longueur du commentaire, avis négatif, calcul de la moyenne) vivent dans le **domaine** et sont testées sans Spring.
- La notification passe par un **port sortant** dédié.

## Critères d'acceptation

- [ ] Tous les cas du tableau sont couverts par des tests.
- [ ] Tests : domaine (JUnit pur), service (Mockito sur les ports), controller (`@WebMvcTest`), adapter de persistance (`@DataJpaTest`).
- [ ] `./mvnw verify` vert, `HexagonalArchitectureTest` compris.
- [ ] Démo `curl` : avis accepté, refus « non acheté », refus « doublon », alerte dans les logs pour une note de 1.

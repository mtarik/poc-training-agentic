# SHOP-102 — [FEATURE] Gestion du stock et annulation de commande

**Type** : Story · **Priorité** : Haute · **Composant** : order, catalog

## Contexte

Aujourd'hui une commande peut être passée même si le produit est en rupture (ex. « Jeu de société Agile », stock 0), et le stock n'est jamais mis à jour. Par ailleurs, il est impossible d'annuler une commande.

Voir `docs/specs/regles-metier.md` §3.2 et §3.3.

## User stories

1. **En tant que** client, **je veux** que ma commande soit refusée si un produit n'est pas disponible en quantité suffisante, **afin de** ne pas payer un produit qui ne sera pas livré.
2. **En tant que** client, **je veux** pouvoir annuler une commande non expédiée, **afin de** changer d'avis.

## Critères d'acceptation

- [ ] `POST /api/orders` renvoie **422** si le stock d'au moins un produit est insuffisant ; aucun stock n'est modifié dans ce cas.
- [ ] À la création réussie, le stock de chaque produit est décrémenté.
- [ ] Nouvel endpoint `POST /api/orders/{id}/cancel` :
  - `200` + commande au statut `CANCELLED` si statut initial `CREATED` ou `PAID` ;
  - `422` si la commande est `SHIPPED` ou déjà `CANCELLED` ;
  - `404` si la commande n'existe pas.
- [ ] L'annulation réintègre le stock.
- [ ] Tests unitaires (service) et tests web (controller) couvrant les cas nominaux et d'erreur.

## Hors périmètre

- Paiement et expédition (transitions `PAID` / `SHIPPED`).

## Question ouverte

Que se passe-t-il si deux clients commandent le dernier exemplaire au même moment ? (à discuter, voire à traiter)

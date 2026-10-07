# Règles métier — Shop API

Ce document est la **référence fonctionnelle**. En cas d'écart entre ce document et le code, c'est ce document qui fait foi (sauf mention contraire dans un ticket).

## 1. Catalogue

- Un produit est identifié par un SKU unique au format `ABC-1234`.
- Un produit peut être en promotion : `promoPercent` entre 1 et 90. `null` ou `0` = pas de promotion.
- Le **prix unitaire remisé** = `prix × (100 − promoPercent) / 100`, arrondi au centime (HALF_UP).
- Le stock ne peut jamais être négatif.

## 2. Clients

- Deux niveaux de fidélité : `STANDARD` et `GOLD`.
- Les clients `GOLD` bénéficient de **5 % de remise sur le sous-total** de la commande (après promotions, avant frais de port).
- Le champ `internalNotes` est **strictement interne** (service client). Il ne doit jamais être exposé par l'API publique.
- L'email est obligatoire, unique et doit être un email valide.

## 3. Commandes

### 3.1 Calcul du montant

```
total ligne = prix unitaire (remisé si promotion) × quantité
sous-total  = Σ totaux lignes, puis remise GOLD éventuelle
total       = sous-total + frais de port
```

Tous les montants sont en euros, en `BigDecimal`, avec 2 décimales.

### 3.2 Stock

- Une commande ne peut être créée que si **chaque produit** a un stock suffisant.
- Sinon, la commande est refusée en totalité (HTTP 422) avec un message indiquant le ou les produits concernés.
- À la création, le stock de chaque produit est décrémenté de la quantité commandée.

### 3.3 Cycle de vie

```
CREATED ──payer──▶ PAID ──expédier──▶ SHIPPED
   │                 │
   └──annuler──▶ CANCELLED ◀──annuler──┘
```

- Une commande peut être annulée uniquement si elle est `CREATED` ou `PAID`.
- Une commande `SHIPPED` ou déjà `CANCELLED` ne peut pas être annulée (HTTP 422).
- L'annulation **réintègre le stock** des produits commandés.

## 4. Frais de port

Le calcul dépend du pays de livraison, du sous-total (après remise GOLD), du poids total et de l'option express.

| Zone | Pays | Tarif de base | Supplément poids | Livraison offerte |
|---|---|---|---|---|
| France | FR | 4,90 € | +5,00 € si poids > 5 kg | sous-total **≥ 50 €** |
| UE | BE, DE, ES, IT, LU, NL | 9,90 € | +8,00 € si poids > 5 kg | sous-total ≥ 100 € |
| International | autres | 19,90 € | +2,00 € par kg entamé au-delà de 1 kg | jamais |

Règles complémentaires :

1. **Express** : les frais calculés sont doublés. Si la livraison est offerte, l'express est facturé 9,90 € forfaitaire.
2. **GOLD** : −50 % sur les frais de port (ne s'applique pas au forfait express d'une livraison offerte).
3. Pays non renseigné : France par défaut.
4. Les codes pays sont insensibles à la casse.

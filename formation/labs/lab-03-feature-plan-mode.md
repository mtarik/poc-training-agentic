# Lab 03 : Développer une feature avec le Plan mode

⏱ 1 h 30 · 🎯 Faire planifier l'agent, **challenger le plan**, puis implémenter par étapes vérifiables.

Ticket : [SHOP-102](../../docs/tickets/SHOP-102.md) (gestion du stock et annulation de commande).

## 1. Planifier (25 min)

`/clear`, puis passez en **Plan mode** avec `Shift+Tab` (jusqu'à voir « plan mode »). En Plan mode, l'agent lit et réfléchit mais **ne modifie rien**.

```text
Lis docs/tickets/SHOP-102.md et les règles métier associées.
Propose un plan d'implémentation détaillé :
- fichiers créés / modifiés ;
- signature des nouvelles méthodes et du nouvel endpoint ;
- gestion des erreurs et codes HTTP ;
- liste des tests à écrire ;
- découpage en étapes, chacune vérifiable par ./mvnw test.
Signale les ambiguïtés du ticket plutôt que de trancher seul.
```

**Challengez le plan** avant de l'accepter. Questions à poser :

- Où est vérifié le stock : avant ou pendant la boucle sur les lignes ? Que se passe-t-il si la 2ᵉ ligne est en rupture alors que la 1ʳᵉ a déjà décrémenté le stock ?
- Où mettre la logique de transition de statut ? (en hexagonal : dans l'agrégat `Order` du domaine, par exemple `order.cancel()`, pas dans le service)
- Le décrément de stock : `Product.decreaseStock(qty)` dans le domaine ? Quel port sortant pour sauvegarder les produits ?
- Faut-il un nouveau use case `CancelOrderUseCase` ? (oui : un port entrant par intention)
- Deux commandes simultanées sur le dernier exemplaire ? (question ouverte du ticket)
- Le plan modifie-t-il des fichiers qui n'ont rien à voir ?

Demandez des modifications du plan jusqu'à être satisfait.

## 2. Implémenter par étapes (50 min)

Acceptez le plan, puis demandez une étape à la fois :

```text
Implémente uniquement l'étape 1 du plan, avec ses tests. Lance ./mvnw test. Arrête-toi ensuite.
```

Entre chaque étape :

1. relisez le diff (`git diff`) ;
2. commitez si c'est bon (`git commit -am "SHOP-102 étape N"`) ;
3. sinon, recadrez ou utilisez `Esc` `Esc` / `/rewind` pour revenir en arrière.

> 💡 Le mode « acceptation automatique des modifications » (`Shift+Tab`) accélère l'exécution d'un plan validé. Utilisez-le seulement quand le plan est clair et le périmètre maîtrisé.

## 3. Vérifier de bout en bout (15 min)

```bash
./mvnw spring-boot:run
# Commande refusée : stock insuffisant (produit 10, stock 0) -> 422 attendu
curl -s -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"customerId":3,"country":"FR","lines":[{"productId":10,"quantity":1}]}'
# Annulation d'une commande CREATED -> 200
curl -s -X POST http://localhost:8080/api/orders/3/cancel
# Annulation d'une commande SHIPPED -> 422
curl -s -X POST http://localhost:8080/api/orders/1/cancel
```

## ✅ Critères de réussite

- [ ] Tous les critères d'acceptation de SHOP-102 sont cochés.
- [ ] Les règles (stock suffisant, transitions de statut) sont dans le domaine et testées sans Spring ; `HexagonalArchitectureTest` est vert.
- [ ] Un commit par étape.
- [ ] Une réponse argumentée à la question de la concurrence (implémentée ou documentée).

## 💬 À débriefer

- Combien de modifications avez-vous demandées sur le plan ? Lesquelles auraient coûté cher si elles avaient été découvertes après l'implémentation ?
- L'agent a-t-il soulevé les ambiguïtés de lui-même ?
- Plan mode vs prompt « ne modifie rien » : quelle différence ?

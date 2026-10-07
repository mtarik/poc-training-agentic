# Lab 04 : Générer des tests utiles

⏱ 1 h · 🎯 Obtenir des tests qui **protègent** vraiment, pas seulement de la couverture.

Ticket : [SHOP-103](../../docs/tickets/SHOP-103.md).

## 1. Mesurer l'existant (5 min)

```bash
./mvnw test
```

Ouvrez `target/site/jacoco/index.html` et notez la couverture des packages `domain.order` et `application.service`.

## 2. Générer (25 min)

`/clear`, puis :

```text
Lis docs/tickets/SHOP-103.md.
Couvre les commandes par des tests, couche par couche :
- domaine (Order, OrderLine) en JUnit pur : c'est là que sont les règles de calcul ;
- application (OrderService) avec Mockito sur les ports : uniquement l'orchestration.
Avant d'écrire, liste les cas de test que tu prévois, sous forme de tableau
(couche | scénario | données | résultat attendu) et attends ma validation.
Contraintes : ne modifie pas le code de production ; si un test révèle un comportement
qui contredit docs/specs/regles-metier.md, ne l'ajuste pas : signale-le.
```

Relisez le tableau. Manque-t-il des cas limites ? Ajoutez-les avant de valider.

## 3. Juger la qualité des tests (20 min)

Une couverture élevée ne prouve rien. Faites le **test du mutant** à la main :

1. Introduisez volontairement un bug dans `Order.computeTotals` ou `Order.addLine` (par exemple, retirez la remise GOLD ou remplacez `HALF_UP` par `DOWN`).
2. Lancez `./mvnw test`. **Au moins un test doit échouer.**
3. Annulez votre modification (`git checkout src/main`).

Répétez avec 2 ou 3 mutants. Pour chaque mutant qui survit, demandez à l'agent :

```text
Si je remplace <X> par <Y> dans Order, aucun test n'échoue.
Ajoute le test qui manque.
```

## 4. Mesurer à nouveau (10 min)

```bash
./mvnw test
```

Comparez la couverture. Relisez les tests produits : sont-ils lisibles ? Un nouveau venu comprendrait-il la règle métier en les lisant ?

## ✅ Critères de réussite

- [ ] Couverture de lignes de `Order`, `OrderLine` et `OrderService` ≥ 90 %.
- [ ] Les tests du domaine n'utilisent ni Spring ni Mockito.
- [ ] Au moins 3 mutants « tués ».
- [ ] Aucun test qui dépend de `data.sql`.

## 💬 À débriefer

- L'agent a-t-il essayé de modifier le code de production ou d'adapter une assertion pour faire passer un test ?
- Quels cas limites l'agent a-t-il oubliés ? Lesquels a-t-il trouvés et que vous n'aviez pas vus ?
- Pour aller plus loin : un outil comme PIT (mutation testing) automatise l'étape 3.

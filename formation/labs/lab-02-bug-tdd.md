# Lab 02 : Corriger un bug en boucle TDD

⏱ 1 h · 🎯 Donner à l'agent un moyen de **se vérifier** et le cadrer pour éviter les corrections « au hasard ».

Ticket : [SHOP-101](../../docs/tickets/SHOP-101.md) (montant faux pour les produits en promotion).

## 1. Reproduire à la main (10 min)

```bash
./mvnw spring-boot:run
curl -s -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"customerId":3,"country":"FR","lines":[{"productId":3,"quantity":3}]}'
```

Constatez `lineTotal: 36.00` au lieu de `108.00`.

## 2. Mauvaise approche, pour comparer (10 min, optionnel)

Sur une branche jetable :

```text
Il y a un bug dans les commandes, corrige-le.
```

Observez : l'agent trouve-t-il le bon bug ? En corrige-t-il d'autres au passage ? Écrit-il un test ? Revenez ensuite à `main` (`git checkout main` et `git branch -D` de la branche jetable).

## 3. Bonne approche : TDD guidé (30 min)

`/clear`, puis :

```text
Lis docs/tickets/SHOP-101.md.
1. Localise la cause du bug et explique-la-moi, sans rien modifier.
```

Validez l'explication, puis :

```text
2. Le bug est-il dans le domaine, l'application ou un adapter ? Écris le test au niveau le plus
   bas possible (architecture hexagonale : un test du domaine en JUnit pur, sans Spring ni mock,
   au style de ProductTest) qui reproduit exactement le scénario du ticket.
3. Lance ce test seul et montre-moi qu'il échoue pour la bonne raison.
```

> 💡 Si l'agent se lance dans un test Mockito de `OrderService` ou un `@SpringBootTest`, recadrez-le : le bug est dans le domaine, et un test du domaine s'exécute en quelques millisecondes. C'est un des bénéfices concrets de l'hexagonal.

Vérifiez vous-même que le test est rouge **sur une assertion** (et non sur une erreur de compilation ou un mock mal configuré). Puis :

```text
4. Applique la correction minimale. Ne refactore rien d'autre.
5. Relance le test, puis toute la suite ./mvnw test.
6. Résume : cause racine, correction, test ajouté.
```

## 4. Relire (10 min)

```bash
git diff
```

- La correction est-elle minimale ?
- Le test vérifie-t-il bien `lineTotal` **et** `subtotal` ?
- Existe-t-il un cas non couvert ? (produit en promotion avec quantité 1, client GOLD…) Demandez à l'agent de les ajouter si besoin.

Commitez : `git commit -am "SHOP-101 corrige le total des lignes en promotion"`.

## ✅ Critères de réussite

- [ ] Un test rouge avant correction, vert après.
- [ ] Un diff de production de quelques lignes au plus.
- [ ] `./mvnw test` vert.

## 💬 À débriefer

- Pourquoi demander d'abord d'**expliquer** avant de corriger ?
- Pourquoi exiger de **voir le test rouge** ?
- Avez-vous dû corriger l'agent sur un import ou une annotation Spring Boot 4 ? Que faut-il ajouter au `CLAUDE.md` ?

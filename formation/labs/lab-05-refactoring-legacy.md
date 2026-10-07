# Lab 05 : Refactorer du code legacy sous filet de sécurité

⏱ 1 h 15 · 🎯 Faire refactorer l'agent **sans changer le comportement**, en séparant clairement refactoring et correction de bugs.

Ticket : [SHOP-104](../../docs/tickets/SHOP-104.md) · Cible : `domain/shipping/ShippingCostCalculator.java`.

> Contrainte d'architecture : le calculateur est dans le **domaine**. Le refactoring doit rester en Java pur, sans `@Component` ni autre dépendance Spring. `HexagonalArchitectureTest` vous le rappellera si besoin.

## 1. Comprendre (10 min)

```text
Explique ShippingCostCalculator.calc sous forme d'un tableau de décision
(pays, seuil, poids, express, niveau -> frais). Ne modifie rien.
Compare ensuite avec la section 4 de docs/specs/regles-metier.md et liste les écarts.
```

Gardez la liste des écarts : elle servira pour la PR.

## 2. Poser le filet : tests de caractérisation (25 min)

```text
Écris des tests de caractérisation pour ShippingCostCalculator.calc dans
ShippingCostCalculatorTest (JUnit 5, @ParameterizedTest + @CsvSource).
Règle absolue : la valeur attendue est celle que renvoie le code ACTUEL, même si elle
contredit la spec. Pour chaque écart avec la spec, ajoute un commentaire "// ECART SPEC : ...".
Couvre toutes les branches, y compris les bornes (50 / 50.01, 5000 / 5001 g, pays en minuscules, pays null).
Lance les tests : ils doivent tous être verts sans toucher au code.
```

Vérifiez la couverture des branches de `ShippingCostCalculator` dans JaCoCo (objectif : 100 %). Commitez.

## 3. Refactorer (30 min)

Passez en Plan mode :

```text
Propose un refactoring de ShippingCostCalculator selon SHOP-104.
Contraintes :
- les tests de caractérisation ne doivent PAS être modifiés (seule l'adaptation de l'appel est permise) ;
- aucun écart avec la spec ne doit être corrigé dans ce refactoring ;
- pas de nouvelle dépendance.
Propose 2 designs possibles avec leurs compromis avant de choisir.
```

Choisissez un design, puis faites implémenter **par petites étapes**, tests verts à chaque étape.

## 4. Vérifier (10 min)

```bash
git diff HEAD~1 -- src/test   # les valeurs attendues ont-elles changé ?
./mvnw test
```

Demandez enfin :

```text
Rédige la description de PR : objectif, design choisi, preuve de non-régression,
et la liste des écarts avec la spec à traiter dans des tickets séparés.
```

## ✅ Critères de réussite

- [ ] Tests de caractérisation commités **avant** le refactoring.
- [ ] Aucune valeur attendue modifiée.
- [ ] Plus de `static`, de `double` ni de `String` pour le niveau de fidélité.
- [ ] Le domaine reste pur (`HexagonalArchitectureTest` vert).
- [ ] Écarts avec la spec documentés, non corrigés.

## 💬 À débriefer

- L'agent a-t-il « corrigé » un écart de lui-même pendant le refactoring ? Pourquoi est-ce un problème ?
- Les tests de caractérisation figent-ils aussi des bugs ? Est-ce grave ?
- Quelle taille d'étape a le mieux fonctionné ?

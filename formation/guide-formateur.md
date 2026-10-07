# Guide formateur

> 🧑‍🏫 Document destiné au formateur. Il est publié avec le dépôt : stagiaires, consultez-le **après** avoir fait les labs, pour comparer vos résultats.

## 1. Préparation (J-7 à J-1)

- [ ] Vérifier les **licences** : chaque participant a un compte Claude (Team / Enterprise de préférence) ou une clé Console API avec un budget plafonné.
- [ ] Vérifier la **politique de sécurité** du poste : installation de Claude Code autorisée, proxy d'entreprise (variables `HTTPS_PROXY`, certificats), accès à `claude.ai`, `api.anthropic.com` et Maven Central.
- [ ] Envoyer aux participants : prérequis (JDK 21+, Git, IDE) et `scripts/check-setup.*` à lancer avant la session.
- [ ] Partager l'URL du dépôt GitHub. Les solutions y sont publiées : rappeler aux stagiaires de chercher par eux-mêmes avant de les consulter, et de s'en servir pour repartir sur une base propre s'ils sont en retard (section « Repartir d'une solution » du README).
- [ ] Faire tourner `./mvnw verify` une fois sur un poste type pour pré-remplir le cache Maven.
- [ ] Faire chaque lab soi-même avec la version courante de Claude Code : l'outil évolue vite (noms de commandes, raccourcis).

## 2. Carte des défauts volontaires

| Id | Où | Défaut | Lab |
|---|---|---|---|
| B1 | `domain/order/OrderLine.of` | ligne en promotion : `lineTotal` = prix remisé **sans × quantité** (copier-coller de `discountedPrice()`) | 02 |
| B2 | `application/service/OrderService.place` + domaine | aucun contrôle ni décrément de stock | 03 |
| F1 | `adapter/in/web/order` | annulation de commande inexistante (ni use case, ni endpoint) | 03 |
| T1 | `domain/order`, `OrderService` | aucun test sur le calcul ni sur l'orchestration des commandes | 04 |
| R1 | `domain/shipping/ShippingCostCalculator` | legacy : static, `double`, `String`, `if` imbriqués, nombres magiques ; appelé en dur par `Order.computeTotals` | 05 |
| R1-a | idem | **écart spec** : livraison offerte FR si `> 50` au lieu de `≥ 50` | 05 |
| R1-b | idem | **écart spec** : seul `fr` minuscule est géré ; `de` part en tarif international | 05 |
| S1 | `adapter/out/persistence/customer/CustomerSearchJdbcAdapter` | **injection SQL** par concaténation dans `searchByName` | 07 |
| S2 | `adapter/in/web/customer/CustomerController` | **objet du domaine exposé** comme DTO : fuite de `internalNotes` (données sensibles RGPD), y compris via `/search` | 07 |
| S3 | `CustomerController.create` + `RegisterCustomerUseCase` | le use case prend l'objet du domaine en entrée, sans commande ni `@Valid` : email non validé, **mass assignment** (`loyaltyLevel: GOLD`, `id`, `internalNotes`) | 07 |
| P1 | `OrderPersistenceAdapter.findAll` → `OrderJpaEntity.toDomain` | **N+1** caché dans le mapping JPA → domaine : 1 requête orders + 1 par client + 1 par liste de lignes + 1 par produit (~19 à 23 requêtes pour 8 commandes) | 07 |
| P2 | `GET /api/orders` | pas de pagination | 07 (bonus) |
| A1 | `customer` (toutes couches) | contre-exemple d'hexagonal « de façade » : la structure en couches est respectée (ArchUnit est vert) mais le domaine anémique sert de DTO. Montre les **limites** d'ArchUnit : il vérifie les dépendances, pas la qualité du modèle | 01, 07 |

Le use case de la démo (SHOP-201) n'a pas de défaut : voir [demo/README.md](demo/README.md).

Les labs 01 et 06 n'ont pas de défaut dédié : ils portent sur l'outillage.

## 3. Corrigés et points d'attention par lab

### Lab 00 : installation

- Pièges fréquents : Git for Windows absent (requis sous Windows), proxy d'entreprise, PowerShell `ExecutionPolicy`.
- Question 3 : vérifier si l'agent a **lu** le code ou résumé de mémoire. Bon moment pour montrer la trace des outils appelés.
- Question 5 : `./mvnw test` est dans `allow` (`.claude/settings.json`), donc pas de confirmation. `mvnw.cmd` (Windows) n'y est pas : confirmation demandée. C'est une transition naturelle vers les permissions.

### Lab 01 : `CLAUDE.md`

- Sans contexte, l'essai A imite souvent `CustomerController` (objet du domaine renvoyé tel quel), ou prend un raccourci : le controller ou le service appelle directement `OrderJpaRepository`. Dans ce cas `HexagonalArchitectureTest` casse : excellent point de discussion (« l'agent ne l'a pas vu car il n'a pas lancé les tests »).
- Avec le `CLAUDE.md` de référence, on attend un `FindCustomerOrdersUseCase`, une méthode `findByCustomerId` sur le port `OrderRepository` et son implémentation dans l'adapter.
- `/init` génère souvent un document descriptif, parfois trop long. Insister : **ce que l'agent ne peut pas deviner**.
- Référence : `formation/solutions/CLAUDE.md`.
- Le résultat est non déterministe : un même prompt peut donner des résultats différents. Faire comparer entre binômes.

### Lab 02 : SHOP-101

Correction attendue (`domain/order/OrderLine.of`) :

```java
if (product.isOnPromotion()) {
    unitPrice = product.discountedPrice();
    lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
}
```

Test type : un test **du domaine**, en JUnit pur (aucun mock, aucun Spring, exécuté en quelques millisecondes) :

```java
@Test
void of_shouldMultiplyDiscountedPriceByQuantity_whenProductIsOnPromotion() {
    Product effectiveJava = new Product(3L, "BOO-0003", "Effective Java", Category.BOOKS,
            new BigDecimal("45.00"), 20, 800, 15);

    OrderLine line = OrderLine.of(effectiveJava, 3);

    assertThat(line.unitPrice()).isEqualByComparingTo("36.00");
    assertThat(line.lineTotal()).isEqualByComparingTo("108.00");
}
```

Vérifié sur le code livré : rouge avec `expected: 108.00 but was: 36.00`.

Pièges :

- L'agent écrit un test Mockito de `OrderService`, voire un `@SpringBootTest`. Ça marche, mais c'est lourd : faire remarquer que le bug est dans le domaine, donc testable sans infrastructure. C'est un bénéfice direct de l'hexagonal.
- L'agent utilise `@MockBean` (supprimé en Boot 4) au lieu de `@MockitoBean`, ou l'ancien package de `@WebMvcTest`. C'est l'occasion d'enrichir `CLAUDE.md`.
- Test rouge pour une mauvaise raison (NPE sur un client sans `loyaltyLevel`, mock mal configuré). Exiger de voir le message d'échec.
- Approche « vague » : l'agent peut repérer d'autres défauts (stock, N+1) et tout modifier. Bonne illustration du besoin de cadrage.

### Lab 03 : SHOP-102

Éléments attendus :

- **Placement hexagonal** : `Product.decreaseStock(qty)` / `increaseStock(qty)` dans le domaine (lève `BusinessException`), `Order.cancel()` qui porte la règle de transition, un `CancelOrderUseCase` + `POST /api/orders/{id}/cancel`, et la sauvegarde des produits via le port `ProductRepository`. Refuser un plan qui met les règles dans le service ou le controller.
- **Valider tout le stock avant de décrémenter** (ou s'appuyer sur le rollback transactionnel : une `BusinessException` est une `RuntimeException`, donc la transaction est annulée. Les deux approches sont valables, à faire expliquer).
- Message 422 listant les produits en rupture.
- Transitions de statut : idéalement une méthode `Order.cancel()` qui porte la règle (modèle riche) ou `OrderStatus.canBeCancelled()`.
- `POST /api/orders/{id}/cancel` qui renvoie `OrderResponse`.
- Réintégration du stock à l'annulation.
- **Concurrence** : lecture-modification-écriture sans verrou, donc risque de survente. Réponses acceptables : `@Version` sur `Product` (verrou optimiste, avec retry ou 409), `@Lock(PESSIMISTIC_WRITE)` sur la lecture du produit, ou `UPDATE product SET stock = stock - :q WHERE id = :id AND stock >= :q` en vérifiant le nombre de lignes modifiées. Valoriser ceux qui ont **fait soulever** la question par l'agent en Plan mode.

### Lab 04 : SHOP-103

Cas attendus au minimum :

- **Domaine** (`Order`, `OrderLine`, JUnit pur) : commande simple, plusieurs lignes, promotion avec quantité > 1, remise GOLD (vérifier l'arrondi : 89,90 × 0,95 = 85,405 → 85,41), poids total, frais de port appliqués, total = sous-total + port.
- **Application** (`OrderService`, Mockito sur les ports) : client inconnu (404), produit inconnu (404), commande sauvegardée via le port, aucune sauvegarde en cas d'erreur.

Mutants utiles à proposer : supprimer la remise GOLD ; `HALF_UP` → `DOWN` ; `subtotal.add(shippingCost)` → `subtotal` ; `totalWeightGrams +=` → `totalWeightGrams =`. Ce dernier survit souvent, car il faut un test où le poids fait franchir le seuil de 5 kg avec plusieurs lignes.

Note : `Order.computeTotals` appelle `ShippingCostCalculator` en statique, donc les tests du domaine dépendent des vraies règles de port. C'est une bonne transition vers le lab 05 : passer une politique de frais de port en paramètre rend l'agrégat testable isolément.

### Lab 05 : SHOP-104

Valeurs de caractérisation vérifiées sur le code livré :

| country | subtotal | weight (g) | level | express | → frais | Remarque |
|---|---|---|---|---|---|---|
| FR | 50.00 | 1000 | STANDARD | false | **4.90** | ÉCART SPEC (≥ 50 → offert) |
| FR | 50.01 | 1000 | STANDARD | false | 0.00 | |
| FR | 30.00 | 6000 | STANDARD | false | 9.90 | |
| FR | 30.00 | 1000 | GOLD | true | 4.90 | (4,90 × 2) / 2 |
| FR | 80.00 | 1000 | GOLD | true | 9.90 | forfait express, pas de −50 % |
| fr | 30.00 | 1000 | STANDARD | false | 4.90 | |
| de | 30.00 | 1000 | STANDARD | false | **19.90** | ÉCART SPEC (casse) |
| DE | 100.00 | 1000 | STANDARD | false | 0.00 | |
| DE | 60.00 | 6000 | GOLD | true | 17.90 | (9,90 + 8) × 2 / 2 |
| US | 10.00 | 2500 | STANDARD | false | 23.90 | 2 kg entamés au-delà de 1 kg |
| US | 10.00 | 1000 | STANDARD | false | 19.90 | |
| null | 30.00 | 1000 | STANDARD | false | 4.90 | FR par défaut |

Designs possibles, **tous en Java pur dans le domaine** : enum `ShippingZone` (pays, tarif, supplément, seuil) + une classe `ShippingPolicy` instanciable, passée à `Order.computeTotals(policy)` par `OrderService`. Si l'on veut l'injecter, on la déclare en `@Bean` dans une classe de configuration **hors du domaine** (par exemple `adapter/config` ou la classe principale). Variante : pattern Strategy (une politique par zone). Faire discuter du risque de **sur-ingénierie** : pour 3 zones, une enum suffit.

Piège hexagonal : l'agent annote la nouvelle classe avec `@Component` dans le domaine. ArchUnit (règle `domain_must_be_pure_java`) casse le build. Laisser arriver : c'est démonstratif.

Piège majeur : l'agent « corrige » le `> 50` en `>= 50` pendant le refactoring et ajuste le test. À détecter avec `git diff` sur `src/test`.

### Lab 06 : personnalisation

- Solutions : `formation/solutions/.claude/` (skills `fix-ticket` et `new-use-case`, agents `spring-reviewer` et `test-writer`, hooks `protect-prod-config`, `compile-check` et `architecture-check`, `settings.json`).
- Les hooks sont testés sous Git Bash (Windows) et bash (Linux/macOS). Ils utilisent `grep`/`sed` (pas besoin de `jq`).
- Hook `Stop` + ArchUnit : vérifié sur trois cas (code propre → laisse finir ; `@Entity` dans le domaine → bloque avec le détail de la violation ; `stop_hook_active: true` → laisse finir pour éviter une boucle).
- Le hook de compilation utilise `-o` (offline) : le cache Maven doit déjà être rempli (`./mvnw verify` au moins une fois).
- La `description` d'un skill ou d'un subagent détermine quand l'agent l'utilise spontanément. Faire tester une description vague puis une description précise.
- Débrief permission vs hook : `deny` est natif, simple et fiable ; un hook permet une logique (chemins, contenu, message explicatif à l'agent, journalisation).

### Lab 07 : SHOP-105

- **S1** : `curl "http://localhost:8080/api/customers/search?name=%27)%20OR%201%3D1%20--%20"` renvoie tous les clients (avec leurs `internalNotes`). Correction dans l'adapter : `jdbcTemplate.query(sql, mapper, "%" + name + "%")` avec un `?`, ou une méthode Spring Data `findByLastNameContainingIgnoreCase` dans `CustomerJpaRepository`. Le port `CustomerSearchPort` ne change pas : bel exemple d'isolation.
- **S2** : `GET /api/customers/2` renvoie `internalNotes` (« A menacé de porter plainte… »). Correction : record `CustomerResponse` dans `adapter/in/web/customer`.
- **S3** : `POST /api/customers` avec `{"email":"x","loyaltyLevel":"GOLD","internalNotes":"..."}` est accepté. Correction : `RegisterCustomerRequest` (`@Email @NotBlank`, `@Valid`) → `RegisterCustomerCommand` (sans `loyaltyLevel` ni `internalNotes`) → le service construit le `Customer`.
- Débat utile : ArchUnit est vert alors que S2 et S3 existent. Une règle d'architecture vérifie des dépendances, pas des intentions. On pourrait ajouter une règle « les controllers ne renvoient pas de classes de `..domain..` », à faire écrire par l'agent.
- Faux positifs fréquents : « pas d'authentification » (vrai, mais hors périmètre du POC, à discuter), « CSRF » (non pertinent pour une API stateless sans cookie), « H2 console exposée » (elle ne l'est pas).
- **P1** : 19 à 23 requêtes pour les 8 commandes du jeu de données (mesuré). Le N+1 est **dans l'adapter** (`OrderJpaEntity.toDomain`) : c'est là qu'il se corrige, sans toucher au port ni au domaine. Solutions : `@EntityGraph(attributePaths = {"customer", "lines", "lines.product"})` sur une méthode dédiée de `OrderJpaRepository`, `JOIN FETCH` (attention au produit cartésien et à la pagination en mémoire, avertissement HHH90003004), ou `hibernate.default_batch_fetch_size`. Après correction : 1 à 3 requêtes.

### Lab 08 : MCP et headless

- Si Context7 n'est pas accessible (proxy), se rabattre sur un serveur MCP local, ou faire la partie 2 uniquement.
- Vérifier la syntaxe `claude mcp add` avec `claude mcp --help` sur la version installée.
- `--allowedTools` en headless : sans lui, une commande nécessitant une permission échoue en mode non interactif. C'est une bonne démonstration.

## 4. Animation

- **Binômes** recommandés : un « pilote » au clavier, un « relecteur » qui challenge l'agent. Inverser à chaque lab.
- Commencer chaque lab par 2 minutes de démo au vidéoprojecteur, finir par 5 minutes de débrief.
- Valoriser les **échecs de l'agent** : ce sont les meilleurs moments pédagogiques. Tenir au tableau une liste « erreurs vues de l'agent ».
- Rappeler régulièrement : `git commit` avant chaque tâche, `/clear` entre les tâches.
- Non-déterminisme : deux binômes avec le même prompt n'auront pas le même code. C'est normal, et c'est l'occasion de comparer les résultats.

## 5. Rétrospective finale (questions)

1. Sur quelles tâches l'agent vous a-t-il fait gagner le plus de temps ?
2. Sur lesquelles vous a-t-il fait perdre du temps, ou pris un risque ?
3. Qu'allez-vous mettre en place dès lundi sur votre projet ? (`CLAUDE.md`, un skill, un hook…)
4. Quelles questions restent ouvertes (sécurité, confidentialité client, coût) ?

## 6. Remise à zéro du projet entre deux sessions

```bash
git checkout main && git reset --hard <commit-initial> && git clean -fd
```

Ou re-cloner le dépôt de référence.

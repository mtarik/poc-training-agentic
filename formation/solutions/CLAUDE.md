# Shop API

API REST e-commerce (catalogue, clients, commandes) en **architecture hexagonale**. Support de formation : le code contient des défauts volontaires décrits dans `docs/tickets/`.

## Stack

- Java 21, Spring Boot 4.0 (Spring MVC, Data JPA, Validation, Actuator), Jackson 3
- H2 en mémoire, schéma généré par Hibernate, données dans `src/main/resources/data.sql`
- Tests : JUnit 5, Mockito, AssertJ, MockMvc, ArchUnit. Couverture : JaCoCo

## Commandes

- Build + tous les tests : `./mvnw verify` (Windows : `mvnw.cmd verify`)
- Un seul test : `./mvnw test -Dtest=ReviewServiceTest`
- Règles d'architecture seules : `./mvnw test -Dtest=HexagonalArchitectureTest`
- Lancer l'appli : `./mvnw spring-boot:run` → http://localhost:8080

## Architecture hexagonale

```
fr.formation.shop
├── domain/                     Java pur : modèles, règles métier, exceptions métier
├── application/
│   ├── port/in/<contexte>/     use cases (interfaces XxxUseCase) + commandes (records XxxCommand)
│   ├── port/out/               ports sortants (interfaces : XxxRepository, XxxPort)
│   └── service/                implémentation des use cases (@Service, @Transactional)
└── adapter/
    ├── in/web/<contexte>/      controllers REST + DTO (XxxRequest, XxxResponse)
    └── out/persistence/<ctx>/  entités XxxJpaEntity, Spring Data XxxJpaRepository, XxxPersistenceAdapter
        out/<autre>/            autres adapters sortants (notification, client HTTP...)
```

**Règles de dépendance** (vérifiées par `HexagonalArchitectureTest`, qui doit toujours être vert) :

- `domain` ne dépend de **rien** : ni Spring, ni JPA, ni Jackson, ni `application`, ni `adapter`.
- `application` ne dépend que de `domain`. Seuls `@Service` et `@Transactional` sont tolérés.
- `adapter.in` n'utilise que les **ports entrants** (jamais un service, un port sortant ou un adapter sortant).
- `adapter.out` implémente un **port sortant** et ne connaît rien du côté entrant.
- Les entités JPA s'appellent `XxxJpaEntity` et vivent uniquement dans `adapter.out.persistence`.

**Le contexte `catalog` est la référence.** S'en inspirer pour tout nouveau code, dans chaque couche.

## Où mettre quoi

| Besoin | Emplacement |
|---|---|
| Règle métier, invariant, calcul | `domain` (méthode de l'objet métier ou factory) |
| Orchestration : charger, vérifier l'existence, appeler le domaine, sauvegarder, notifier | `application/service` |
| Validation de format d'entrée (Bean Validation) | DTO `XxxRequest` dans `adapter.in.web` |
| Mapping DTO ↔ commande | méthode `toCommand()` du `XxxRequest` |
| Mapping domaine ↔ JPA | `XxxJpaEntity.fromDomain()` / `toDomain()` |
| Appel à un système externe | nouveau port dans `application/port/out` + adapter dans `adapter/out/<techno>` |

## Conventions

- Injection par constructeur uniquement. Les adapters sortants sont package-private (`class XxxPersistenceAdapter`).
- Erreurs : `NotFoundException` (404) et `BusinessException` (422) du package `domain.exception`, traduites par `GlobalExceptionHandler`.
- Montants : `BigDecimal`, 2 décimales, `RoundingMode.HALF_UP`. Jamais de `double`.
- SQL : Spring Data ou requêtes paramétrées. **Jamais de concaténation de chaînes.**
- Messages d'erreur et documentation en français, code en anglais.

## Tests

| Couche | Type de test | Modèle |
|---|---|---|
| domain | JUnit pur, sans Spring ni Mockito | `ProductTest` |
| application | Mockito sur les ports | `ProductServiceTest` |
| adapter.in.web | `@WebMvcTest` + `@MockitoBean` sur les use cases | `ProductControllerTest` |
| adapter.out.persistence | `@DataJpaTest` + `@Import(adapter)` | `ProductPersistenceAdapterTest` |

- Nommage : `methode_shouldComportement_whenCondition`.
- Spring Boot 4 : `@WebMvcTest` est dans `org.springframework.boot.webmvc.test.autoconfigure`, `@DataJpaTest` dans `org.springframework.boot.data.jpa.test.autoconfigure`, `@MockitoBean` remplace `@MockBean`.
- Aucun test ne dépend des données de `data.sql`.
- Pour un bug : écrire d'abord le test qui échoue, puis corriger.

## Règles métier

Référence : @docs/specs/regles-metier.md

## Interdits

- Ne jamais modifier ni désactiver `HexagonalArchitectureTest` pour faire passer le build.
- Ne pas modifier `src/main/resources/application-prod.yml` (géré par l'équipe Ops).
- Ne pas modifier les valeurs attendues d'un test existant sans le signaler explicitement.
- Ne pas ajouter de dépendance Maven sans le demander.

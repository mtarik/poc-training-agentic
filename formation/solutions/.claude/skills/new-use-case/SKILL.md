---
name: new-use-case
description: Implémente un nouveau cas d'usage métier de bout en bout en architecture hexagonale (domaine, ports, service, adapters web et persistance, tests à chaque couche). À utiliser dès qu'un ticket demande une nouvelle fonctionnalité ou un nouvel endpoint.
argument-hint: "[SHOP-XXX ou description du use case]"
---

# Nouveau use case : $ARGUMENTS

Tu travailles **de l'intérieur vers l'extérieur** de l'hexagone. Ne passe jamais à la couche suivante tant que la précédente n'est pas verte.

## 0. Cadrer (sans écrire de code)

1. Lis le ticket (si un identifiant `SHOP-XXX` est fourni : `docs/tickets/SHOP-XXX.md`) et `CLAUDE.md`.
2. Relis le contexte de référence `catalog` dans chaque couche.
3. Présente un plan sous forme de tableau **couche | fichier | rôle** couvrant : modèle(s) du domaine, use case(s) et commande(s), port(s) sortant(s) nouveaux ou modifiés, service, adapter web, adapter(s) sortant(s), tests.
4. Liste les règles métier et indique pour chacune **dans quelle couche** elle sera implémentée et testée.
5. Signale les ambiguïtés du ticket. **Attends la validation de l'utilisateur.**

## 1. Domaine (Java pur)

- Modèle(s) et invariants dans le constructeur ou une factory `create(...)`, qui lève une `BusinessException`.
- Tests JUnit purs (aucune annotation Spring, aucun mock). Lance-les.

## 2. Application

- Ports entrants : une interface `XxxUseCase` par intention + un record `XxxCommand`.
- Ports sortants : interfaces dans `application/port/out` (méthodes exprimées en langage métier, pas en SQL).
- Service `@Service` qui implémente le(s) use case(s) : orchestration uniquement, la règle métier reste dans le domaine.
- Test Mockito : chaque cas nominal et chaque cas d'erreur du ticket. Vérifie aussi les interactions qui **ne doivent pas** avoir lieu (`never()`, `verifyNoInteractions`).

## 3. Adapters

- Web : `XxxRequest` (Bean Validation + `toCommand()`), `XxxResponse` (`from(domain)`), controller qui n'appelle **que** des use cases. Test `@WebMvcTest` : 2xx, 400, 404, 422.
- Persistance : `XxxJpaEntity` (`fromDomain` / `toDomain`), `XxxJpaRepository`, `XxxPersistenceAdapter` package-private. Test `@DataJpaTest`.
- Autre système externe : adapter dans `adapter/out/<techno>` implémentant le port.

## 4. Vérifier

1. `./mvnw verify` : tous les tests, **y compris `HexagonalArchitectureTest`**.
2. Si une règle d'architecture échoue : corrige le code, **jamais la règle**.
3. Termine par : la liste des fichiers créés ou modifiés par couche, la couverture des critères d'acceptation, et des exemples `curl` (corps JSON sans accents, pour Git Bash sous Windows).

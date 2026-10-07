# Lab 07 : Revue de code, sécurité et performance

⏱ 1 h 15 · 🎯 Utiliser l'agent comme auditeur, en restant critique sur ses conclusions.

Ticket : [SHOP-105](../../docs/tickets/SHOP-105.md).

## 1. Audit de sécurité (30 min)

`/clear`, puis :

```text
Fais un audit de sécurité du package customer, comme un pentesteur.
Pour chaque problème : fichier:ligne, gravité (CVSS approximatif), scénario d'exploitation
concret (avec la requête curl), correction proposée. N'applique aucune correction.
```

Vous pouvez aussi essayer la commande intégrée `/security-review` (elle analyse les changements en cours sur la branche).

**Vérifiez chaque trouvaille vous-même** : lancez l'application et rejouez les `curl` proposés. Classez chaque trouvaille : ✅ confirmée · ❌ faux positif · ❓ non démontrée.

Puis faites corriger les problèmes de gravité haute **un par un**, chacun avec un test qui prouve la correction :

```text
Corrige l'injection SQL de CustomerSearchJdbcAdapter. Ajoute d'abord un test d'adapter
(@JdbcTest ou @DataJpaTest avec @Import de l'adapter) qui démontre la faille (rouge),
puis corrige avec une requête paramétrée.
```

Pour la fuite de données (`internalNotes`) et le mass assignment, demandez aussi **dans quelle couche** se trouve le problème : le controller expose directement l'objet du domaine `Customer` et l'accepte en entrée. La correction attendue est côté adapter web (`CustomerResponse`, `RegisterCustomerRequest` validé) et côté application (une commande `RegisterCustomerCommand` au lieu de l'objet du domaine) :

```text
Corrige la fuite d'internalNotes et le mass assignment de /api/customers en respectant
l'architecture hexagonale. Explique pourquoi un objet du domaine ne doit pas servir de DTO.
```

## 2. Performance : le N+1 (30 min)

1. Activez les logs SQL dans `application.yml` : `logging.level.org.hibernate.SQL: DEBUG`.
2. Lancez l'application, appelez `GET /api/orders` et **comptez** les requêtes.
3. Demandez :

```text
GET /api/orders émet N requêtes SQL pour 8 commandes (voir logs).
Indice : le mapping JPA → domaine (toDomain) de l'adapter de persistance déclenche des chargements lazy.
Explique la cause, propose au moins 3 solutions (JOIN FETCH, @EntityGraph, batch fetching, projection DTO...)
avec leurs compromis (pagination, produit cartésien, lisibilité). N'implémente rien.
```

4. Choisissez une solution, faites-la implémenter, et **mesurez à nouveau**.
5. Demandez un test qui protège contre une régression, par exemple en comptant les requêtes via les statistiques Hibernate (`hibernate.generate_statistics`).

## 3. Revue croisée (15 min)

Échangez votre diff avec votre voisin. Lancez **votre** subagent `spring-reviewer` (lab 06) sur **son** code, puis comparez avec votre propre revue humaine.

## ✅ Critères de réussite

- [ ] Rapport d'audit avec au moins 3 problèmes confirmés.
- [ ] Injection SQL et fuite de données corrigées, avec tests.
- [ ] Nombre de requêtes de `GET /api/orders` mesuré avant / après.

## 💬 À débriefer

- Combien de faux positifs ? De faux négatifs (problèmes que vous avez vus et pas l'agent) ?
- L'agent peut-il remplacer un outil SAST ou un pentest ? Le compléter ?
- Que faut-il ajouter au `CLAUDE.md` ou au subagent pour éviter que ces erreurs reviennent ?

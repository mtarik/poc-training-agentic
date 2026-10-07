# SHOP-105 — [AUDIT] Revue sécurité et performance avant mise en production

**Type** : Audit · **Priorité** : Haute · **Composant** : tous

## Contexte

Un audit est demandé avant l'ouverture de l'API à des partenaires. Deux signaux remontés :

- le RSSI s'inquiète des endpoints `/api/customers` (et de leur adapter de recherche JDBC) ;
- l'équipe Ops constate que `GET /api/orders` devient lent quand le nombre de commandes augmente.

## Attendu

- [ ] Produire un rapport listant chaque problème : localisation (fichier:ligne), gravité, scénario d'exploitation ou impact, correction proposée.
- [ ] Corriger les problèmes de gravité haute, avec un test qui prouve la correction.
- [ ] Pour la performance : mesurer le nombre de requêtes SQL émises par `GET /api/orders` **avant / après** correction.

Astuce : `logging.level.org.hibernate.SQL=DEBUG` dans `application.yml` affiche les requêtes.

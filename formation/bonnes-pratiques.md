# Bonnes pratiques et points de vigilance

## Les 10 règles d'or

1. **Explorer → Planifier → Implémenter → Vérifier.** Ne sautez pas le plan sur une tâche de plus de quelques lignes.
2. **Donnez un critère de fin vérifiable** : un test, une commande, un comportement observable.
3. **Une tâche = une session.** `/clear` entre deux sujets.
4. **Committez souvent** (avant chaque tâche) : c'est votre filet de sécurité, avec `/rewind`.
5. **Relisez chaque diff** comme celui d'un collègue junior très rapide et très confiant.
6. **Méfiez-vous des tests modifiés** : un test dont l'assertion a changé doit être justifié.
7. **Capitalisez** : une remarque répétée deux fois → `CLAUDE.md`. Une procédure répétée → skill. Une règle absolue → hook ou permission.
8. **Petits pas** : 5 petites tâches valent mieux qu'une grosse.
9. **Interrompez tôt** (`Esc`) quand l'agent part dans une mauvaise direction : recadrer coûte moins que laisser finir.
10. **Restez l'auteur** : si vous ne pouvez pas expliquer le code en revue, il n'est pas prêt.

## Sécurité

- L'agent exécute des commandes **avec vos droits**. Gardez les confirmations activées pour les commandes non listées dans `allow`.
- Interdisez explicitement les actions irréversibles : `git push`, `rm -rf`, déploiements, accès aux environnements de production.
- **Contenu externe = non fiable.** Une page web, un ticket, un fichier téléchargé ou une réponse d'un serveur MCP peut contenir des instructions malveillantes (*prompt injection*). Ne laissez pas l'agent suivre aveuglément des instructions trouvées dans des données.
- Ne connectez que des serveurs MCP dont vous connaissez la provenance.
- Le code généré doit passer les mêmes contrôles que le reste : revue, SAST, tests, dépendances.

## Confidentialité

- **Jamais de secrets** dans le prompt, dans `CLAUDE.md` ou dans des fichiers lisibles par l'agent (`.env` en `deny`).
- **Jamais de données personnelles réelles** (clients, salariés) : utilisez des jeux de données fictifs.
- Respectez la politique de votre client : certains contrats interdisent l'envoi de code à un service tiers. Vérifiez **avant** d'utiliser l'agent sur un projet client.
- Préférez les offres entreprise (Team / Enterprise / Console API) dont les conditions d'utilisation des données sont adaptées.

## Quand ne pas (ou peu) utiliser l'agent

- Vous ne comprenez pas encore le problème vous-même : explorez d'abord, avec l'agent en mode lecture si besoin.
- Code critique sans tests (sécurité, paiement) : écrivez ou faites écrire **d'abord** les tests, relisez-les vous-même.
- Décisions d'architecture structurantes : l'agent peut proposer des options, la décision reste humaine.
- Apprentissage d'une techno : l'agent peut vous priver de l'apprentissage. Demandez-lui d'expliquer plutôt que de faire.

## Signaux d'alerte en revue

- Annotation ou classe que vous ne connaissez pas → vérifiez qu'elle existe dans **votre** version (Spring Boot 4 ≠ 3).
- `@Disabled`, `catch (Exception e) {}`, assertion supprimée ou affaiblie.
- Fichiers modifiés hors du périmètre demandé.
- Nouvelle dépendance Maven non demandée.
- « Tous les tests passent » sans trace d'exécution de `./mvnw test`.

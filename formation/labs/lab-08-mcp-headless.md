# Lab 08 : MCP, mode headless et intégration CI

⏱ 1 h · 🎯 Connecter l'agent à des outils externes et l'utiliser hors de l'interface interactive.

## Partie 1 : MCP (Model Context Protocol) (25 min)

MCP est un protocole standard qui permet de brancher des outils externes sur l'agent : documentation, tickets (Jira, GitHub), bases de données, navigateur, etc.

### 1.a Une documentation à jour

Les modèles ont une date de coupure : ils connaissent mal les versions récentes (Spring Boot 4, Jackson 3…). Un serveur MCP de documentation comble ce manque. Exemple avec Context7 :

```bash
claude mcp add --transport http context7 https://mcp.context7.com/mcp
```

Dans Claude Code : `/mcp` pour vérifier la connexion, puis :

```text
En utilisant la documentation à jour (context7), explique ce qui a changé pour les tests
@WebMvcTest entre Spring Boot 3 et Spring Boot 4, et vérifie que nos tests suivent la bonne approche.
```

### 1.b Partager la configuration avec l'équipe

```bash
claude mcp add --scope project --transport http context7 https://mcp.context7.com/mcp
```

→ un fichier `.mcp.json` est créé à la racine. Ouvrez-le : il peut être versionné. Les secrets ne doivent **pas** y figurer en clair (utilisez des variables d'environnement `${VAR}`).

### 1.c Pour aller plus loin (selon l'environnement)

- **Gestionnaire de tickets** (Jira, GitHub Issues, Linear) : « Lis le ticket PROJ-123 et implémente-le ».
- **Base de données** : interroger le schéma ou les données de dev (jamais la prod).
- **Navigateur** (Playwright) : tester une interface.

> ⚠️ Un serveur MCP est du code tiers qui s'exécute avec vos droits, et ses réponses entrent dans le contexte de l'agent (risque d'injection de prompt). N'installez que des serveurs de confiance.

## Partie 2 : mode headless (20 min)

`claude -p` exécute une tâche et rend la main : idéal pour les scripts.

```bash
# Question ponctuelle
claude -p "Liste les endpoints REST du projet sous forme de tableau markdown"

# Sortie JSON exploitable par un script
claude -p "Combien de classes de test contient le projet ?" --output-format json

# Restreindre les outils (aucune modification possible)
claude -p "Relis OrderService et liste les problèmes potentiels" --allowedTools "Read,Grep,Glob"

# Chaîner avec d'autres commandes
git diff main --name-only | claude -p "Pour chacun de ces fichiers, résume en une ligne ce qui a changé"
```

Exercice : écrivez un script `scripts/release-notes.sh` qui génère des notes de version à partir de `git log` des 10 derniers commits.

## Partie 3 : intégration CI (15 min)

Ouvrez `formation/solutions/ci/claude-pr-review.yml` : un workflow GitHub Actions qui fait relire chaque Pull Request par Claude avec les conventions du projet.

Discutez en groupe :

- Quels outils autoriser en CI ? Lesquels interdire ?
- Qui est responsable d'une PR fusionnée après une revue « approuvée » par l'agent ?
- Quel coût par PR ? Comment le maîtriser ?
- Sur GitLab CI ou Jenkins : `claude -p` dans un job, avec la clé API dans les variables protégées.

## ✅ Critères de réussite

- [ ] Un serveur MCP connecté et utilisé dans un prompt.
- [ ] Au moins 2 commandes headless exécutées, dont une avec `--allowedTools`.

## 💬 À débriefer

- Quels outils internes de vos projets mériteraient un serveur MCP ?
- Quelles tâches répétitives de votre équipe pourraient passer en headless ?

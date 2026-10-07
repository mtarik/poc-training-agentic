# Aide-mémoire Claude Code

> Les commandes évoluent vite : `/help` fait foi pour votre version.

## Lancer

| Commande | Effet |
|---|---|
| `claude` | session interactive dans le dossier courant |
| `claude "explique ce projet"` | session avec un premier prompt |
| `claude -c` / `claude --continue` | reprendre la dernière conversation |
| `claude -r` / `claude --resume` | choisir une conversation à reprendre |
| `claude -p "..."` | mode headless : répond et s'arrête (scripts, CI) |
| `claude -p "..." --output-format json` | sortie structurée |

## Dans la session

| Saisie | Effet |
|---|---|
| `@src/main/.../OrderService.java` | ajouter un fichier au contexte |
| `!./mvnw test` | exécuter une commande shell directement |
| `Shift+Tab` | changer de mode : normal → acceptation auto des modifications → **Plan mode** |
| `Esc` | interrompre l'agent |
| `Esc` `Esc` | revenir à un message précédent (rewind) |
| `/init` | générer un `CLAUDE.md` en analysant le projet |
| `/memory` | éditer les fichiers de mémoire |
| `/clear` | repartir d'un contexte vide (**entre deux tâches**) |
| `/compact [consigne]` | résumer la conversation pour libérer du contexte |
| `/context` | voir l'occupation du contexte |
| `/model` | changer de modèle |
| `/permissions` | voir et modifier les permissions |
| « crée un subagent qui… » | créer un subagent (on le demande à Claude, ou on écrit `.claude/agents/<nom>.md`) |
| « utilise l'agent reviewer pour… » | déléguer une tâche à un subagent |
| `/equipe SHOP-301` | faire traiter un ticket par l'équipe architecte → dev → QA + reviewer |
| `/hooks` | voir les hooks configurés |
| `/mcp` | gérer les serveurs MCP |
| `/rewind` | restaurer code et/ou conversation à un point antérieur |
| `/security-review` | revue de sécurité des changements en cours |
| `/help` | toutes les commandes, dont vos skills |

## Fichiers de configuration

```
CLAUDE.md                        mémoire projet (versionnée, partagée)
CLAUDE.local.md                  mémoire perso pour ce projet (non versionnée)
~/.claude/CLAUDE.md              mémoire perso globale
.claude/settings.json            permissions + hooks du projet (versionné)
.claude/settings.local.json      surcharges perso (non versionné)
.claude/skills/<nom>/SKILL.md    skills (invocables par /<nom>)
.claude/agents/<nom>.md          subagents
.mcp.json                        serveurs MCP du projet
```

## Permissions (extrait)

```json
{
  "permissions": {
    "allow": ["Bash(./mvnw test:*)", "Bash(git diff:*)"],
    "deny":  ["Read(./.env)", "Bash(git push:*)"]
  }
}
```

## Hooks (extrait)

```json
{
  "hooks": {
    "PostToolUse": [{
      "matcher": "Edit|Write",
      "hooks": [{ "type": "command", "command": "bash .claude/hooks/compile-check.sh" }]
    }]
  }
}
```

Code de sortie `0` = OK, `2` = bloquer / renvoyer stderr à l'agent.

## Prompts types

```text
Explore : lis le package order et explique le calcul d'une commande. Ne modifie rien.
Plan    : (Plan mode) propose un plan pour SHOP-102. Liste les fichiers impactés et les tests.
TDD     : écris d'abord un test qui échoue pour <comportement>, montre-le rouge, puis corrige.
Revue   : relis le diff courant comme un senior exigeant, classe les problèmes par gravité.
Limite  : ne modifie pas <fichier>. N'ajoute pas de dépendance.
Fin     : termine quand ./mvnw test est vert et résume ce que tu as changé.
```

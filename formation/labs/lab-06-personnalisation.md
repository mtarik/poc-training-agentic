# Lab 06 : Personnaliser l'agent (skills, subagents, hooks)

⏱ 1 h 30 · 🎯 Transformer les pratiques de l'équipe en outillage partagé et versionné.

> Règle de choix :
> - **`CLAUDE.md`** : ce que l'agent doit *savoir* à chaque session.
> - **Skill** : une *procédure* que l'on déclenche (ou que l'agent déclenche) au besoin.
> - **Subagent** : une *spécialité* qui travaille dans son propre contexte, avec ses propres outils.
> - **Hook** : une règle qui doit s'appliquer *à chaque fois*, de façon **déterministe**.
> - **Permission** : ce que l'agent peut faire sans demander, ou jamais.

## Partie 1 : un skill `/fix-ticket` (25 min)

Créez `.claude/skills/fix-ticket/SKILL.md` qui encode la boucle TDD du lab 02 :

```markdown
---
name: fix-ticket
description: <quand l'agent doit-il utiliser ce skill ? soyez précis>
argument-hint: "[SHOP-XXX]"
---

# Corriger le ticket $ARGUMENTS

1. ...
```

Contenu attendu : lire le ticket et la spec, localiser, test rouge, correction minimale, suite verte, résumé normalisé, pas de commit.

Testez-le : créez vous-même un petit ticket `docs/tickets/SHOP-900.md` (par exemple : « le message d'erreur 404 d'un produit doit contenir le mot "Produit" en majuscule »), puis :

```text
/fix-ticket SHOP-900
```

**Bonus** : un skill `/new-use-case` qui impose le workflow hexagonal de l'équipe : plan par couche, puis implémentation de l'intérieur vers l'extérieur (domaine → application → adapters), avec des tests verts à chaque couche.

## Partie 2 : un subagent `spring-reviewer` (25 min)

Demandez à Claude de le créer (« crée un subagent de projet spring-reviewer qui… »), ou écrivez vous-même `.claude/agents/spring-reviewer.md`. Inspirez-vous des 4 agents de l'équipe déjà présents dans `.claude/agents/` (`architecte`, `developpeur`, `qa`, `reviewer`).

```markdown
---
name: spring-reviewer
description: Relecteur Spring Boot / JPA. À utiliser après toute modification de code Java.
tools: Read, Grep, Glob, Bash
---

<prompt système : rôle, grille de revue, format de sortie>
```

Points clés :

- **outils restreints** : un relecteur n'a pas besoin de `Edit` ni de `Write` ;
- une **grille de revue** explicite : architecture (bonne couche ? règle métier hors du domaine ? port qui fuit des détails techniques ?), transactions, N+1, DTO, validation, BigDecimal, SQL… ;
- un **format de sortie** fixe (tableau par gravité + verdict).

Testez sur le code du lab 03 :

```text
Utilise le subagent spring-reviewer pour relire les changements de SHOP-102.
```

Observez : le subagent a son propre contexte, et seul son rapport revient dans la conversation principale.

## Partie 3 : des hooks (30 min)

### Hook 1 : protéger la configuration de production (PreToolUse)

Créez `.claude/hooks/protect-prod-config.sh` : il lit le JSON de l'appel d'outil sur stdin, extrait `file_path`, et sort en **code 2** (avec un message sur stderr) si le fichier est `application-prod.yml`.

### Hook 2 : compiler après chaque modification Java (PostToolUse)

Créez `.claude/hooks/compile-check.sh` : si le fichier modifié est un `.java`, lance `./mvnw -q -o test-compile` ; en cas d'échec, renvoie les erreurs sur stderr avec le code 2. L'agent voit alors l'erreur et se corrige tout de suite.

### Hook 3 : garde-fou d'architecture (Stop)

Créez `.claude/hooks/architecture-check.sh`, déclenché sur l'événement `Stop` (quand l'agent s'apprête à rendre la main). Il lance `./mvnw -q -o test -Dtest=HexagonalArchitectureTest` ; en cas d'échec, il renvoie les violations sur stderr avec le code 2. **L'agent ne peut alors pas conclure** et doit corriger son code.

Attention à la boucle infinie : le JSON reçu contient `"stop_hook_active": true` quand l'agent a déjà été relancé par un hook Stop. Dans ce cas, sortez en code 0.

### Déclaration

Dans `.claude/settings.json` :

```json
"hooks": {
  "PreToolUse": [
    { "matcher": "Edit|Write",
      "hooks": [{ "type": "command", "command": "bash \"$CLAUDE_PROJECT_DIR/.claude/hooks/protect-prod-config.sh\"" }] }
  ],
  "PostToolUse": [
    { "matcher": "Edit|Write",
      "hooks": [{ "type": "command", "command": "bash \"$CLAUDE_PROJECT_DIR/.claude/hooks/compile-check.sh\"", "timeout": 180 }] }
  ],
  "Stop": [
    { "hooks": [{ "type": "command", "command": "bash \"$CLAUDE_PROJECT_DIR/.claude/hooks/architecture-check.sh\"", "timeout": 300 }] }
  ]
}
```

Redémarrez la session (ou vérifiez avec `/hooks`), puis testez :

```text
Passe le niveau de log root à DEBUG dans application-prod.yml.
```

→ l'action doit être bloquée et l'agent doit vous expliquer pourquoi.

```text
Ajoute une méthode dans ProductService qui utilise une classe qui n'existe pas.
```

→ le hook de compilation doit renvoyer l'erreur à l'agent.

```text
Dans ProductService, injecte directement ProductJpaRepository à la place du port ProductRepository.
```

→ à la fin du tour, le hook Stop doit refuser l'arrêt et l'agent doit annuler la modification.

## ✅ Critères de réussite

- [ ] Un skill invocable par `/fix-ticket`, testé sur un vrai ticket.
- [ ] Un subagent en lecture seule qui produit un rapport au format attendu.
- [ ] Trois hooks fonctionnels, démontrés (prod protégée, compilation, architecture).
- [ ] Le tout commité dans `.claude/` : c'est maintenant l'outillage **de l'équipe**.

## 💬 À débriefer

- Interdire `application-prod.yml` par une **permission** `deny` ou par un **hook** : avantages de chacun ?
- Quel coût a un hook PostToolUse lent ? Comment le limiter ?
- Quels skills seraient utiles sur vos projets réels ?

Solutions de référence : `formation/solutions/.claude/` (à consulter **après**).

# Lab 00 : Installation et premier contact

⏱ 45 min · 🎯 Installer Claude Code, découvrir l'interface, explorer un code inconnu sans rien modifier.

## 1. Vérifier l'environnement (5 min)

```bash
./scripts/check-setup.sh
# Windows : powershell -ExecutionPolicy Bypass -File scripts\check-setup.ps1
```

Tout doit être vert sauf éventuellement `claude` (installé à l'étape suivante).

## 2. Installer Claude Code (10 min)

**Windows (PowerShell)** : Git for Windows doit être installé.

```powershell
irm https://claude.ai/install.ps1 | iex
```

**macOS / Linux / WSL**

```bash
curl -fsSL https://claude.ai/install.sh | bash
```

Alternative si Node.js 18+ est disponible : `npm install -g @anthropic-ai/claude-code`.

Puis, dans le dossier du projet :

```bash
claude
```

Suivez l'authentification (compte Claude ou clé Console API, selon ce que fournit votre organisation). Vérifiez avec `/status`.

> 💡 Une extension **Claude Code** existe pour VS Code et pour les IDE JetBrains : elle affiche les diffs directement dans l'éditeur. Vous pouvez l'installer, mais faites ce lab dans le terminal pour en voir les mécanismes.

## 3. Mettre le projet sous Git (5 min)

L'agent modifie des fichiers : il faut pouvoir revenir en arrière. Si le projet n'est pas déjà un dépôt Git :

```bash
git init && git add -A && git commit -m "Etat initial de la formation"
```

## 4. Explorer sans modifier (20 min)

Posez les questions suivantes **une par une** et observez *quels outils* l'agent utilise (Read, Grep, Glob, Bash…).

1. `Quelle est la stack technique de ce projet et comment le lance-t-on ?`
2. `Liste tous les endpoints REST exposés avec leur méthode HTTP.`
3. `Explique pas à pas comment est calculé le montant total d'une commande. Cite les fichiers et les lignes.`
4. `Ce projet suit une architecture hexagonale. Dessine en ASCII le chemin d'une requête POST /api/orders, du controller jusqu'à la base, en nommant chaque port et adapter traversé.`
5. `Quelles différences de qualité vois-tu entre le contexte catalog et le contexte customer, couche par couche ?`
6. `Lance les tests et résume le résultat. À quoi sert HexagonalArchitectureTest ?` → l'agent vous demande la permission ? Pourquoi ? (regardez `.claude/settings.json`)

Puis lancez l'application et demandez :

7. `Lance l'application en arrière-plan puis appelle GET /api/orders/1 et commente la réponse.`

## 5. Observer (5 min)

- Tapez `/context` : combien de contexte est déjà consommé ? Par quoi ?
- Tapez `/cost` (ou `/status` selon votre offre).
- Tapez `/clear`, puis reposez la question 3. La réponse est-elle identique ?

## ✅ Critères de réussite

- [ ] Claude Code est installé et authentifié.
- [ ] Le projet est sous Git avec un commit initial.
- [ ] Vous savez expliquer les étapes de la boucle agentique observées (lecture → raisonnement → outil → observation).

## 💬 À débriefer

- L'agent a-t-il répondu juste à la question 3 ? A-t-il **vérifié** dans le code ou supposé ?
- Une question d'exploration est-elle « gratuite » ? (contexte, temps, coût)

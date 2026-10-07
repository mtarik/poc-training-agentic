#!/usr/bin/env bash
# Prépare le dépôt pour la démo : installe le CLAUDE.md et l'outillage .claude/ de référence.
set -euo pipefail
cd "$(dirname "$0")/../.."

SOL=formation/solutions

if [ -d src/main/java/fr/formation/shop/domain/review ]; then
  echo "ATTENTION : le contexte review existe déjà (démo déjà jouée ?). Remettez le dépôt à zéro avec git avant la démo." >&2
  exit 1
fi

echo "Copie de CLAUDE.md et de .claude/ (skills, agents, hooks, settings)..."
cp "$SOL/CLAUDE.md" CLAUDE.md
mkdir -p .claude
cp -r "$SOL/.claude/." .claude/

echo "Préchauffage du cache Maven et vérification du build..."
./mvnw -q -B verify

echo
echo "Prêt. Conseillé : git add -A && git commit -m \"Avant démo SHOP-201\""
echo "Puis : claude   (et suivez formation/demo/README.md)"

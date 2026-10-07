#!/usr/bin/env bash
# Hook Stop : avant que l'agent ne rende la main, vérifie les règles d'architecture hexagonale.
# En cas de violation, le code de sortie 2 empêche l'arrêt et renvoie le détail à l'agent,
# qui doit corriger son code avant de pouvoir terminer.

input=$(cat)

# Evite une boucle infinie : si l'agent a déjà été relancé par ce hook, on le laisse s'arrêter.
if printf '%s' "$input" | grep -q '"stop_hook_active"[[:space:]]*:[[:space:]]*true'; then
  exit 0
fi

cd "${CLAUDE_PROJECT_DIR:-.}" || exit 0

if ! output=$(./mvnw -q -o -B test -Dtest=HexagonalArchitectureTest -Dsurefire.failIfNoSpecifiedTests=false 2>&1); then
  echo "Les règles d'architecture hexagonale ne sont pas respectées. Corrige le code (jamais la règle) :" >&2
  printf '%s\n' "$output" | grep -E "was violated|^(Constructor|Field|Method|Class) <" | awk '!seen[$0]++' | head -15 >&2
  exit 2
fi

exit 0

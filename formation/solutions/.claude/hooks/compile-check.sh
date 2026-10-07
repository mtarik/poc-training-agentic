#!/usr/bin/env bash
# Hook PostToolUse (Edit|Write) : recompile le projet après chaque modification d'un fichier .java.
# En cas d'échec, le code de sortie 2 renvoie les erreurs à l'agent, qui peut se corriger immédiatement.

input=$(cat)
file_path=$(printf '%s' "$input" | grep -o '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"' | head -1 | sed 's/.*:[[:space:]]*"\(.*\)"$/\1/')

case "$file_path" in
  *.java) ;;
  *) exit 0 ;;
esac

cd "${CLAUDE_PROJECT_DIR:-.}" || exit 0

if ! output=$(./mvnw -q -o -B test-compile 2>&1); then
  echo "La compilation échoue après la modification de $file_path :" >&2
  printf '%s\n' "$output" | grep -E '\[ERROR\].*\.java' | awk '!seen[$0]++' | head -20 >&2
  exit 2
fi

exit 0

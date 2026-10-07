#!/usr/bin/env bash
# Hook PreToolUse (Edit|Write) : interdit toute modification de la configuration de production.
# Claude Code transmet l'appel d'outil en JSON sur stdin ; un code de sortie 2 bloque l'action
# et renvoie le message d'erreur (stderr) à l'agent.

input=$(cat)
file_path=$(printf '%s' "$input" | grep -o '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"' | head -1 | sed 's/.*:[[:space:]]*"\(.*\)"$/\1/')

case "$file_path" in
  *application-prod.yml)
    echo "BLOQUÉ : application-prod.yml est géré par l'équipe Ops. Propose la modification dans ta réponse au lieu de l'appliquer." >&2
    exit 2
    ;;
esac

exit 0

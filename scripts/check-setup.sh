#!/usr/bin/env bash
# Vérifie que le poste est prêt pour la formation.

ok=0
check() {
  local label="$1"; shift
  if "$@" >/dev/null 2>&1; then
    printf '  \033[32m[OK]\033[0m %s\n' "$label"
  else
    printf '  \033[31m[KO]\033[0m %s\n' "$label"
    ok=1
  fi
}

java_major() {
  java -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+).*/\1/'
}

echo "Vérification de l'environnement de formation"
check "Java installé" java -version
if command -v java >/dev/null 2>&1; then
  check "Java >= 21 (trouvé : $(java_major))" test "$(java_major)" -ge 21
fi
check "Git installé" git --version
check "Accès à Maven Central" curl -fsS -o /dev/null https://repo.maven.apache.org/maven2/
check "Accès à l'API Anthropic" curl -sS -o /dev/null https://api.anthropic.com
check "Claude Code installé" claude --version
check "Build et tests du projet (./mvnw -q verify)" ./mvnw -q -B verify

if [ $ok -eq 0 ]; then
  echo "Tout est prêt."
else
  echo "Certains prérequis manquent : voir formation/labs/lab-00-installation.md"
fi
exit $ok

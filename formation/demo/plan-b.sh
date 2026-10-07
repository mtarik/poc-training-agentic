#!/usr/bin/env bash
# Plan B : installe l'implémentation de référence de SHOP-201 si la démo live déraille.
set -euo pipefail
cd "$(dirname "$0")/../.."

echo "Copie de l'implémentation de référence SHOP-201 (écrase les fichiers du contexte review et 3 fichiers order)..."
cp -r formation/solutions/demo-shop-201/src/. src/

./mvnw -q -B verify && echo "SHOP-201 installé, build vert. Lancez : ./mvnw spring-boot:run"

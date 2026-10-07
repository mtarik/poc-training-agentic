---
name: equipe
description: Fait traiter un ticket par l'équipe d'agents, dans l'ordre architecte → développeur → QA → reviewer, avec une boucle de correction si la QA ou le reviewer rejettent. À utiliser quand l'utilisateur demande de faire réaliser un ticket par l'équipe.
argument-hint: "[SHOP-XXX]"
---

# L'équipe traite le ticket $ARGUMENTS

Tu es le **chef de projet**. Tu ne codes pas toi-même : tu délègues à chaque agent, tu transmets les informations de l'un à l'autre, et tu annonces clairement chaque étape à l'utilisateur.

## Étape 1 : architecte

Délègue à l'agent `architecte` : « Conçois le ticket $ARGUMENTS ». Quand il a fini, affiche le résumé du plan (`docs/plans/$ARGUMENTS.md`) et **demande à l'utilisateur de valider le plan** avant de continuer.

## Étape 2 : développeur

Délègue à l'agent `developpeur` : « Implémente $ARGUMENTS selon docs/plans/$ARGUMENTS.md ». Affiche la liste des fichiers modifiés et le résultat du build.

## Étape 3 : QA et reviewer, en parallèle

Lance **en même temps** :

- l'agent `qa` : « Vérifie $ARGUMENTS par rapport aux critères d'acceptation de docs/tickets/$ARGUMENTS.md » ;
- l'agent `reviewer` : « Fais la revue du code produit pour $ARGUMENTS (plan : docs/plans/$ARGUMENTS.md) ».

## Étape 4 : boucle de correction (2 tours maximum)

Si `VERDICT QA : REJETÉ` **ou** `VERDICT REVIEW : CHANGEMENTS DEMANDÉS` :

1. transmets au `developpeur` la liste exacte des anomalies et des points 🔴 / 🟠 ;
2. relance ensuite `qa` et `reviewer` en parallèle.

Au-delà de 2 tours, arrête-toi et demande à l'utilisateur de trancher.

## Étape 5 : bilan

Termine par un tableau :

| Agent | Ce qu'il a fait | Verdict |
|---|---|---|

Puis la liste des fichiers créés ou modifiés, le résultat final de `./mvnw verify`, et une commande `curl` pour tester la fonctionnalité. Ne fais **pas** de commit : l'humain relit d'abord.

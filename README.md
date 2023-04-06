# Projet Applicatif

## Table des matières

- [Projet Applicatif](#projet-applicatif)
  - [Table des matières](#table-des-matières)
  - [Introduction](#introduction)
    - [Résumé](#résumé)
    - [2 Parties](#2-parties)
    - [Structure](#structure)
  - [Fonctionnalités](#fonctionnalités)
  - [Prérequis](#prérequis)
  - [Installation et configuration](#installation-et-configuration)
  - [Structure du projet](#structure-du-projet)
  - [Contribution](#contribution)

## Introduction

### Résumé

Ce projet a pour objectif de créer un interpréteur pour un langage de programmation spécifique, destiné à la création, modification et manipulation d'objets graphiques tels que des images et des textes. Ce langage, nommé ROBI, permet aux utilisateurs de concevoir et de contrôler des éléments graphiques de manière intuitive et efficace. Une IHM est présente permettant au client d'envoyer des instructions au serveur, et d'afficher les résultats graphiques.

### 2 Parties

Dans la première partie du projet, nous nous sommes concentrés sur le développement de l'interpréteur ROBI, capable de comprendre et d'exécuter les instructions du langage.

La seconde partie du projet consiste à créer une interface homme-machine (IHM) distante permettant la communication entre un client et un serveur. Cette interface permet aux utilisateurs de saisir et d'exécuter du code ROBI sur une machine distante, en bénéficiant d'une interface utilisateur conviviale pour afficher les résultats.

### Structure

Le projet est structuré en deux parties principales : le serveur ROBI, qui se charge de l'exécution du code ROBI, et le client ROBI, qui permet la saisie du code, l'envoi des instructions au serveur via des sockets, et l'affichage des résultats graphiques. Les deux parties communiquent entre elles pour permettre une exécution fluide et efficace du langage ROBI sur différentes machines.

## Fonctionnalités

Listez les principales fonctionnalités de votre projet, en décrivant brièvement leur fonction et leur utilité pour les utilisateurs.

- Interpreteur : Un interpreteur pour le langage ROBI, capable de comprendre et d'exécuter les instructions du langage. L'interpreteur est capable de créer et de manipuler des objets graphiques tels que des images et des textes. Il peut également enregistrer des scripts robi manuellement.
- Communication serveur / client : Des sockets permettent de à un client d'exécuter à distance du code ROBI. Le client peut envoyer des instructions au serveur, et le serveur peut envoyer des informations au client. Les deux parties communiquent entre elles pour permettre une exécution fluide et efficace du langage ROBI sur différentes machines.
- IHM Client : Une IHM permettant à l'utilisateur de saisir et d'exécuter du code ROBI sur une machine distante, en bénéficiant d'une interface utilisateur conviviale pour afficher les résultats.
- Modes d'exécutions : Le serveur peut exécuter le code ROBI en mode bloc ou en mode pas à pas. Le mode bloc permet d'exécuter le code ROBI en une seule fois, tandis que le mode pas à pas permet d'exécuter le code ROBI étape par étape, en affichant les résultats à chaque étape.
- Modes d'envois de données : Le serveur peut envoyer les données au client en mode capture d'écran, ou en mode commandes graphiques. Dans le cas où le serveur n'a pas de capacités graphiques, il peut simplement compiler le code et envoyer des commandes. Le client peut alors les exécuter et afficher les résultats.

## Prérequis

- Java 11
- Junit 4 et 5.8.1
- Jackson core et jackson databind

## Installation et configuration

Pour installer le projet, suivez les étapes suivantes :

- Cloner le projet (git clone)
- Lancer votre IDE
- Ayez les importations de Junit 4 et 5.8.1, ainsi que de jackson core et jackson databind
- Faites en sorte d'exécuter le client à partir de "Client.java" avec comme package exercice4.Client.Client
- Faites en sorte d'exécuter le serveur à partir de "Server.java" avec comme package exercice4.Server.Server
- Mettez comme dossier d'exécution pour le serveur "exercice4" dans Robi
- Lancez le serveur
- Lancez le client

Pour configurer le projet, suivez les étapes suivantes :

- Changez le port et le host du serveur dans le fichier "Server.java" si besoin
- Changez le port et le host du serveur dans le ficher "ClientSocketsOperations.java" si besoin

## Structure du projet



## Contribution

Expliquez comment les autres développeurs peuvent contribuer à votre projet. Décrivez les directives pour la soumission de problèmes, les demandes de fonctionnalités et les pull requests.

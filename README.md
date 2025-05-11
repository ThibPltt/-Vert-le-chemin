# 🌿 Vert le Chemin

**Vert le Chemin** est une application mobile conçue pour sensibiliser les utilisateurs à l’impact environnemental de leurs déplacements quotidiens. 
Elle estime les émissions de CO2 en fonction du mode de transport choisi et propose des alternatives plus vertueuses pour réduire l’empreinte carbone.

## 📱 Objectif

Offrir une solution interactive et sociale pour :
- Estimer les émissions de CO2 d’un trajet.
- Comparer ces émissions à d'autres activités du quotidien.
- Proposer des trajets optimisés selon le mode de transport (vélo, voiture, marche…).
- Sauvegarder et partager ses trajets pour encourager un changement collectif.

## 🎯 Public visé

Utilisateurs se rendant chaque jour à leur lieu de travail ou d’étude (ex: Le Mans Université), souhaitant adopter un mode de transport plus respectueux de l’environnement.

---

## 🧠 Fonctionnalités clés

- 🌍 Navigation interactive basée sur la localisation GPS.
- 🚲 Choix de différents modes de transport.
- 📊 Estimations de CO2 basées sur les données officielles de l’ADEME.
- ⭐ Sauvegarde des trajets et ajout aux favoris.
- 🔄 Partage de trajets entre utilisateurs.
- 🔐 Respect de la confidentialité et conformité RGPD.

---

## 🛠️ Stack technique

| Domaine        | Technologie            |
|----------------|------------------------|
| Langage        | Kotlin (Android)       |
| UI/UX          | Figma (Low-fi & High-fi) |
| Données        | Base de données sécurisée |
| Navigation     | API GPS Android        |

---

## 🚧 État d’avancement (au 16/04/2025)

- ✅ Cahier des charges rédigé
- ✅ Maquette Low-fi
- ✅ Arbre des tâches
- 🔜 Maquette High-fi
- 🔜 MVP en cours de développement

---

## 🔬 Protocole d’expérimentation

### Hypothèses
- L'utilisateur choisit son moyen de transport selon l’impact environnemental.
- Il est influencé par la comparaison d’émissions.
- Il utilise les fonctionnalités de sauvegarde et de partage.
- L'application est perçue comme ergonomique (SUS > 72).

### Fonctionnalités testées
- Sélection de transport.
- Navigation.
- Affichage d’estimations de CO2.
- Gestion des favoris.
- Partage social.

---

## 📈 Business Model envisagé (futur)

- Monétisation par publicité légère : ~0,36€/1000 affichages.
- Exemple : 1000 utilisateurs actifs/jour = ~648€/an.

---

## 🧩 Arborescence fonctionnelle (extrait)

- Authentification (login, création, mot de passe oublié)
- Choix du mode de transport
- Calcul du trajet
- Estimation des émissions
- Affichage de la carte
- Sauvegarde / favoris / partage

---

## 🔐 Confidentialité

Les données personnelles de l'utilisateur (âge, nom, géolocalisation) sont stockées de manière sécurisée et cryptée. L'utilisateur peut à tout moment supprimer son compte selon le RGPD.

---

## 📚 Glossaire

- **BDD** : Base de Données  
- **GPS** : Global Positioning System  
- **MVP** : Minimum Viable Product  
- **RGPD** : Règlement Général sur la Protection des Données  

---

## 🤝 Contributeurs

- **Thibaut PLATET** – Étudiant 4A IPS – ENSIM

---

## 📦 Installation

Pour cloner ce projet en local :

```bash
git clone https://github.com/ThibPltt/-Vert-le-chemin.git
cd -Vert-le-chemin


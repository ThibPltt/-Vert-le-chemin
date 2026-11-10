<picture><source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/ThibPltt/ThibPltt/main/assets/repos/Vert-le-chemin-dark.svg"><source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/ThibPltt/ThibPltt/main/assets/repos/Vert-le-chemin-light.svg"><img src="https://raw.githubusercontent.com/ThibPltt/ThibPltt/main/assets/repos/Vert-le-chemin-light.svg" width="100%" alt="Vert le Chemin, 2024 to 2025: Android app that estimates the CO2 of daily trips. Kotlin, Jetpack Compose, ENSIM."></picture>

# Vert le Chemin

Android app that estimates the CO₂ of daily trips and suggests greener means of transport.

| At a glance | |
|---|---|
| **Dates** | 2024 – 2025 |
| **Context** | mobile app · course project · ENSIM |
| **Technologies** | Android · Kotlin · Jetpack Compose · Firebase |

## Problem

Make people who commute every day to work or study (e.g. Le Mans Université) aware of the environmental impact of their trips, and help them switch to a more environmentally friendly way of travelling.

## Role

Course project, ENSIM 4th year (Interaction Personnes-Systèmes): specification, mock-ups and the Kotlin prototype.

## Decisions

- Estimate the CO₂ emissions of a trip from official ADEME data, for the chosen means of transport.
- Compare these emissions with everyday activities, so that the figure speaks to the user.
- Suggest routes optimised for each means of transport (bike, car, walking…), on OpenStreetMap.
- Save and share trips to encourage collective change, with privacy by design (GDPR).

## Result

Specification, functional tree and low-fi mock-ups done; a Kotlin prototype with sign-in, registration and password reset, language and settings, destination choice, route display on the map, navigation and favourites screens.

## Features

- 🌍 Interactive navigation based on GPS location
- 🚲 Choice between several means of transport
- 📊 CO₂ estimates based on ADEME data
- ⭐ Saved trips and favourites
- 🔄 Trips shared between users
- 🔐 Privacy by design and GDPR compliance

## Tech

| Area | Technology |
|---|---|
| Language | Kotlin (Android, min SDK 28, target SDK 35) |
| UI | Jetpack Compose, Material 3, Navigation Compose; mock-ups in Figma (low-fi and high-fi) |
| Architecture | MVVM (ViewModels), Hilt dependency injection, Kotlin coroutines |
| Accounts | Firebase Authentication |
| Maps and routing | osmdroid (OpenStreetMap), Valhalla routing API through Retrofit, Google Play location services |

## User testing protocol

**Hypotheses:** users choose their means of transport according to its environmental impact; they are influenced by the comparison of emissions; they use the save and share features; the app is perceived as usable (SUS > 72).

**Features tested:** choice of transport, navigation, CO₂ estimates, favourites, sharing.

## Run it

```bash
git clone https://github.com/ThibPltt/-Vert-le-chemin.git
```

Open the folder in Android Studio, let Gradle sync, then run the `app` configuration on a device or an emulator (Android 9 or later).

**Author:** Thibaut Platet · ENSIM · [thibautplatet.com](https://thibautplatet.com/en/)

<details>
<summary>🇫🇷 En bref</summary>

Application Android (Kotlin) qui estime les émissions de CO₂ d'un trajet quotidien à partir des données de l'ADEME et propose des alternatives plus vertueuses. Projet de 4e année à l'ENSIM : cahier des charges, maquettes et prototype avec connexion, choix de la destination, calcul et affichage de l'itinéraire, navigation.
</details>

# Share My Book

Application Android de gestion et de partage de bibliothèque personnelle développée en Kotlin avec Jetpack Compose.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-green.svg)](https://developer.android.com/jetpack/compose)
[![MinSDK](https://img.shields.io/badge/MinSDK-24-orange.svg)](https://developer.android.com/about/versions/nougat)

## Présentation

**Share My Book** permet de :
- Scanner des ISBN pour ajouter des livres automatiquement
- Gérer sa bibliothèque personnelle
- Prêter et emprunter des livres via QR Code
- Garder une trace de ses échanges

## Fonctionnalités

### Gestion de Bibliothèque
- **Scan ISBN** avec ML Kit + récupération automatique des métadonnées (OpenLibrary API)
- **Saisie manuelle** si le livre n'est pas trouvé en ligne
- **3 onglets** : Tous / Mes livres / Empruntés
- **Badges visuels** : Statut du livre en un coup d'œil

### Échanges de Livres
- **Système de transaction sécurisé** : INIT → ACCEPT → RESULT
- **Partage par QR Code** (génération et scan)
- **Saisie manuelle** du code de partage
- **Prévention des doublons** : impossible d'emprunter deux fois le même livre

### Interface
- **Material Design 3** avec Jetpack Compose
- **FAB expansible** pour actions rapides
- **Notifications colorées** (succès/erreur)
- **Profil utilisateur** personnalisable

## Installation

### Pour les Utilisateurs Finaux

#### Option 1 : Installation directe de l'APK
1. Téléchargez l'APK : **[app-release.apk](https://github.com/votre_depot/releases/latest)** *(Remplacez par votre lien)*
2. Activez "Sources inconnues" dans les paramètres Android
3. Ouvrez l'APK téléchargé et suivez les instructions

#### Option 2 : Build depuis le code source (voir section Développeurs ci-dessous)

### Pour les Développeurs

#### Prérequis
- **Android Studio** : Ladybug (2024.2.1) ou supérieur
- **JDK** : Version 11 ou supérieure
- **SDK Android** : API 24+ (Android 7.0) minimum, API 36 recommandée
- **Gradle** : 8.x (inclus dans le projet)

#### Étapes d'Installation

1. **Cloner le repository**
   ```bash
   git clone https://github.com/votre_depot/iai3androidshareMyBook.git
   cd iai3androidshareMyBook
   ```

2. **Ouvrir dans Android Studio**
   - Lancez Android Studio
   - File → Open → Sélectionnez le dossier du projet
   - Attendez la synchronisation Gradle (automatique)

3. **Configuration (optionnelle)**
   - Vérifiez que le SDK Android est bien configuré (File → Project Structure → SDK Location)
   - Si vous utilisez un émulateur, créez un AVD (API 24+) via AVD Manager

4. **Build le projet**
   ```bash
   ./gradlew build
   ```
   Ou via Android Studio : Build → Make Project (Ctrl+F9)

5. **Lancer l'application**
   - Connectez un appareil Android via USB (avec débogage USB activé)
   - Ou lancez un émulateur Android
   - Cliquez sur Run ou utilisez Shift+F10

#### Build de l'APK Release

Pour générer un APK prêt à distribuer :

```bash
./gradlew assembleRelease
```

L'APK sera généré dans : `app/build/outputs/apk/release/app-release.apk`

**Note :** Pour une version signée, configurez le keystore dans `app/build.gradle.kts` :
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("path/to/keystore.jks")
        storePassword = "your_store_password"
        keyAlias = "your_key_alias"
        keyPassword = "your_key_password"
    }
}
```

#### Vérifier l'Installation

Après le lancement, vérifiez que :
- L'écran de démarrage (Splash) s'affiche
- L'écran principal (liste de livres) apparaît
- Le bouton flottant (+) est accessible
- Les permissions caméra sont demandées lors du premier scan

#### Dépannage

| Problème | Solution |
|----------|----------|
| "SDK not found" | Installez le SDK Android via SDK Manager |
| "Gradle sync failed" | Vérifiez votre connexion internet et relancez la sync |
| "Device not found" | Activez le débogage USB sur votre appareil |
| Erreur de build Room | Invalidate Caches → Restart (File menu) |

## Tests

### Lancer les Tests Unitaires
```bash
./gradlew test
```

### Lancer les Tests Instrumentés (nécessite un appareil/émulateur)
```bash
./gradlew connectedAndroidTest
```

### Plan de Tests Fonctionnels
Voir [TEST_PLAN.md](TEST_PLAN.md) pour les scénarios de test manuels End-to-End.

## Documentation

- **[DOCUMENTATION.md](DOCUMENTATION.md)** : Architecture détaillée, choix techniques et justifications
- **[TEST_PLAN.md](TEST_PLAN.md)** : Scénarios de tests fonctionnels

## Technologies Utilisées

| Catégorie | Technologie | Version |
|-----------|-------------|---------|
| Langage | Kotlin | 2.0.0 |
| UI | Jetpack Compose | Latest |
| Architecture | MVVM | - |
| Base de données | Room | Latest (KSP) |
| Réseau | Ktor Client | 2.3.12 |
| Scan | CameraX + ML Kit | 1.5.1 / 17.3.0 |
| Images | Coil | 2.7.0 |
| QR Code | QRGen | 3.0.1 |
| DI | Manual (ViewModelFactory) | - |

## Structure du Projet

```
app/src/main/java/fr/enssat/sharemybook/edkfet_inc/
├── data/
│   ├── auth/           # AuthManager (UUID, SharedPrefs)
│   ├── local/          # Room Database (DAO, Entities)
│   │   ├── dao/
│   │   └── repository/
│   └── remote/         # Ktor Services (OpenLibrary, Backend)
├── model/              # Data models (Book, User, BookState)
├── ui/
│   ├── screens/        # Écrans Compose
│   ├── theme/          # Material Theme personnalisé
│   └── viewmodel/      # ViewModels (State management)
├── MainActivity.kt     # Point d'entrée + NavHost
└── ShareMyBookApplication.kt  # Application class
```

## Conformité avec le Sujet

- Scanner des ISBN pour stocker en base de données
- Prêter des livres et garder une trace
- Architecture MVVM recommandée
- Room pour la persistance locale
- OpenLibrary API pour les métadonnées
- QR Code pour le partage (QRGen)
- CameraX + ML Kit pour le scan
- Backend Cloud Functions pour les transactions
- UUID unique par utilisateur (SharedPreferences)
- Documentation complète (Markdown)
- Package ID: `fr.enssat.sharemybook.edkfet_inc`

## Développeurs

- **Binôme** : Valentin DEROUET & Arthur HAVET
- **Package ID** : `fr.enssat.sharemybook.edkfet_inc`
- **École** : ENSSAT
- **Cours** : Projet Android

## Licence

Ce projet est développé dans un cadre académique (ENSSAT).

## Problèmes Connus

- Le polling des transactions (1 req/sec) peut être optimisé avec WebSockets
- Pas de synchronisation cloud (données uniquement locales)
- Les permissions caméra doivent être accordées manuellement si refusées

---

**Version** : 1.0

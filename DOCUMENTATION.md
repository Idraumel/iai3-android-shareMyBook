# Documentation Technique - Share My Book

## Vue d'ensemble

Share My Book est une application Android développée en Kotlin avec Jetpack Compose permettant de gérer sa bibliothèque personnelle et d'échanger des livres avec d'autres utilisateurs via un système sécurisé de QR Codes.

**Package ID :** `fr.enssat.sharemybook.edkfet_inc`

## Architecture

L'application suit l'architecture recommandée par Android : **MVVM (Model-View-ViewModel)** avec une séparation claire des responsabilités.

### Structure des couches

```
┌─────────────────────────────────┐
│   UI Layer (Jetpack Compose)    │
│  - Screens (BookList, Detail)   │
│  - ViewModels (State Management)│
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│      Domain Layer                │
│  - Repositories (Book, User)     │
│  - Business Logic                │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│      Data Layer                  │
│  - Room Database (Local)         │
│  - Ktor Services (Remote)        │
│  - AuthManager (SharedPrefs)     │
└──────────────────────────────────┘
```

- **Modèle :** Entités Room (`Book`, `User`) et modèles de données pour l'API (Transaction models)
- **Vue :** Écrans Jetpack Compose déclaratifs et réactifs
- **ViewModel :** Gestion de la logique métier, des appels réseau et de la persistance via les Repositories

## Conformité avec le Sujet

### Fonctionnalités Principales Implémentées

#### 1. Scanner les ISBNs pour stocker en base de données
- **Technologie :** CameraX + ML Kit Barcode Scanning
- **Implémentation :** Activity dédiée au scan (`ScanActivity`) appelée via `registerForActivityResult`
- **Récupération des métadonnées :** API OpenLibrary (`https://openlibrary.org/api/volumes/brief/isbn/{ISBN}.json`)
- **Fallback manuel :** Formulaire de saisie si l'ISBN n'est pas trouvé

#### 2. Prêter des livres et garder une trace
- **Système d'annotation Room :**
  - Livre prêté : reste dans la base du propriétaire avec `borrowedByUuid` renseigné
  - Livre emprunté : ajouté dans la base de l'emprunteur avec `lentByUuid` renseigné
- **Transaction en 3 étapes :**
  - `INIT` : Le propriétaire crée la transaction
  - `ACCEPT` : L'emprunteur accepte la transaction
  - `RESULT` : Le propriétaire récupère les informations complètes
- **Partage via QR Code :** Génération (QRGen) et scan (ML Kit) du `shareId`

## Choix Techniques Détaillés

### 1. Persistance des Données (Room)

**Base de données Room** avec deux tables principales :

#### Table `books`
```kotlin
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val uuid: String,              // UUID unique du livre
    val ownerUuid: String,         // UUID du propriétaire
    val isbn: String,
    val title: String,
    val authors: String,
    val coverUrl: String?,
    val state: BookState,          // AVAILABLE, BORROWED, LENT
    val borrowedByUuid: String?,   // UUID de l'emprunteur
    val lentByUuid: String?        // UUID du prêteur
)
```

#### Table `users`
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uuid: String,
    val fullName: String,
    val tel: String,
    val email: String
)
```

**Logique de transaction :**
- Lors d'un **prêt (LOAN)** :
  - Livre du propriétaire : `borrowedByUuid` = UUID emprunteur, `state` = LENT
  - Livre de l'emprunteur : Nouveau livre avec `lentByUuid` = UUID propriétaire, `state` = BORROWED
  - Les utilisateurs sont mutuellement ajoutés dans les bases locales
- Lors d'un **retour (RETURN)** :
  - Livre du propriétaire : `borrowedByUuid` = null, `state` = AVAILABLE
  - Livre de l'emprunteur : Supprimé de la base
  - Les utilisateurs sont retirés s'ils ne servent plus

**Déduplication :** L'interface affiche une seule instance par ISBN dans l'onglet "Tous" pour éviter la confusion.

### 2. Réseau & API (Ktor 2.3.12)

**Ktor Client** est utilisé pour deux services distincts :

#### OpenLibraryService
- **Endpoint :** `GET https://openlibrary.org/api/volumes/brief/isbn/{ISBN}.json`
- **Fonctionnalité :** Récupère titre, auteurs et URL de couverture
- **Gestion d'erreur :** Fallback sur formulaire manuel si le livre n'est pas trouvé

#### BackendService
- **Base URL :** `https://europe-west9-mythic-cocoa-442917-i7.cloudfunctions.net/shareMyBook`
- **Endpoints :**
  - `POST /init` : Initie une transaction (LOAN ou RETURN)
  - `POST /accept/{shareId}` : Accepte une transaction
  - `GET /result/{shareId}` : Récupère le résultat (polling avec délai d'1 seconde)

**Configuration Ktor :**
```kotlin
HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
}
```

### 3. Scan & Capture d'Images

#### CameraX + ML Kit
- **CameraX :** Gestion moderne de la caméra Android
- **ML Kit Barcode Scanning :** Détection en temps réel des codes-barres (ISBN) et QR Codes
- **Implémentation :** `ScanActivity` réutilisable pour les deux types de scan
- **Retour de résultat :** Via `registerForActivityResult` avec un contrat personnalisé

#### Coil (Image Loading)
- **Version :** 2.7.0
- **Usage :** Chargement asynchrone des couvertures de livres depuis OpenLibrary
- **Avantages :** Cache automatique, gestion mémoire optimisée, support Compose natif

### 4. Génération de QR Codes (QRGen)

**Librairie :** `com.github.kenglxn.QRGen:android:3.0.1`

**Format du QR Code :**
```json
{
  "shareId": "93295976-6c83-4111-afc7-f6bc7e36c01f"
}
```

**Génération :**
```kotlin
val bitmap = QRCode.from(shareIdJson)
    .withSize(500, 500)
    .bitmap()
```

### 5. Gestion des Utilisateurs

#### AuthManager (SharedPreferences)
- **UUID Utilisateur :** Généré au premier lancement avec `UUID.randomUUID()`
- **Persistance :** Stocké dans `SharedPreferences` pour survivre aux redémarrages
- **Informations :** Nom complet, téléphone, email (modifiables dans le profil)

**Pourquoi SharedPreferences ?**
- Simple et suffisant pour un UUID unique
- Pas de nécessité de base de données pour cette donnée unique
- Conforme aux recommandations du sujet (pas de backend d'authentification)

**Note :** Dans une application en production, un backend d'authentification (OAuth, Firebase Auth) serait utilisé. Pour ce projet, la simplicité est privilégiée.

## Interface Utilisateur

L'interface a été conçue avec **Material Design 3** et Jetpack Compose pour être intuitive, moderne et responsive.

### Navigation et Architecture UI

**Navigation Compose** avec les écrans principaux :
- `SplashScreen` : Écran de démarrage avec logo
- `BookListScreen` : Écran principal avec liste de livres
- `BookDetailScreen` : Détails et actions sur un livre
- `LendBookScreen` : Affichage QR Code pour transaction
- `AddBookManuallyScreen` : Formulaire de saisie manuelle
- `ProfileScreen` : Gestion du profil utilisateur
- `MyLoansScreen` : Historique des emprunts/prêts

### Écran Principal (BookListScreen)

#### Organisation par Onglets
**3 onglets avec compteurs dynamiques :**
- **Tous :** Affiche tous les livres disponibles (dédupliqués par ISBN) avec badges de statut
- **Mes livres :** Uniquement les livres que vous possédez (filtrés par `ownerUuid`)
- **Empruntés :** Uniquement les livres que vous avez empruntés (filtrés par `lentByUuid`)

#### Actions Rapides (FAB Expansible)
**Floating Action Button** avec deux actions :
- **Ajouter un livre** : Ouvre `ScanActivity` pour scanner un ISBN
- **Emprunter un livre** : Ouvre `ScanActivity` pour scanner un QR Code de transaction

#### Badges de Statut Visuels
Chaque livre affiche des **chips colorés** indiquant son état :
- **"Mon livre" (Vert)** : Livre vous appartenant et disponible
- **"Emprunté" (Bleu)** : Livre que vous avez emprunté
- **"Prêté" (Orange)** : Livre que vous avez prêté à quelqu'un

#### Barre d'Actions
- **Bouton Profile** : Accès au profil utilisateur
- **Bouton Saisie Manuelle** : Permet d'entrer un `shareId` manuellement (utile pour le test ou si le scan QR ne fonctionne pas)

### Écran Détails (BookDetailScreen)

#### Affichage des Informations
- **Couverture du livre** : Image chargée via Coil (ou placeholder si absente)
- **Métadonnées** : Titre, auteurs, ISBN
- **Statut actuel** : "Disponible", "Prêté à [Nom]", "Emprunté à [Nom]"

#### Actions Contextuelles
Les boutons affichés dépendent du statut du livre :

**Propriétaire + Disponible :**
- "Prêter ce livre (Générer QR Code)" → Ouvre `LendBookScreen` avec action LOAN
- Icône de suppression (si livre non prêté)

**Propriétaire + Prêté :**
- "Récupérer ce livre (Générer QR Code)" → Ouvre `LendBookScreen` avec action RETURN
- Pas de suppression possible (livre prêté)

**Emprunteur :**
- "Scanner QR pour rendre" → Scanne le QR Code de retour
- "Entrer code manuellement" → Saisie du `shareId` de retour

### Écran de Transaction (LendBookScreen)

**Affichage lors de l'initialisation d'une transaction :**

#### Informations Visibles
- **QR Code** : Bitmap 500x500 généré par QRGen, affiché en grand
- **ShareId** : Code affiché dans une carte copiable
- **Informations du livre** : Titre et auteur
- **Type d'action** : "Prêt" ou "Retour"

#### Polling Automatique
- **Requête GET /result/{shareId}** : Toutes les 1 seconde
- **Indicateur de chargement** : "En attente du partenaire..."
- **Timeout** : Aucun (l'utilisateur peut annuler manuellement)
- **Succès** : Navigation automatique vers l'écran principal avec snackbar de confirmation

### Écran de Saisie Manuelle (AddBookManuallyScreen)

**Formulaire complet pour ajouter un livre sans API :**
- Champ **Titre** (obligatoire)
- Champ **Auteurs** (obligatoire)
- Champ **ISBN** (obligatoire)
- Champ **URL Couverture** (optionnel)
- Bouton **"Ajouter le livre"**

**Validation :**
- Tous les champs obligatoires doivent être remplis
- Message d'erreur si champs vides
- Snackbar de succès après ajout

### Écran Profil (ProfileScreen)

**Gestion des informations utilisateur :**
- **UUID** : Affiché en lecture seule (non modifiable)
- **Nom complet** : TextField modifiable
- **Téléphone** : TextField modifiable (format recommandé : +33...)
- **Email** : TextField modifiable
- **Bouton "Enregistrer"** : Sauvegarde en base Room
- **Validation** : Vérification de format email basique

### Système de Notifications (Snackbar)

**Notifications colorées avec gestion avancée :**

#### Types de Notifications
- **Succès (Vert - `SnackbarType.SUCCESS`)** :
  - "Livre ajouté avec succès"
  - "Livre emprunté à [Nom]"
  - "Livre rendu avec succès"
- **Erreur (Rouge - `SnackbarType.ERROR`)** :
  - "Code invalide ou expiré"
  - "Vous avez déjà emprunté ce livre"
  - "Erreur de connexion"
- **Info (Bleu - `SnackbarType.INFO`)** :
  - Messages généraux d'information

#### Fonctionnalités
- **Bouton de fermeture (X)** : Toutes les notifications peuvent être fermées manuellement
- **Swipe to dismiss** : Geste de glissement pour fermer
- **Durée adaptée** : 4-6 secondes selon l'importance du message
- **Icônes** : Check pour succès, croix pour erreur

## Sécurité et Gestion d'Erreurs

### Prévention des Doublons
- **Vérification avant emprunt :**
  ```kotlin
  // Vérifie qu'un livre (par UUID et ISBN) n'est pas déjà emprunté
  val existingBook = bookDao.getBookByIsbnAndLentBy(isbn, ownerUuid)
  if (existingBook != null) {
      throw BookAlreadyBorrowedException()
  }
  ```
- **Message d'erreur clair :** "Vous avez déjà emprunté ce livre" si tentative de double emprunt

### Gestion des Transactions
- **Validation des codes :**
  - Vérification du format UUID du `shareId`
  - Parsing JSON sécurisé avec try-catch
- **Codes expirés :** Détection via code HTTP 404 ou 410
- **Erreurs réseau :**
  - Timeout configuré (10 secondes)
  - Message d'erreur adapté ("Vérifiez votre connexion")
- **Logging détaillé :** Utilisation de `Log.d/w/e` pour faciliter le débogage

### Expérience Utilisateur
- **Feedback immédiat :** Toutes les actions donnent un retour visuel (snackbar, loading indicator)
- **États de chargement :** CircularProgressIndicator pendant les opérations réseau
- **Possibilité d'annulation :** Bouton retour toujours accessible
- **Messages explicites :** Pas de codes d'erreur techniques, uniquement des messages compréhensibles

## Gestion des Permissions

### Permissions Requises (`AndroidManifest.xml`)
```xml
<!-- Requis pour scanner les ISBN et QR Codes -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Requis pour les appels réseau (OpenLibrary, Backend) -->
<uses-permission android:name="android.permission.INTERNET" />
```

### Demande Runtime (API 23+)
- **CAMERA** : Demandée au lancement de `ScanActivity`
- **Gestion du refus** : Message explicite et option d'ouvrir les paramètres

## Build et Configuration

### Configuration Gradle

**Versions Clés :**
- Kotlin : 2.0.0
- Compose BOM : (dernière version stable)
- Room : (KSP)
- Ktor : 2.3.12
- CameraX : 1.5.1
- ML Kit : 17.3.0

**Plugins :**
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)  // Pour Ktor JSON
    alias(libs.plugins.ksp)                   // Pour Room
}
```

**SDK Versions :**
- `minSdk = 24` (Android 7)
- `targetSdk = 36` (Android 15)
- `compileSdk = 36`

### Système de Build
- **Gradle Kotlin DSL** : Configuration type-safe
- **Version Catalog** : Gestion centralisée des dépendances (`libs.versions.toml`)
- **Desugaring** : Support Java 8+ APIs sur Android < 8.0

## Tests

### Tests Unitaires
Fichier : `app/src/test/java/fr/enssat/sharemybook/edkfet_inc/`

**Tests couverts :**
- Validation des modèles de données (`TransactionModels`)
- Logique métier du `BookRepository`
- Parsing JSON des réponses API
- Génération d'UUID unique

### Tests Instrumentés
Fichier : `app/src/androidTest/java/fr/enssat/sharemybook/edkfet_inc/`

**Tests couverts :**
- Vérification du package ID
- Tests Room Database (CRUD operations)
- Tests de navigation Compose

### Plan de Tests Fonctionnels
Voir [TEST_PLAN.md](TEST_PLAN.md) pour les scénarios de test End-to-End manuels.

## Limitations et Améliorations Futures

### Limitations Actuelles
- **Pas de synchronisation cloud** : Données uniquement locales (conforme au sujet)
- **Pas de backup automatique** : Perte de données si désinstallation
- **Polling pour transactions** : Non optimisé (pas de WebSockets)
- **Pas de gestion de conflits** : Si deux transactions simultanées sur le même livre

### Améliorations Possibles
- **Backend complet** : Base de données centralisée avec synchronisation
- **Notifications Push** : Alertes lors de demandes de retour
- **Historique détaillé** : Logs de toutes les transactions
- **Export PDF** : Liste des livres en PDF
- **Recherche avancée** : Filtres par auteur, genre, année
- **Mode hors-ligne** : Queue des actions réseau pour synchronisation ultérieure
- **Tests E2E automatisés** : Espresso/UI Automator pour les flux complets

## Ressources et Références

### Documentation Utilisée
- [Android Developers - Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning/android)
- [OpenLibrary API](https://openlibrary.org/developers/api)
- [Ktor Client Documentation](https://ktor.io/docs/client.html)

### Bibliothèques Tierces
- **Coil** : [https://coil-kt.github.io/coil/](https://coil-kt.github.io/coil/)
- **QRGen** : [https://github.com/kenglxn/QRGen](https://github.com/kenglxn/QRGen)

---

**Date de dernière mise à jour :** 25 Janvier 2026 <br>
**Version de l'application :** 1.0 <br>
**Auteur :** Valentin DEROUET et Arthur HAVET (IAI3 2026)

# Plan de Tests Fonctionnels - Share My Book

Ce document décrit les scénarios de test pour valider les flux de bout en bout (E2E) de l'application.

---

## Scénario 1 : Ajout d'un nouveau livre (Manuel)
**Objectif :** Vérifier qu'un utilisateur peut ajouter un livre à sa collection sans passer par le scan.

1. **Étape :** Depuis l'écran d'accueil, cliquer sur le bouton flottant (+) pour l'agrandir.
2. **Étape :** Cliquer sur "Ajouter un livre" (icône livre).
3. **Étape :** Pointer la caméra vers un texte quelconque (pour simuler un échec de scan ISBN) ou attendre le timeout.
4. **Étape :** Sur l'écran de saisie manuelle, remplir :
    - Titre : "Test Book Manual"
    - Auteurs : "Author Manual"
    - ISBN : "123456789"
5. **Étape :** Cliquer sur "Ajouter le livre".
6. **Résultat attendu :**
    - Une snackbar verte s'affiche confirmant l'ajout
    - Le livre "Test Book Manual" apparaît dans la liste des livres
    - Un badge vert "Mon livre" est visible sur le livre

---

## Scénario 2 : Flux de Prêt (Côté Propriétaire)
**Objectif :** Initier une transaction et attendre un emprunteur.

1. **Pré-requis :** Avoir un livre "Test Book Manual" (voir Scénario 1).
2. **Étape :** Cliquer sur un livre possédé (ex: "Test Book Manual") dans la liste.
3. **Étape :** Sur l'écran de détails, cliquer sur le bouton **"Prêter ce livre (Générer QR Code)"**.
4. **Étape :** Attendre le chargement.
5. **Résultat attendu :** Un QR Code s'affiche avec l'ID de transaction en dessous. Un indicateur de progression tourne en bas de l'écran avec le texte "En attente du partenaire...".

---

## Scénario 3 : Flux d'Emprunt (Côté Emprunteur - Scan QR)
**Objectif :** Scanner un QR Code de prêt pour récupérer un livre.
*Nécessite deux téléphones ou un simulateur + une image de QR Code valide.*

1. **Pré-requis :** L'appareil propriétaire est en attente (Scénario 2).
2. **Étape :** Sur le second appareil (emprunteur), cliquer sur le bouton (+) depuis l'accueil pour l'agrandir.
3. **Étape :** Cliquer sur "Emprunter un livre" (icône QR scanner).
4. **Étape :** Scanner le QR Code affiché sur le téléphone du propriétaire (Scénario 2).
5. **Étape :** Attendre la fin du processus de chargement.
6. **Résultat attendu :**
    - Une snackbar verte s'affiche : "Livre 'Test Book Manual' emprunté à [Nom du propriétaire]"
    - Le livre "Test Book Manual" apparaît dans la liste de l'emprunteur.
    - Un badge bleu "Emprunté" est visible sur le livre de l'emprunteur.

---

## Scénario 4 : Validation de la Synchronisation (Post-Prêt)
**Objectif :** Vérifier que les états locaux sont mis à jour après la transaction du Scénario 3.

1. **Vérification Propriétaire :** 
    - L'écran du propriétaire doit être passé automatiquement à "Succès : Livre prêté à [Nom de l'emprunteur]".
    - En revenant à l'accueil, le livre "Test Book Manual" doit avoir l'état "Prêté".
2. **Vérification Emprunteur :** 
    - Le livre "Test Book Manual" doit être visible dans sa liste.
    - En cliquant dessus, l'icône de suppression ne doit PAS être visible (car emprunté).

---

## Scénario 5 : Retour d'un livre (Flux Complet)
**Objectif :** Vérifier le processus de récupération d'un livre.

1. **Pré-requis :** Livre "Test Book Manual" est prêté (Scénario 4).
2. **Étape :** Sur l'appareil propriétaire, cliquer sur le livre "Test Book Manual" (état "Prêté").
3. **Étape :** Cliquer sur **"Récupérer ce livre (Générer QR Code)"**.
4. **Étape :** Sur l'appareil emprunteur, cliquer sur le livre "Test Book Manual".
5. **Étape :** Cliquer sur "Scanner QR" et scanner le nouveau QR Code généré par le propriétaire.
6. **Résultat attendu :** 
    - Sur l'appareil propriétaire, le livre "Test Book Manual" redevient "Disponible".
    - Sur l'appareil emprunteur, le livre "Test Book Manual" disparaît de sa collection (ou un message de succès s'affiche).

---

## Scénario 6 : Suppression d'un livre
**Objectif :** Vérifier qu'un propriétaire peut supprimer un livre si celui-ci n'est pas prêté.

1. **Pré-requis :** Créer un nouveau livre (ex: "Livre à Supprimer" - voir Scénario 1, mais ne pas le prêter).
2. **Étape :** Cliquer sur le livre "Livre à Supprimer" dans la liste.
3. **Étape :** Vérifier que l'icône de poubelle est visible dans la barre supérieure.
4. **Étape :** Cliquer sur l'icône de poubelle.
5. **Résultat attendu :** Une boîte de dialogue de confirmation "Confirmer la suppression" apparaît.
6. **Étape :** Cliquer sur "Annuler" dans la boîte de dialogue.
7. **Résultat attendu :** La boîte de dialogue se ferme, le livre n'est pas supprimé.
8. **Étape :** Cliquer à nouveau sur l'icône de poubelle.
9. **Étape :** Cliquer sur "Supprimer" dans la boîte de dialogue.
10. **Résultat attendu :**
    - Une snackbar verte "Livre 'Livre à Supprimer' supprimé !" s'affiche.
    - L'application revient à l'écran de la liste des livres.
    - Le livre "Livre à Supprimer" ne figure plus dans la liste.

---

## Scénario 7 : Suppression d'un livre prêté (Comportement attendu : Échec)
**Objectif :** Vérifier qu'un propriétaire ne peut pas supprimer un livre prêté.

1. **Pré-requis :** Avoir un livre prêté (ex: "Test Book Manual" - voir Scénario 4, côté propriétaire).
2. **Étape :** Cliquer sur le livre "Test Book Manual" dans la liste (appareil propriétaire).
3. **Étape :** Vérifier que l'icône de poubelle n'est PAS visible dans la barre supérieure.
4. **Résultat attendu :** L'icône de poubelle est absente, empêchant la tentative de suppression d'un livre prêté.

---

## Scénario 8 : Profil et Persistance
**Objectif :** Vérifier que les informations utilisateur sont conservées.

1. **Étape :** Cliquer sur l'icône "Profil" en haut à droite.
2. **Étape :** Modifier le nom ou le téléphone et enregistrer.
3. **Étape :** Quitter l'application (tuer le processus) et la relancer.
4. **Résultat attendu :** Les informations modifiées sont toujours présentes dans l'écran Profil.

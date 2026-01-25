package fr.enssat.sharemybook.edkfet_inc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authManager: AuthManager,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val user by profileViewModel.user.collectAsState()

    // State for form fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Update form fields when user data is loaded
    LaunchedEffect(user) {
        user?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phone ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authManager.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Déconnexion")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Informations personnelles", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nom complet") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Téléphone (requis pour transactions)") },
                placeholder = { Text("+33612345678") },
                modifier = Modifier.fillMaxWidth(),
                isError = phone.isBlank(),
                supportingText = if (phone.isBlank()) {
                    { Text("Le téléphone est requis pour prêter/emprunter des livres", color = MaterialTheme.colorScheme.error) }
                } else null
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Dark Mode Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column {
                        Text("Mode sombre", style = MaterialTheme.typography.titleMedium)
                        Text("Activer le thème sombre", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeToggle
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    user?.let {
                        profileViewModel.saveUser(it.copy(
                            fullName = fullName,
                            email = email,
                            phone = phone.takeIf { it.isNotBlank() }
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = fullName.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Enregistrer les modifications")
            }

            if (fullName.isBlank() || email.isBlank() || phone.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tous les champs doivent être remplis pour pouvoir prêter/emprunter des livres",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Display UUID as required by SRS
            user?.let {
                Text("UUID de l'application :", style = MaterialTheme.typography.labelSmall)
                Text(it.uuid, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

package fr.enssat.sharemybook.edkfet_inc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookManuallyScreen(navController: NavController, bookViewModel: BookViewModel, initialIsbn: String = "") {
    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf(initialIsbn) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter manuellement") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
        ) {
            Text("Le livre n'a pas été trouvé. Veuillez saisir ses informations.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du livre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = authors,
                onValueChange = { authors = it },
                label = { Text("Auteur(s)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && authors.isNotBlank()) {
                        bookViewModel.addBookManually(title, authors, isbn)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && authors.isNotBlank()
            ) {
                Text("Ajouter à ma bibliothèque")
            }
        }
    }
}

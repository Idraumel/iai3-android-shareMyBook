package fr.enssat.sharemybook.edkfet_inc.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import fr.enssat.sharemybook.edkfet_inc.R
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.SnackbarType // Assuming this is defined somewhere or needs to be created

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(bookId: Long, bookViewModel: BookViewModel, navController: NavController) {
    val context = LocalContext.current
    val books by bookViewModel.books.collectAsStateWithLifecycle()
    val book = books.find { it.id == bookId }
    val loggedInUserUuid by bookViewModel.loggedInUserUuid.collectAsStateWithLifecycle()
    val transactionState by bookViewModel.transactionState.collectAsStateWithLifecycle()
    var showManualReturnDialog by remember { mutableStateOf(false) }
    var manualReturnShareId by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf<SnackbarType>(SnackbarType.Info) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for delete confirmation

    val returnScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("scanned_value")?.let {
                    bookViewModel.onScanResult(it)
                }
            }
        }
    )

    LaunchedEffect(transactionState) {
        when (val state = transactionState) {
            is BookViewModel.TransactionStatus.Success -> {
                snackbarType = SnackbarType.Success
                snackbarHostState.showSnackbar(
                    message = state.message,
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                bookViewModel.resetTransactionState()
                navController.popBackStack() // Navigate back after successful operation
            }
            is BookViewModel.TransactionStatus.Error -> {
                snackbarType = SnackbarType.Error
                snackbarHostState.showSnackbar(
                    message = "Erreur: ${state.message}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                bookViewModel.resetTransactionState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val backgroundColor = when (snackbarType) {
                    SnackbarType.Success -> MaterialTheme.colorScheme.primaryContainer
                    SnackbarType.Error -> MaterialTheme.colorScheme.errorContainer
                    SnackbarType.Info -> MaterialTheme.colorScheme.surfaceVariant
                }
                val contentColor = when (snackbarType) {
                    SnackbarType.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                    SnackbarType.Error -> MaterialTheme.colorScheme.onErrorContainer
                    SnackbarType.Info -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = backgroundColor,
                    contentColor = contentColor,
                    actionColor = contentColor,
                    dismissActionContentColor = contentColor
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "Détails du livre") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    // Show delete button only if current user is owner and book is not lent/borrowed
                    if (book?.ownerUuid == loggedInUserUuid && book?.borrowedByUuid == null && book?.lentByUuid == null) {
                        IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer le livre")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (book != null) {
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                SubcomposeAsyncImage(
                    model = book.coverUrl,
                    contentDescription = "Couverture de ${book.title}",
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        AsyncImage(model = R.drawable.ic_book_placeholder, contentDescription = null)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Par ${book.authors}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ISBN: ${book.isbn}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                val displayState = when {
                    book.borrowedByUuid != null -> "Prêté"
                    book.lentByUuid != null -> "Emprunté"
                    else -> "Disponible"
                }
                Text(text = "Statut: $displayState", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                // Case 1: I am the owner and the book is available -> I can LEND it
                if (book.ownerUuid == loggedInUserUuid && book.borrowedByUuid == null && book.lentByUuid == null) {
                    Button(
                        onClick = {
                            bookViewModel.resetTransactionState()
                            navController.navigate("transaction/${book.id}/LOAN")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Prêter ce livre (Générer QR Code)")
                    }
                }

                // Case 2: I am the owner and the book is lent -> I can trigger RETURN to get it back
                if (book.ownerUuid == loggedInUserUuid && book.borrowedByUuid != null) {
                    Button(
                        onClick = {
                            bookViewModel.resetTransactionState()
                            navController.navigate("transaction/${book.id}/RETURN")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Récupérer ce livre (Générer QR Code)")
                    }
                }

                // Case 3: I am the borrower (lentByUuid is not null)
                if (book.lentByUuid != null) {
                    Column {
                        Text(
                            "Ce livre est emprunté. Pour le rendre, scannez le QR code de retour du propriétaire.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(context, Class.forName("fr.enssat.sharemybook.edkfet_inc.ScanActivity"))
                                    returnScanLauncher.launch(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("Scanner QR")
                            }
                            OutlinedButton(
                                onClick = { showManualReturnDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Entrer code")
                            }
                        }
                    }
                }
            }

            // Manual Return ShareId Dialog
            if (showManualReturnDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showManualReturnDialog = false
                        manualReturnShareId = ""
                    },
                    title = { Text("Rendre le livre") },
                    text = {
                        Column {
                            Text("Entrez le code de retour (shareId) :")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = manualReturnShareId,
                                onValueChange = { manualReturnShareId = it },
                                label = { Text("Code de retour") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (manualReturnShareId.isNotBlank()) {
                                    // Call the viewmodel directly for the return process
                                    // Since onScanResult handles both ISBN and JSON shareId,
                                    // we can use it or a more specific method
                                    bookViewModel.onScanResult("{\"shareId\":\"${manualReturnShareId.trim()}\"}")
                                    showManualReturnDialog = false
                                    manualReturnShareId = ""
                                }
                            }
                        ) {
                            Text("Rendre")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showManualReturnDialog = false
                                manualReturnShareId = ""
                            }
                        ) {
                            Text("Annuler")
                        }
                    }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Livre non trouvé")
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce livre ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        bookViewModel.deleteBook(bookId)
                        showDeleteConfirmationDialog = false
                        // Navigate back immediately after delete confirmation
                        navController.popBackStack()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

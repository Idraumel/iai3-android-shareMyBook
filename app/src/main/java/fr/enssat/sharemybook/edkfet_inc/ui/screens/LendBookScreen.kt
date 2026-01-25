package fr.enssat.sharemybook.edkfet_inc.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel
import net.glxn.qrgen.android.QRCode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import fr.enssat.sharemybook.edkfet_inc.data.remote.ShareResponse
import fr.enssat.sharemybook.edkfet_inc.data.remote.TransactionAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendBookScreen(
    navController: NavController, 
    bookViewModel: BookViewModel, 
    bookId: Long,
    action: TransactionAction = TransactionAction.LOAN
) {
    val books by bookViewModel.books.collectAsState()
    val book = books.find { it.id == bookId }
    val transactionState by bookViewModel.transactionState.collectAsState()

    val title = if (action == TransactionAction.LOAN) "Prêter un livre" else "Récupérer un livre"
    val instruction = if (action == TransactionAction.LOAN) "Faites scanner ce code à l'emprunteur" else "Faites scanner ce code à celui qui rend le livre"

    // Reset state when entering screen to ensure clean slate
    LaunchedEffect(Unit) {
        bookViewModel.resetTransactionState()
    }

    LaunchedEffect(book, transactionState) {
        if (book != null && transactionState is BookViewModel.TransactionStatus.Idle) {
            bookViewModel.startTransactionProcess(book, action)
        }
    }

    // Reset state when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            bookViewModel.resetTransactionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = transactionState) {
                is BookViewModel.TransactionStatus.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Initialisation de la transaction...")
                }
                is BookViewModel.TransactionStatus.WaitingForPartner -> {
                    Text(instruction, style = MaterialTheme.typography.titleLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Generate QR Code with JSON as specified in the subject
                    val qrJson = Json.encodeToString(ShareResponse(state.shareId))
                    val bitmap = QRCode.from(qrJson).withSize(512, 512).bitmap()

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code de transaction",
                        modifier = Modifier.size(250.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Code de partage :",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.shareId,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "(Pour test : copiez ce code)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("En attente du partenaire...", style = MaterialTheme.typography.bodyMedium)
                }
                is BookViewModel.TransactionStatus.Success -> {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(state.message, style = MaterialTheme.typography.headlineSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate("bookList") { popUpTo("bookList") { inclusive = true } } }) {
                        Text("Retour à l'accueil")
                    }
                }
                is BookViewModel.TransactionStatus.Error -> {
                    Text("Erreur: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { book?.let { bookViewModel.startTransactionProcess(it, action) } }) {
                        Text("Réessayer")
                    }
                }
                else -> {}
            }
        }
    }
}

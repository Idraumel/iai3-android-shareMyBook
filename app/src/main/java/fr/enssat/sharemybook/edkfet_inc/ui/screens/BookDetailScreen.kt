package fr.enssat.sharemybook.edkfet_inc.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import fr.enssat.sharemybook.edkfet_inc.R
import fr.enssat.sharemybook.edkfet_inc.model.BookState
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(bookId: Long, bookViewModel: BookViewModel, navController: NavController) {
    val books by bookViewModel.books.collectAsStateWithLifecycle()
    val book = books.find { it.id == bookId }
    val loggedInUserUuid by bookViewModel.loggedInUserUuid.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "Book Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (book != null) {
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                SubcomposeAsyncImage(
                    model = book.coverUrl,
                    contentDescription = "Book cover for ${book.title}",
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
                Text(text = "By ${book.authors}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ISBN: ${book.isbn}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "State: ${book.state}", style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(16.dp))

                if (book.state == BookState.AVAILABLE && book.ownerUuid != loggedInUserUuid) {
                    Button(
                        onClick = { bookViewModel.requestLoan(book) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Emprunter ce livre")
                    }
                }
            }
        } else {
            Text("Book not found", modifier = Modifier.padding(innerPadding))
        }
    }
}

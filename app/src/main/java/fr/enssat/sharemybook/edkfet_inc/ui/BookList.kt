package fr.enssat.sharemybook.edkfet_inc.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import fr.enssat.sharemybook.edkfet_inc.R
import fr.enssat.sharemybook.edkfet_inc.model.Book

@Composable
fun BookList(books: List<Book>, modifier: Modifier = Modifier, onItemClick: (Book) -> Unit) {
    LazyColumn(modifier = modifier.padding(vertical = 8.dp)) {
        items(books) { book ->
            BookItem(
                book = book,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable(
                        interactionSource = null, // No interaction source needed for this simple case
                        indication = rememberRipple(bounded = true), // Use the Material ripple effect
                        onClick = { onItemClick(book) }
                    )
            )
        }
    }
}

@Composable
fun BookItem(book: Book, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Book Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp),
                loading = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator() 
                    }
                },
                error = { 
                    AsyncImage(model = R.drawable.ic_book_placeholder, contentDescription = null)
                }
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = book.authors,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
package fr.enssat.sharemybook.edkfet_inc.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import fr.enssat.sharemybook.edkfet_inc.R
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.BookState

@Composable
fun BookList(
    books: List<Book>,
    currentUserUuid: String?,
    modifier: Modifier = Modifier,
    onItemClick: (Book) -> Unit
) {
    LazyColumn(modifier = modifier.padding(vertical = 8.dp)) {
        items(books) { book ->
            BookItem(
                book = book,
                isOwner = book.ownerUuid == currentUserUuid,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable(
                        interactionSource = null,
                        indication = rememberRipple(bounded = true),
                        onClick = { onItemClick(book) }
                    )
            )
        }
    }
}

@Composable
fun BookItem(book: Book, isOwner: Boolean, modifier: Modifier = Modifier) {
    // Debug logging for cover URL
    Log.d("BookItem", "Loading book: ${book.title}, Cover URL: '${book.coverUrl}'")

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl?.takeIf { it.isNotBlank() })
                    .crossfade(true)
                    .error(R.drawable.ic_book_placeholder)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .build(),
                contentDescription = "Book Cover",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp),
                loading = {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    AsyncImage(
                        model = R.drawable.ic_book_placeholder,
                        contentDescription = "Book placeholder",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(8.dp)
                    )
                }
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Text(
                    text = book.authors,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Ownership badge - only show if I own it AND didn't borrow it from someone
                    if (isOwner && book.lentByUuid == null) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Mon livre", style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    // Borrowed badge - show if this book was borrowed from someone
                    if (book.lentByUuid != null) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Emprunté", style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }

                    // Lent status badge - show if I own it and lent it to someone
                    if (isOwner && book.lentByUuid == null && book.borrowedByUuid != null) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Prêté", style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}
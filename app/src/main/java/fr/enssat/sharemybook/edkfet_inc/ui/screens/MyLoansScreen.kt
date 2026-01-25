package fr.enssat.sharemybook.edkfet_inc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.User
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.MyLoansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLoansScreen(navController: NavController, viewModel: MyLoansViewModel) {
    val lentBooks by viewModel.lentBooks.collectAsState()
    val borrowedBooks by viewModel.borrowedBooks.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Prêtés", "Empruntés")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Échanges") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val displayList = if (selectedTab == 0) lentBooks else borrowedBooks
                
                if (displayList.isEmpty()) {
                    item {
                        Text(
                            text = if (selectedTab == 0) "Vous n'avez prêté aucun livre." else "Vous n'avez aucun livre emprunté.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    items(displayList) { book ->
                        LoanCard(
                            book = book,
                            isLent = selectedTab == 0,
                            viewModel = viewModel,
                            onDetailClick = { navController.navigate("bookDetail/${book.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoanCard(book: Book, isLent: Boolean, viewModel: MyLoansViewModel, onDetailClick: () -> Unit) {
    val partnerUuid = if (isLent) book.borrowedByUuid else book.lentByUuid
    val partnerInfo by if (partnerUuid != null) {
        viewModel.getPartnerInfo(partnerUuid).collectAsState(initial = null)
    } else {
        remember { mutableStateOf<User?>(null) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
            Text(text = book.authors, style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            if (partnerInfo != null) {
                Text(
                    text = if (isLent) "Prêté à : ${partnerInfo?.fullName}" else "Emprunté à : ${partnerInfo?.fullName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "Email : ${partnerInfo?.email}", style = MaterialTheme.typography.bodySmall)
                partnerInfo?.phone?.let {
                    Text(text = "Tél : $it", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Text(text = "Chargement des infos partenaire...", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onDetailClick,
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            ) {
                Text("Gérer")
            }
        }
    }
}

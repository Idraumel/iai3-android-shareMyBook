package fr.enssat.sharemybook.edkfet_inc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import fr.enssat.sharemybook.edkfet_inc.ui.BookList
import fr.enssat.sharemybook.edkfet_inc.ui.screens.BookDetailScreen
import fr.enssat.sharemybook.edkfet_inc.ui.screens.LoginScreen
import fr.enssat.sharemybook.edkfet_inc.ui.screens.MyLoansScreen
import fr.enssat.sharemybook.edkfet_inc.ui.screens.ProfileScreen
import fr.enssat.sharemybook.edkfet_inc.ui.screens.SignUpScreen
import fr.enssat.sharemybook.edkfet_inc.ui.theme.Iai3androidshareMyBookTheme
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.AuthViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.MyLoansViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.ProfileViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Iai3androidshareMyBookTheme {
                ShareMyBookApp()
            }
        }
    }
}

@Composable
fun ShareMyBookApp() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as ShareMyBookApplication
    val authManager = application.authManager
    val loggedInUserUuid by authManager.loggedInUserUuid.collectAsState()

    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(userRepository = application.userRepository, authManager = authManager)
    )

    NavHost(
        navController = navController,
        startDestination = if (loggedInUserUuid == null) "loginGraph" else "appGraph"
    ) {
        navigation(startDestination = "login", route = "loginGraph") {
            composable("login") {
                LoginScreen(navController, authViewModel)
            }
            composable("signup") {
                SignUpScreen(navController, authViewModel)
            }
        }

        navigation(startDestination = "bookList", route = "appGraph") {
            val appViewModelFactory = ViewModelFactory(
                bookRepository = application.bookRepository,
                userRepository = application.userRepository,
                loanRepository = application.loanRepository,
                authManager = authManager
            )

            composable("bookList") {
                val bookViewModel: BookViewModel = viewModel(factory = appViewModelFactory)
                BookListScreen(navController, bookViewModel)
            }
            composable("profile") {
                val profileViewModel: ProfileViewModel = viewModel(factory = appViewModelFactory)
                ProfileScreen(navController, profileViewModel, authManager)
            }
            composable("myLoans") { // New destination
                val myLoansViewModel: MyLoansViewModel = viewModel(factory = appViewModelFactory)
                MyLoansScreen(navController, myLoansViewModel)
            }
            composable(
                route = "bookDetail/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { 
                val bookViewModel: BookViewModel = viewModel(factory = appViewModelFactory)
                val bookId = it.arguments?.getString("bookId")?.toLongOrNull()
                if (bookId != null) {
                    BookDetailScreen(bookId = bookId, bookViewModel = bookViewModel, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavHostController, bookViewModel: BookViewModel) {
    val context = LocalContext.current
    val books by bookViewModel.books.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("scanned_value")?.let {
                    bookViewModel.onScanResult(it)
                }
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Share My Book") },
                actions = {
                    IconButton(onClick = { navController.navigate("myLoans") }) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = "My Loans")
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { launcher.launch(Intent(context, ScanActivity::class.java)) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Book")
            }
        }
    ) { innerPadding ->
        BookList(books = books, modifier = Modifier.padding(innerPadding), onItemClick = {
            navController.navigate("bookDetail/${it.id}")
        })
    }
}

package fr.enssat.sharemybook.edkfet_inc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.AppDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import fr.enssat.sharemybook.edkfet_inc.ui.screens.*
import fr.enssat.sharemybook.edkfet_inc.ui.theme.Iai3androidshareMyBookTheme
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.AuthViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.BookViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.ProfileViewModel
import fr.enssat.sharemybook.edkfet_inc.ui.viewmodel.ViewModelFactory // Import the generic ViewModelFactory
import fr.enssat.sharemybook.edkfet_inc.data.remote.TransactionAction
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val bookRepository = BookRepository(database.bookDao())
        val userRepository = UserRepository(database.userDao())
        val authManager = AuthManager(applicationContext)

        setContent {
            // Read dark mode preference from SharedPreferences
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            var isDarkMode by remember {
                mutableStateOf(prefs.getBoolean("dark_mode", false))
            }

            Iai3androidshareMyBookTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShareMyBookApp(
                        bookRepository = bookRepository,
                        userRepository = userRepository,
                        authManager = authManager,
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { enabled ->
                            isDarkMode = enabled
                            prefs.edit().putBoolean("dark_mode", enabled).apply()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShareMyBookApp(
    bookRepository: BookRepository,
    userRepository: UserRepository,
    authManager: AuthManager,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(userRepository = userRepository, authManager = authManager))
    val bookViewModel: BookViewModel = viewModel(factory = ViewModelFactory(bookRepository = bookRepository, userRepository = userRepository, authManager = authManager))
    val profileViewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(userRepository = userRepository, authManager = authManager))
    // val myLoansViewModel: MyLoansViewModel = viewModel(factory = ViewModelFactory(bookRepository = bookRepository, userRepository = userRepository, authManager = authManager))

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController, authViewModel, authManager)
        }
        composable("signup") {
            SignUpScreen(navController, authViewModel)
        }
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("bookList") {
            BookListScreen(navController, bookViewModel, authViewModel)
        }
        composable("bookDetail/{bookId}") {
            val bookId = it.arguments?.getString("bookId")?.toLongOrNull()
            if (bookId != null) {
                BookDetailScreen(bookId, bookViewModel, navController)
            }
        }
        composable("profile") {
            ProfileScreen(navController, profileViewModel, authManager, isDarkMode, onDarkModeToggle)
        }
        composable("addBookManually") {
            AddBookManuallyScreen(navController, bookViewModel)
        }
        composable("transaction/{bookId}/{action}") {
            val bookId = it.arguments?.getString("bookId")?.toLongOrNull()
            val actionString = it.arguments?.getString("action")
            val action = if (actionString == "LOAN") TransactionAction.LOAN else TransactionAction.RETURN

            if (bookId != null) {
                LendBookScreen(navController, bookViewModel, bookId, action)
            }
        }
        // composable("myLoans") {
        //     MyLoansScreen(navController, myLoansViewModel)
        // }
    }
}

package fr.enssat.sharemybook.edkfet_inc

import android.app.Application
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.AppDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareMyBookApplication : Application() {
    
    // Using by lazy so the database and repositories are only created when they're needed
    val database by lazy { AppDatabase.getDatabase(this) }
    val bookRepository by lazy { BookRepository(database.bookDao()) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val authManager by lazy { AuthManager(this) }

    override fun onCreate() {
        super.onCreate()
        
        // Force the database to initialize and seed on startup
        CoroutineScope(Dispatchers.IO).launch {
            database.userDao().getAllUsers()
        }
    }
}

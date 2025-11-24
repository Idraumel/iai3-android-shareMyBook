package fr.enssat.sharemybook.edkfet_inc

import android.app.Application
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.AppDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.LoanRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository

class ShareMyBookApplication : Application() {
    // Using by lazy so the database and repositories are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val bookRepository by lazy { BookRepository(database.bookDao()) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val loanRepository by lazy { LoanRepository(database.loanDao()) }
    val authManager by lazy { AuthManager(this) }
}

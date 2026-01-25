package fr.enssat.sharemybook.edkfet_inc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository

class ViewModelFactory(
    private val bookRepository: BookRepository? = null,
    private val userRepository: UserRepository? = null,
    private val authManager: AuthManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java) && bookRepository != null && userRepository != null && authManager != null) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(bookRepository, userRepository, authManager) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java) && userRepository != null && authManager != null) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository, authManager) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java) && userRepository != null && authManager != null) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, authManager) as T
        }
        if (modelClass.isAssignableFrom(MyLoansViewModel::class.java) && bookRepository != null && userRepository != null && authManager != null) {
            @Suppress("UNCHECKED_CAST")
            return MyLoansViewModel(bookRepository, userRepository, authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class or required repository/manager not provided")
    }
}

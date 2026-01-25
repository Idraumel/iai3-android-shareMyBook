package fr.enssat.sharemybook.edkfet_inc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.flow.*

class MyLoansViewModel(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val userUuidFlow = authManager.loggedInUserUuid

    // Books I own that are currently lent to someone
    val lentBooks: StateFlow<List<Book>> = combine(bookRepository.allBooks, userUuidFlow) { books, uuid ->
        books.filter { it.ownerUuid == uuid && it.borrowedByUuid != null }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Books I have borrowed from someone else
    val borrowedBooks: StateFlow<List<Book>> = combine(bookRepository.allBooks, userUuidFlow) { books, uuid ->
        books.filter { it.lentByUuid != null }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getPartnerInfo(partnerUuid: String): Flow<User?> {
        return userRepository.getUser(partnerUuid)
    }
}

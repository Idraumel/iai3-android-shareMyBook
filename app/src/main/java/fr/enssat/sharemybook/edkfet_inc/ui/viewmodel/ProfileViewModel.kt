package fr.enssat.sharemybook.edkfet_inc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<User?> = authManager.loggedInUserUuid.flatMapLatest { uuid ->
        if (uuid != null) {
            userRepository.getUser(uuid)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveUser(user: User) {
        viewModelScope.launch {
            userRepository.update(user)
        }
    }
}

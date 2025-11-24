package fr.enssat.sharemybook.edkfet_inc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// A sealed class is a great way to represent the different states of our UI
sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    data object Loading : AuthResult()
    data object Idle : AuthResult()
}

class AuthViewModel(private val userRepository: UserRepository, private val authManager: AuthManager) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult = _authResult.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                authManager.login(user.uuid)
                _authResult.value = AuthResult.Success
            } else {
                _authResult.value = AuthResult.Error("Invalid email or password.")
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String, phone: String?) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            if (userRepository.getUserByEmail(email) != null) {
                _authResult.value = AuthResult.Error("A user with this email already exists.")
                return@launch
            }

            val newUser = User(fullName = fullName, email = email, password = password, phone = phone)
            val result = userRepository.insert(newUser)
            
            if (result != -1L) { // In Room, a failed insert (due to conflict) returns -1
                authManager.login(newUser.uuid)
                _authResult.value = AuthResult.Success
            } else {
                _authResult.value = AuthResult.Error("An unexpected error occurred during sign up.")
            }
        }
    }
    
    fun resetAuthResult() {
        _authResult.value = AuthResult.Idle
    }
}

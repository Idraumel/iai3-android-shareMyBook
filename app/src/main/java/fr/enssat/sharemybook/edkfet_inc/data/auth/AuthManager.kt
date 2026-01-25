package fr.enssat.sharemybook.edkfet_inc.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // The UUID for the current device's user profile (required by SRS)
    private val _localUserUuid = prefs.getString("local_user_uuid", null) ?: run {
        val newUuid = UUID.randomUUID().toString()
        prefs.edit().putString("local_user_uuid", newUuid).apply()
        newUuid
    }
    val localUserUuid: String = _localUserUuid

    // Session management for testing multiple users on one device
    private val _loggedInUserUuid = MutableStateFlow<String?>(prefs.getString("logged_in_user_uuid", null))
    val loggedInUserUuid: StateFlow<String?> = _loggedInUserUuid

    fun login(userUuid: String) {
        prefs.edit().putString("logged_in_user_uuid", userUuid).apply()
        _loggedInUserUuid.value = userUuid
    }

    fun logout() {
        prefs.edit().remove("logged_in_user_uuid").apply()
        _loggedInUserUuid.value = null
    }
}

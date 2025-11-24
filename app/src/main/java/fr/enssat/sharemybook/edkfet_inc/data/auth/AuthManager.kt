package fr.enssat.sharemybook.edkfet_inc.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

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

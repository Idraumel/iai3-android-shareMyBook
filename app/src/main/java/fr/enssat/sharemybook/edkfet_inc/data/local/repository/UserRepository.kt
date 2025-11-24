package fr.enssat.sharemybook.edkfet_inc.data.local.repository

import fr.enssat.sharemybook.edkfet_inc.data.local.dao.UserDao
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    fun getUser(uuid: String): Flow<User?> = userDao.getUser(uuid)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
}

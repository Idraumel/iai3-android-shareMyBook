package fr.enssat.sharemybook.edkfet_inc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Changed to IGNORE to prevent overwriting on email conflict
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE uuid = :uuid")
    fun getUser(uuid: String): Flow<User?>
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}

package fr.enssat.sharemybook.edkfet_inc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.BookDao
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.UserDao
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Book::class, User::class], version = 10, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sharemybook_final.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed only on creation
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                seedData(database)
                            }
                        }
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Safety check: if DB is empty (after migration), seed it
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val users = database.userDao().getAllUsersSync()
                                if (users.isEmpty()) {
                                    seedData(database)
                                }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedData(db: AppDatabase) {
            val user1Uuid = "e41bb15c-4c50-47a8-8974-9e74af81b3cc"
            val user2Uuid = "ceaf985f-d6ba-4185-8c39-e41a1849fe1e"

            val user1 = User(uuid = user1Uuid, fullName = "John Doe", email = "john.doe@example.com", phone = "+33612345678", password = "password123")
            val user2 = User(uuid = user2Uuid, fullName = "Jane Smith", email = "jane.smith@example.com", phone = "+33698765432", password = "password456")
            
            db.userDao().insert(user1)
            db.userDao().insert(user2)

            db.bookDao().insert(Book(ownerUuid = user1Uuid, isbn = "978-0618640157", title = "The Lord of the Rings", authors = "J.R.R. Tolkien", coverUrl = "https://covers.openlibrary.org/b/isbn/9780618640157-L.jpg"))
            db.bookDao().insert(Book(ownerUuid = user1Uuid, isbn = "978-0345339683", title = "The Hobbit", authors = "J.R.R. Tolkien", coverUrl = "https://covers.openlibrary.org/b/isbn/9780345339683-L.jpg"))
            db.bookDao().insert(Book(ownerUuid = user2Uuid, isbn = "978-0132350884", title = "Clean Code", authors = "Robert C. Martin", coverUrl = "https://covers.openlibrary.org/b/id/8230011-L.jpg"))
        }
    }
}

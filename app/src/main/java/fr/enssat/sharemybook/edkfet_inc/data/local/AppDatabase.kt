package fr.enssat.sharemybook.edkfet_inc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.BookDao
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.LoanDao
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.UserDao
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.BookRepository
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.UserRepository
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.Loan
import fr.enssat.sharemybook.edkfet_inc.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Book::class, User::class, Loan::class], version = 4, exportSchema = false) 
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sharemybook_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val bookRepository = BookRepository(it.bookDao())
                    val userRepository = UserRepository(it.userDao())

                    // Add sample users with passwords
                    val user1 = User(fullName = "John Doe", email = "john.doe@example.com", phone = "123456789", password = "password123")
                    val user2 = User(fullName = "Jane Smith", email = "jane.smith@example.com", phone = "987654321", password = "password456")
                    userRepository.insert(user1)
                    userRepository.insert(user2)

                    // Add sample books, owned by different users
                    bookRepository.insert(Book(ownerUuid = user1.uuid, isbn = "978-0618640157", title = "The Lord of the Rings", authors = "J.R.R. Tolkien", coverUrl = "https://covers.openlibrary.org/b/isbn/9780618640157-L.jpg"))
                    bookRepository.insert(Book(ownerUuid = user2.uuid, isbn = "978-0132350884", title = "Clean Code: A Handbook of Agile Software Craftsmanship", authors = "Robert C. Martin", coverUrl = "https://covers.openlibrary.org/b/id/8230011-L.jpg"))
                }
            }
        }
    }
}

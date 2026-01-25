package fr.enssat.sharemybook.edkfet_inc.data.local.dao

import androidx.room.*
import fr.enssat.sharemybook.edkfet_inc.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)
    
    @Query("DELETE FROM books WHERE isbn = :isbn AND lentByUuid IS NOT NULL")
    suspend fun deleteBorrowedBookByIsbn(isbn: String)
}

package fr.enssat.sharemybook.edkfet_inc.data.local.repository

import fr.enssat.sharemybook.edkfet_inc.data.local.dao.BookDao
import fr.enssat.sharemybook.edkfet_inc.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Book] from a given data source.
 */
class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun getBookById(id: Long): Flow<Book?> = bookDao.getBookById(id)

    suspend fun insert(book: Book) {
        bookDao.insert(book)
    }

    suspend fun update(book: Book) {
        bookDao.update(book)
    }

    suspend fun delete(book: Book) {
        bookDao.delete(book)
    }

    suspend fun deleteBorrowedBookByIsbn(isbn: String) {
        bookDao.deleteBorrowedBookByIsbn(isbn)
    }
}

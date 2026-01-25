package fr.enssat.sharemybook.edkfet_inc

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.enssat.sharemybook.edkfet_inc.data.local.AppDatabase
import fr.enssat.sharemybook.edkfet_inc.data.local.dao.BookDao
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.BookState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

/**
 * Instrumented tests for Share My Book application.
 * These tests run on an Android device/emulator and test Android-specific components.
 */
@RunWith(AndroidJUnit4::class)
class ShareMyBookInstrumentedTests {

    private lateinit var database: AppDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        bookDao = database.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    // ========================================
    // Package ID Tests
    // ========================================

    @Test
    fun app_packageName_isCorrect() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("fr.enssat.sharemybook.edkfet_inc", appContext.packageName)
    }

    // ========================================
    // Room Database Tests
    // ========================================

    @Test
    fun database_insert_andRetrieve() = runBlocking {
        // Create a test book
        val book = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.AVAILABLE
        )

        // Insert the book
        bookDao.insert(book)

        // Retrieve all books
        val allBooks = bookDao.getAllBooks().first()

        // Verify the book was inserted
        assertEquals(1, allBooks.size)
        assertEquals("Test Book", allBooks[0].title)
        assertEquals("Test Author", allBooks[0].authors)
        assertEquals("1234567890", allBooks[0].isbn)
        assertEquals(BookState.AVAILABLE, allBooks[0].state)
    }

    @Test
    fun database_insertMultiple_andRetrieveAll() = runBlocking {
        val book1 = Book(
            ownerUuid = "owner-1",
            isbn = "1111111111",
            title = "Book 1",
            authors = "Author 1"
        )
        val book2 = Book(
            ownerUuid = "owner-2",
            isbn = "2222222222",
            title = "Book 2",
            authors = "Author 2"
        )

        bookDao.insert(book1)
        bookDao.insert(book2)

        val allBooks = bookDao.getAllBooks().first()

        assertEquals(2, allBooks.size)
        assertTrue(allBooks.any { it.title == "Book 1" })
        assertTrue(allBooks.any { it.title == "Book 2" })
    }

    @Test
    fun database_update_modifiesExistingBook() = runBlocking {
        // Insert a book
        val book = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Original Title",
            authors = "Original Author",
            state = BookState.AVAILABLE
        )
        bookDao.insert(book)

        // Get the inserted book (to get the auto-generated ID)
        val insertedBook = bookDao.getAllBooks().first()[0]

        // Update the book
        val updatedBook = insertedBook.copy(
            title = "Updated Title",
            state = BookState.LENT,
            borrowedByUuid = "borrower-123"
        )
        bookDao.update(updatedBook)

        // Retrieve and verify
        val retrievedBook = bookDao.getBookById(insertedBook.id).first()

        assertNotNull(retrievedBook)
        assertEquals("Updated Title", retrievedBook?.title)
        assertEquals(BookState.LENT, retrievedBook?.state)
        assertEquals("borrower-123", retrievedBook?.borrowedByUuid)
    }

    @Test
    fun database_delete_removesBook() = runBlocking {
        // Insert a book
        val book = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Book to Delete",
            authors = "Author"
        )
        bookDao.insert(book)

        // Verify it was inserted
        val booksBeforeDelete = bookDao.getAllBooks().first()
        assertEquals(1, booksBeforeDelete.size)

        // Delete the book
        bookDao.delete(booksBeforeDelete[0])

        // Verify it was deleted
        val booksAfterDelete = bookDao.getAllBooks().first()
        assertEquals(0, booksAfterDelete.size)
    }

    @Test
    fun database_deleteBorrowedBookByIsbn_removesCorrectBook() = runBlocking {
        // Insert two books with same ISBN but different states
        val ownedBook = Book(
            ownerUuid = "owner-123",
            isbn = "1234567890",
            title = "Owned Book",
            authors = "Author",
            state = BookState.LENT,
            borrowedByUuid = "borrower-456"
        )
        val borrowedBook = Book(
            ownerUuid = "owner-123", // Owner is the same (the original owner)
            isbn = "1234567890",
            title = "Borrowed Book",
            authors = "Author",
            state = BookState.BORROWED,
            lentByUuid = "owner-123"
        )

        bookDao.insert(ownedBook)
        bookDao.insert(borrowedBook)

        // Verify both were inserted
        val booksBeforeDelete = bookDao.getAllBooks().first()
        assertEquals(2, booksBeforeDelete.size)

        // Delete borrowed book by ISBN
        bookDao.deleteBorrowedBookByIsbn("1234567890")

        // Verify only the borrowed book was deleted
        val booksAfterDelete = bookDao.getAllBooks().first()
        assertEquals(1, booksAfterDelete.size)

        // The remaining book should be the owned one (LENT state)
        val remainingBook = booksAfterDelete[0]
        assertEquals(BookState.LENT, remainingBook.state)
        assertNotNull(remainingBook.borrowedByUuid)
    }

    @Test
    fun database_getBookById_returnsCorrectBook() = runBlocking {
        // Insert books
        val book1 = Book(
            ownerUuid = "owner-1",
            isbn = "1111111111",
            title = "Book 1",
            authors = "Author 1"
        )
        val book2 = Book(
            ownerUuid = "owner-2",
            isbn = "2222222222",
            title = "Book 2",
            authors = "Author 2"
        )

        bookDao.insert(book1)
        bookDao.insert(book2)

        val allBooks = bookDao.getAllBooks().first()
        val firstBookId = allBooks[0].id

        // Get specific book
        val retrievedBook = bookDao.getBookById(firstBookId).first()

        assertNotNull(retrievedBook)
        assertEquals(firstBookId, retrievedBook?.id)
    }

    @Test
    fun database_getBookById_nonExistentId_returnsNull() = runBlocking {
        val nonExistentId = 99999L
        val book = bookDao.getBookById(nonExistentId).first()

        assertNull(book)
    }

    // ========================================
    // Business Logic Integration Tests
    // ========================================

    @Test
    fun loanTransaction_fullFlow_updatesDatabase() = runBlocking {
        val ownerUuid = "owner-123"
        val borrowerUuid = "borrower-456"

        // Step 1: Owner has a book
        val ownerBook = Book(
            ownerUuid = ownerUuid,
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.AVAILABLE
        )
        bookDao.insert(ownerBook)

        // Step 2: Book is lent (update owner's book)
        val insertedBook = bookDao.getAllBooks().first()[0]
        val lentBook = insertedBook.copy(
            state = BookState.LENT,
            borrowedByUuid = borrowerUuid
        )
        bookDao.update(lentBook)

        // Step 3: Borrower receives the book (new entry)
        val borrowedBook = Book(
            ownerUuid = ownerUuid, // Original owner
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.BORROWED,
            lentByUuid = ownerUuid
        )
        bookDao.insert(borrowedBook)

        // Verify: Should have 2 books in DB
        val allBooks = bookDao.getAllBooks().first()
        assertEquals(2, allBooks.size)

        // Verify: One LENT, one BORROWED
        assertTrue(allBooks.any { it.state == BookState.LENT })
        assertTrue(allBooks.any { it.state == BookState.BORROWED })

        // Verify: LENT book has borrowedByUuid
        val lentBookInDb = allBooks.first { it.state == BookState.LENT }
        assertEquals(borrowerUuid, lentBookInDb.borrowedByUuid)

        // Verify: BORROWED book has lentByUuid
        val borrowedBookInDb = allBooks.first { it.state == BookState.BORROWED }
        assertEquals(ownerUuid, borrowedBookInDb.lentByUuid)
    }

    @Test
    fun returnTransaction_fullFlow_updatesDatabase() = runBlocking {
        val ownerUuid = "owner-123"
        val borrowerUuid = "borrower-456"

        // Setup: Book is already lent
        val lentBook = Book(
            ownerUuid = ownerUuid,
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.LENT,
            borrowedByUuid = borrowerUuid
        )
        val borrowedBook = Book(
            ownerUuid = ownerUuid,
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.BORROWED,
            lentByUuid = ownerUuid
        )
        bookDao.insert(lentBook)
        bookDao.insert(borrowedBook)

        // Step 1: Return the book (update owner's book)
        val allBooks = bookDao.getAllBooks().first()
        val ownerBookInDb = allBooks.first { it.state == BookState.LENT }
        val returnedBook = ownerBookInDb.copy(
            state = BookState.AVAILABLE,
            borrowedByUuid = null
        )
        bookDao.update(returnedBook)

        // Step 2: Remove from borrower's collection
        bookDao.deleteBorrowedBookByIsbn("1234567890")

        // Verify: Should have 1 book in DB
        val booksAfterReturn = bookDao.getAllBooks().first()
        assertEquals(1, booksAfterReturn.size)

        // Verify: Book is AVAILABLE
        assertEquals(BookState.AVAILABLE, booksAfterReturn[0].state)
        assertNull(booksAfterReturn[0].borrowedByUuid)
    }

    // ========================================
    // Context and Application Tests
    // ========================================

    @Test
    fun context_isNotNull() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
    }

    @Test
    fun database_canBeCreated() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        assertNotNull(db)
        assertNotNull(db.bookDao())
        assertNotNull(db.userDao())

        db.close()
    }
}
package fr.enssat.sharemybook.edkfet_inc

import fr.enssat.sharemybook.edkfet_inc.data.remote.*
import fr.enssat.sharemybook.edkfet_inc.model.Book
import fr.enssat.sharemybook.edkfet_inc.model.BookState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.Assert.*
import java.util.UUID

/**
 * Unit tests for Share My Book application.
 * Tests cover data models, business logic, and serialization.
 */
class ShareMyBookUnitTests {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    // ========================================
    // Book Model Tests
    // ========================================

    @Test
    fun book_defaultState_isAvailable() {
        val book = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author"
        )
        assertEquals(BookState.AVAILABLE, book.state)
    }

    @Test
    fun book_uuid_isUnique() {
        val book1 = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author"
        )
        val book2 = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author"
        )
        assertNotEquals(book1.uuid, book2.uuid)
    }

    @Test
    fun book_lentState_hasValidUuid() {
        val borrowerUuid = UUID.randomUUID().toString()
        val book = Book(
            ownerUuid = "test-owner",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.LENT,
            borrowedByUuid = borrowerUuid
        )
        assertEquals(BookState.LENT, book.state)
        assertEquals(borrowerUuid, book.borrowedByUuid)
    }

    @Test
    fun book_borrowedState_hasValidLentByUuid() {
        val lenderUuid = UUID.randomUUID().toString()
        val book = Book(
            ownerUuid = lenderUuid,
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.BORROWED,
            lentByUuid = lenderUuid
        )
        assertEquals(BookState.BORROWED, book.state)
        assertEquals(lenderUuid, book.lentByUuid)
    }

    // ========================================
    // Transaction Model Tests
    // ========================================

    @Test
    fun transactionAction_hasCorrectValues() {
        assertEquals("LOAN", TransactionAction.LOAN.name)
        assertEquals("RETURN", TransactionAction.RETURN.name)
    }

    @Test
    fun transactionBook_canBeCreated() {
        val book = TransactionBook(
            uid = UUID.randomUUID().toString(),
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            covers = "https://example.com/cover.jpg"
        )
        assertNotNull(book.uid)
        assertEquals("1234567890", book.isbn)
        assertEquals("Test Book", book.title)
        assertEquals("Test Author", book.authors)
        assertEquals("https://example.com/cover.jpg", book.covers)
    }

    @Test
    fun transactionUser_canBeCreated() {
        val user = TransactionUser(
            uid = UUID.randomUUID().toString(),
            fullName = "John Doe",
            tel = "+33612345678",
            email = "john.doe@example.com"
        )
        assertNotNull(user.uid)
        assertEquals("John Doe", user.fullName)
        assertEquals("+33612345678", user.tel)
        assertEquals("john.doe@example.com", user.email)
    }

    // ========================================
    // JSON Serialization Tests
    // ========================================

    @Test
    fun transactionInitRequest_canBeSerializedToJson() {
        val request = TransactionInitRequest(
            action = TransactionAction.LOAN,
            book = TransactionBook(
                uid = "book-uuid",
                isbn = "1234567890",
                title = "Test Book",
                authors = "Test Author"
            ),
            owner = TransactionUser(
                uid = "owner-uuid",
                fullName = "Owner Name",
                tel = "+33612345678",
                email = "owner@example.com"
            )
        )

        val jsonString = json.encodeToString(request)

        assertTrue(jsonString.contains("\"action\":\"LOAN\""))
        assertTrue(jsonString.contains("\"isbn\":\"1234567890\""))
        assertTrue(jsonString.contains("\"title\":\"Test Book\""))
        assertTrue(jsonString.contains("\"fullName\":\"Owner Name\""))
    }

    @Test
    fun transactionAcceptRequest_canBeSerializedToJson() {
        val request = TransactionAcceptRequest(
            borrower = TransactionUser(
                uid = "borrower-uuid",
                fullName = "Borrower Name",
                tel = "+33687654321",
                email = "borrower@example.com"
            )
        )

        val jsonString = json.encodeToString(request)

        assertTrue(jsonString.contains("\"borrower\""))
        assertTrue(jsonString.contains("\"fullName\":\"Borrower Name\""))
        assertTrue(jsonString.contains("\"tel\":\"+33687654321\""))
    }

    @Test
    fun shareResponse_canBeDeserializedFromJson() {
        val jsonString = """
            {
                "shareId": "93295976-6c83-4111-afc7-f6bc7e36c01f"
            }
        """

        val response = json.decodeFromString<ShareResponse>(jsonString)

        assertEquals("93295976-6c83-4111-afc7-f6bc7e36c01f", response.shareId)
    }

    @Test
    fun transactionResponse_withAllFields_canBeDeserialized() {
        val jsonString = """
            {
                "action": "LOAN",
                "book": {
                    "uid": "book-uuid",
                    "isbn": "1234567890",
                    "title": "Test Book",
                    "authors": "Test Author"
                },
                "owner": {
                    "uid": "owner-uuid",
                    "fullName": "Owner Name",
                    "tel": "+33612345678",
                    "email": "owner@example.com"
                },
                "borrower": {
                    "uid": "borrower-uuid",
                    "fullName": "Borrower Name",
                    "tel": "+33687654321",
                    "email": "borrower@example.com"
                }
            }
        """

        val response = json.decodeFromString<TransactionResponse>(jsonString)

        assertEquals(TransactionAction.LOAN, response.action)
        assertNotNull(response.book)
        assertNotNull(response.owner)
        assertNotNull(response.borrower)
        assertEquals("Test Book", response.book?.title)
        assertEquals("Owner Name", response.owner?.fullName)
        assertEquals("Borrower Name", response.borrower?.fullName)
    }

    @Test
    fun transactionResponse_withPartialFields_canBeDeserialized() {
        val jsonString = """
            {
                "action": "LOAN",
                "book": {
                    "uid": "book-uuid",
                    "isbn": "1234567890",
                    "title": "Test Book",
                    "authors": "Test Author"
                },
                "owner": {
                    "uid": "owner-uuid",
                    "fullName": "Owner Name",
                    "tel": "+33612345678",
                    "email": "owner@example.com"
                }
            }
        """

        val response = json.decodeFromString<TransactionResponse>(jsonString)

        assertEquals(TransactionAction.LOAN, response.action)
        assertNotNull(response.book)
        assertNotNull(response.owner)
        assertNull(response.borrower) // Should be null as it's not in JSON
    }

    // ========================================
    // UUID Validation Tests
    // ========================================

    @Test
    fun uuid_generatedByRandomUUID_isValid() {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()

        // UUIDs should be 36 characters (including hyphens)
        assertEquals(36, uuid1.length)
        assertEquals(36, uuid2.length)

        // UUIDs should be unique
        assertNotEquals(uuid1, uuid2)

        // UUID format validation (basic check)
        assertTrue(uuid1.matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")))
    }

    @Test
    fun uuid_canBeParsedAndReconstructed() {
        val originalUuid = UUID.randomUUID()
        val uuidString = originalUuid.toString()
        val parsedUuid = UUID.fromString(uuidString)

        assertEquals(originalUuid, parsedUuid)
    }

    // ========================================
    // BookState Enum Tests
    // ========================================

    @Test
    fun bookState_hasThreeStates() {
        val states = BookState.entries
        assertEquals(3, states.size)
        assertTrue(states.contains(BookState.AVAILABLE))
        assertTrue(states.contains(BookState.LENT))
        assertTrue(states.contains(BookState.BORROWED))
    }

    @Test
    fun bookState_canBeConvertedToString() {
        assertEquals("AVAILABLE", BookState.AVAILABLE.name)
        assertEquals("LENT", BookState.LENT.name)
        assertEquals("BORROWED", BookState.BORROWED.name)
    }

    // ========================================
    // Business Logic Tests
    // ========================================

    @Test
    fun book_loanTransaction_shouldUpdateState() {
        // Simulate a loan transaction
        var book = Book(
            ownerUuid = "owner-123",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.AVAILABLE,
            borrowedByUuid = null
        )

        // Owner lends the book
        val borrowerUuid = "borrower-456"
        book = book.copy(
            state = BookState.LENT,
            borrowedByUuid = borrowerUuid
        )

        assertEquals(BookState.LENT, book.state)
        assertEquals(borrowerUuid, book.borrowedByUuid)
    }

    @Test
    fun book_returnTransaction_shouldResetState() {
        // Simulate a return transaction
        var book = Book(
            ownerUuid = "owner-123",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.LENT,
            borrowedByUuid = "borrower-456"
        )

        // Owner gets the book back
        book = book.copy(
            state = BookState.AVAILABLE,
            borrowedByUuid = null
        )

        assertEquals(BookState.AVAILABLE, book.state)
        assertNull(book.borrowedByUuid)
    }

    @Test
    fun book_borrowerPerspective_shouldHaveLentByUuid() {
        // From borrower's perspective
        val book = Book(
            ownerUuid = "owner-123",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author",
            state = BookState.BORROWED,
            lentByUuid = "owner-123"
        )

        assertEquals(BookState.BORROWED, book.state)
        assertEquals("owner-123", book.lentByUuid)
        assertEquals("owner-123", book.ownerUuid)
    }

    // ========================================
    // Edge Cases Tests
    // ========================================

    @Test
    fun transactionBook_withoutCoverUrl_shouldBeNull() {
        val book = TransactionBook(
            uid = "book-uuid",
            isbn = "1234567890",
            title = "Test Book",
            authors = "Test Author"
        )

        assertNull(book.covers)
    }

    @Test
    fun transactionResponse_emptyResponse_allFieldsNull() {
        val jsonString = "{}"
        val response = json.decodeFromString<TransactionResponse>(jsonString)

        assertNull(response.action)
        assertNull(response.book)
        assertNull(response.owner)
        assertNull(response.borrower)
        assertNull(response.shareId)
    }

    @Test
    fun isbn_validation_basicFormat() {
        val validIsbn10 = "0596156715"
        val validIsbn13 = "9780596156718"

        // ISBN-10 should be 10 digits
        assertEquals(10, validIsbn10.length)
        assertTrue(validIsbn10.all { it.isDigit() })

        // ISBN-13 should be 13 digits
        assertEquals(13, validIsbn13.length)
        assertTrue(validIsbn13.all { it.isDigit() })
    }
}
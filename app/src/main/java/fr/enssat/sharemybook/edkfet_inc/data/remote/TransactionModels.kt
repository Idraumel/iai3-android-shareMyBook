package fr.enssat.sharemybook.edkfet_inc.data.remote

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionAction {
    LOAN, RETURN
}

@Serializable
data class TransactionBook(
    val uid: String,
    val isbn: String,
    val title: String,
    val authors: String,
    val covers: String? = null
)

@Serializable
data class TransactionUser(
    val uid: String,
    val fullName: String,
    val tel: String,
    val email: String
)

@Serializable
data class TransactionInitRequest(
    val action: TransactionAction,
    val book: TransactionBook,
    val owner: TransactionUser
)

@Serializable
data class TransactionAcceptRequest(
    val borrower: TransactionUser
)

@Serializable
data class TransactionResponse(
    val action: TransactionAction? = null,
    val book: TransactionBook? = null,
    val owner: TransactionUser? = null,
    val borrower: TransactionUser? = null,
    val shareId: String? = null
)

@Serializable
data class ShareResponse(
    val shareId: String
)

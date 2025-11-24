package fr.enssat.sharemybook.edkfet_inc.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["ownerUuid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["borrowerUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("ownerUuid"), Index("borrowerUuid")]
)
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val ownerUuid: String,
    val borrowerUuid: String,
    val status: LoanStatus,
    val action: LoanAction,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

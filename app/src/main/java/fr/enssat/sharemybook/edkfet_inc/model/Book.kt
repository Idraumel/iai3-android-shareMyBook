package fr.enssat.sharemybook.edkfet_inc.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["owner_uuid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "owner_uuid", index = true)
    val ownerUuid: String,

    @ColumnInfo(name = "isbn")
    val isbn: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "authors")
    val authors: String,

    @ColumnInfo(name = "cover_url")
    val coverUrl: String?,

    @ColumnInfo(name = "state")
    val state: BookState = BookState.AVAILABLE
)

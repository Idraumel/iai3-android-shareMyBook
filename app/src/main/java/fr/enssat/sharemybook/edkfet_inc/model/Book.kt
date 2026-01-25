package fr.enssat.sharemybook.edkfet_inc.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val ownerUuid: String,
    val isbn: String,
    val title: String,
    val authors: String,
    val coverUrl: String? = null,
    val state: BookState = BookState.AVAILABLE,
    val borrowedByUuid: String? = null,
    val lentByUuid: String? = null
)

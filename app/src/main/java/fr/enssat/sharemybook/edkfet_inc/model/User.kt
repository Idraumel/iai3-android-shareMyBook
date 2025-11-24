package fr.enssat.sharemybook.edkfet_inc.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

// Add a unique index to the email column to prevent duplicates
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String?,

    // IMPORTANT: In a real-world application, this should be a securely hashed password, not plain text.
    @ColumnInfo(name = "password")
    val password: String
)

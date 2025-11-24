package fr.enssat.sharemybook.edkfet_inc.data.local

import androidx.room.TypeConverter
import fr.enssat.sharemybook.edkfet_inc.model.BookState
import fr.enssat.sharemybook.edkfet_inc.model.LoanAction
import fr.enssat.sharemybook.edkfet_inc.model.LoanStatus
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromBookState(value: BookState): String {
        return value.name
    }

    @TypeConverter
    fun toBookState(value: String): BookState {
        return BookState.valueOf(value)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }

    @TypeConverter
    fun fromLoanStatus(value: LoanStatus): String {
        return value.name
    }

    @TypeConverter
    fun toLoanStatus(value: String): LoanStatus {
        return LoanStatus.valueOf(value)
    }

    @TypeConverter
    fun fromLoanAction(value: LoanAction): String {
        return value.name
    }

    @TypeConverter
    fun toLoanAction(value: String): LoanAction {
        return LoanAction.valueOf(value)
    }
}

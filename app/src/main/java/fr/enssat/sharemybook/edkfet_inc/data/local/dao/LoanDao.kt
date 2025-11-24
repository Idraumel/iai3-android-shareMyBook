package fr.enssat.sharemybook.edkfet_inc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.enssat.sharemybook.edkfet_inc.model.Loan
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Insert
    suspend fun insert(loan: Loan)

    @Update
    suspend fun update(loan: Loan)

    /**
     * Gets all loans where the user is the owner.
     */
    @Query("SELECT * FROM loans WHERE ownerUuid = :ownerUuid ORDER BY updatedAt DESC")
    fun getLoansAsOwner(ownerUuid: String): Flow<List<Loan>>

    /**
     * Gets all loans where the user is the borrower.
     */
    @Query("SELECT * FROM loans WHERE borrowerUuid = :borrowerUuid ORDER BY updatedAt DESC")
    fun getLoansAsBorrower(borrowerUuid: String): Flow<List<Loan>>

    /**
     * Gets a specific loan by its ID.
     */
    @Query("SELECT * FROM loans WHERE id = :loanId")
    fun getLoanById(loanId: Long): Flow<Loan?>
}

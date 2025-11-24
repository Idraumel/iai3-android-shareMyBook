package fr.enssat.sharemybook.edkfet_inc.data.local.repository

import fr.enssat.sharemybook.edkfet_inc.data.local.dao.LoanDao
import fr.enssat.sharemybook.edkfet_inc.model.Loan
import kotlinx.coroutines.flow.Flow

class LoanRepository(private val loanDao: LoanDao) {

    suspend fun insert(loan: Loan) {
        loanDao.insert(loan)
    }

    suspend fun update(loan: Loan) {
        loanDao.update(loan)
    }

    fun getLoansAsOwner(ownerUuid: String): Flow<List<Loan>> {
        return loanDao.getLoansAsOwner(ownerUuid)
    }

    fun getLoansAsBorrower(borrowerUuid: String): Flow<List<Loan>> {
        return loanDao.getLoansAsBorrower(borrowerUuid)
    }

    fun getLoanById(loanId: Long): Flow<Loan?> {
        return loanDao.getLoanById(loanId)
    }
}

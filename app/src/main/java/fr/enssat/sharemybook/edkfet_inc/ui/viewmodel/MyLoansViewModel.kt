package fr.enssat.sharemybook.edkfet_inc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.enssat.sharemybook.edkfet_inc.data.auth.AuthManager
import fr.enssat.sharemybook.edkfet_inc.data.local.repository.LoanRepository
import fr.enssat.sharemybook.edkfet_inc.model.Loan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class MyLoansViewModel(
    private val loanRepository: LoanRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val userUuidFlow = authManager.loggedInUserUuid

    val loansAsOwner: StateFlow<List<Loan>> = userUuidFlow.flatMapLatest { uuid ->
        if (uuid != null) {
            loanRepository.getLoansAsOwner(uuid)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loansAsBorrower: StateFlow<List<Loan>> = userUuidFlow.flatMapLatest { uuid ->
        if (uuid != null) {
            loanRepository.getLoansAsBorrower(uuid)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
}

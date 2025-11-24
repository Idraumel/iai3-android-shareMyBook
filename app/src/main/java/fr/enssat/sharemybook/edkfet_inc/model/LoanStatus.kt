package fr.enssat.sharemybook.edkfet_inc.model

/**
 * Represents the status of a loan request.
 */
enum class LoanStatus {
    /**
     * The loan has been initiated by a borrower but not yet accepted by the owner.
     */
    INIT,

    /**
     * The loan has been accepted by the owner.
     */
    ACCEPTED,

    /**
     * The loan is completed (book returned).
     */
    COMPLETED,

    /**
     * The loan was canceled by the owner.
     */
    CANCELED
}

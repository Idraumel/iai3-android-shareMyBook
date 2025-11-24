package fr.enssat.sharemybook.edkfet_inc.model

/**
 * Represents the action being performed in a loan transaction.
 * This is to differentiate between the initial loan and the return of the book.
 */
enum class LoanAction {
    /**
     * The action of lending the book from the owner to the borrower.
     */
    LOAN,

    /**
     * The action of returning the book from the borrower to the owner.
     */
    RETURN
}

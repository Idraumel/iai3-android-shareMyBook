# Software Requirement Specifications

| unique ID | code | description | related specs IDs |
|---|---|---|---|
| REQ-F-LIB-001 | library_createFromISBN | The application must allow a user to create their library by scanning the ISBN of a book to retrieve its information. | REQ-F-LIB-002, REQ-F-LIB-004 |
| REQ-F-LIB-002 | library_fetchMetadataFromAPI | When an ISBN is scanned, the system must query an online database to retrieve title, authors and cover URL. | REQ-F-LIB-001, REQ-F-LIB-003 |
| REQ-F-LIB-003 | library_displayCover | If a cover URL is available, the application must download and display the book cover in the library. | REQ-F-LIB-002 |
| REQ-F-LIB-004 | library_manualEntryFallback | If the information is not available online, the application must provide a form to manually enter the book metadata. | REQ-F-LIB-001 |
| REQ-F-LIB-005 | library_localStorage | The application must store locally the user's book list including at minimum: book identifier, ISBN, title, authors, optional cover URL. | REQ-F-LIB-001, REQ-F-LOAN-011 |
| REQ-F-LIB-006 | library_listView | The user must be able to view their library in a list with at least title, authors, loan state and cover if available. | REQ-F-LIB-005 |
| REQ-F-USER-007 | user_generateUUID | On first launch, the application must generate and persist a user UUID. | REQ-F-USER-008, REQ-F-LOAN-010 |
| REQ-F-USER-008 | user_editPersonalData | The user must be able to enter and modify their full name, phone number and email. These data must be persisted locally. | REQ-F-UI-022, REQ-F-LOAN-010 |
| REQ-F-LIB-009 | dev_debugLocalDB | In development mode, the team must be able to locally inspect the application database. | |
| REQ-F-LOAN-010 | loan_transactionModel | A book exchange transaction involves two users identified by UUID and an exchange of contact information necessary for the loan. | REQ-F-USER-007, REQ-F-USER-008, REQ-F-LOAN-011, REQ-F-LOAN-012 |
| REQ-F-LOAN-011 | loan_markOwnerSide | When loaning, the book remains with the owner but is annotated with the borrower UUID and state loaned. | REQ-F-LIB-005, REQ-F-STATE-018 |
| REQ-F-LOAN-012 | loan_cloneBorrowerSide | When loaning, a book entry is created in the borrower library annotated with the owner UUID and state borrowed. | REQ-F-LIB-005, REQ-F-STATE-018 |
| REQ-F-LOAN-013 | loan_syncUsers | When a loan occurs, both users profiles must be added or updated in each other's local database. | REQ-F-USER-008, REQ-F-LOAN-010 |
| REQ-F-LOAN-014 | loan_returnOwnerSide | When returning, the system must remove the loan annotation on the owner side. | REQ-F-LOAN-011, REQ-F-STATE-018 |
| REQ-F-LOAN-015 | loan_returnBorrowerSide | When returning, the system must delete the book entry on the borrower side. | REQ-F-LOAN-012 |
| REQ-F-LOAN-016 | loan_cleanupUsers | When returning, if no relation remains, the corresponding user entries must be removed locally. | REQ-F-LOAN-013 |
| REQ-F-API-017 | api_transactionBackend | The system must rely on a minimal remote service to orchestrate a transaction with three actions: init, accept, result. | REQ-F-API-019, REQ-F-API-020, REQ-F-API-021, REQ-F-QR-023 |
| REQ-F-STATE-018 | book_states | The system must handle at minimum the following states for a book: available, loaned, borrowed. | REQ-F-LOAN-011, REQ-F-LOAN-012, REQ-F-LOAN-014, REQ-F-LOAN-015 |
| REQ-F-API-019 | api_init | The loan initiator must be able to create a transaction by sending action {LOAN|RETURN}, book metadata and user information. The service returns a transaction identifier shareId. | REQ-F-API-017, REQ-F-QR-023 |
| REQ-F-API-020 | api_accept | The borrower must be able to accept an existing transaction by providing the shareId and their user information. The response must contain consolidated information required for the loan. | REQ-F-API-017 |
| REQ-F-API-021 | api_resultPolling | The owner must be able to fetch consolidated transaction information by polling result periodically until completion with minimum 1 second between attempts. | REQ-F-API-017 |
| REQ-F-UI-022 | ui_profilesAndLists | The application must provide screens to list loaned books, list borrowed books and edit user profile information. | REQ-F-USER-008, REQ-F-STATE-018 |
| REQ-F-QR-023 | qr_displayShareId | The owner must be able to share the shareId as a QR code displayed on screen. | REQ-F-API-019, REQ-F-QR-024, REQ-F-QR-025 |
| REQ-F-QR-024 | qr_scanShareId | The application must allow scanning a QR code to retrieve a shareId to accept a transaction. | REQ-F-QR-023, REQ-F-API-020 |
| REQ-F-QR-025 | qr_contentFormat | The shared QR code must contain a minimal JSON object with the key shareId. | REQ-F-QR-023 |
| REQ-F-SCAN-026 | scan_unifiedISBNAndQR | A single scan activity must handle both ISBN barcodes and QR codes, exposing a standardized result to other screens. | REQ-F-LIB-001, REQ-F-QR-024 |
| REQ-NF-027 | nf_apiThrottling | The system must limit the frequency of calls to online sources to avoid blocking or abuse. | REQ-F-LIB-002 |
| REQ-NF-028 | nf_offlineResilience | The application must work locally for library management even without network access to the exchange service. | REQ-F-LIB-005, REQ-F-API-017 |
| REQ-NF-029 | nf_minimalPersonalData | The personal data exchanged in a transaction must be limited strictly to what is necessary for a loan: UUID, name, email, phone number. | REQ-F-LOAN-010, REQ-F-API-017 |
| REQ-NF-030 | nf_testability | Each functional requirement must be verifiable with a measurable acceptance test and explicit success criteria. | |



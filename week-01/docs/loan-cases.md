# Lending a book: POST /books/{id}/loans

| Case | Status | Also check | Test |
| --- | --- | --- | --- |
| The library lends a copy | 201 | the loan: id, bookId, memberEmail, loanedAt | `lendsACopy_returns201WithLoan` |
| No such book | 404 | detail: "No book with id nope" | `unknownBook_returns404ProblemDetail` |
| No copy free | 409 | detail, and the bookId | `noCopyFree_returns409` |
| Blank memberEmail | 400 | the library is never called | `blankEmail_returns400_andNeverCallsLibrary` |
| memberEmail is not an email, e.g. "ana" | 400 | the library is never called | `invalidEmail_returns400_andNeverCallsLibrary` |
| The book id comes from the path, not the body | 201 | | `bookIdComesFromPath_notFromBody` |
| No memberEmail in the body: {} | 400 | the library is never called | `missingEmail_returns400_andNeverCallsLibrary` |
| Broken JSON | 400 | the library is never called | `brokenJson_returns400_andNeverCallsLibrary` |
| Not JSON (Content-Type: text/plain) | 415 | the library is never called | `notJson_returns415_andNeverCallsLibrary` |

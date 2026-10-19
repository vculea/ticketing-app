package ro.frit.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private static final String ANA = "ana@mail.ro";
    private static final String DAN = "dan@mail.ro";

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library(List.of(
                book("dune", 3, 0),
                book("hobbit", 1, 0)
        ));
    }

    private static Book book(String id, int copies, int loaned) {
        return Book.builder()
                .id(id)
                .title(id)
                .author("author")
                .copies(copies)
                .loanedCopies(loaned)
                .build();
    }

    private int loanedCopies(String id) {
        return library.findBook(id).orElseThrow().getLoanedCopies();
    }

    @Test
    void borrowingAvailableBookIsLoaned() {
        LoanResult result = library.lend(new LoanRequest("dune", ANA));

        LoanResult.Loaned loaned = assertInstanceOf(LoanResult.Loaned.class, result);
        assertEquals("dune", loaned.loan().getBookId());
        assertEquals(ANA, loaned.loan().getMemberEmail());
        assertEquals(1, loanedCopies("dune"));
    }

    @Test
    void lastFreeCopyCanBeBorrowed() {
        LoanResult result = library.lend(new LoanRequest("hobbit", ANA));

        assertInstanceOf(LoanResult.Loaned.class, result);
        assertEquals(1, loanedCopies("hobbit"));
    }

    @Test
    void borrowingFullyLoanedBookIsNotAvailable() {
        library.lend(new LoanRequest("hobbit", ANA));

        LoanResult result = library.lend(new LoanRequest("hobbit", DAN));

        assertEquals(new LoanResult.NotAvailable("hobbit"), result);
        assertEquals(1, loanedCopies("hobbit"));
    }

    @Test
    void borrowingExactlyAllFreeCopiesSucceeds() {
        for (int i = 0; i < 3; i++) {
            LoanResult result = library.lend(new LoanRequest("dune", "member" + i + "@mail.ro"));
            assertInstanceOf(LoanResult.Loaned.class, result);
        }

        assertEquals(3, loanedCopies("dune"));
    }

    @Test
    void borrowingOneMoreThanFreeCopiesIsNotAvailable() {
        for (int i = 0; i < 3; i++) {
            library.lend(new LoanRequest("dune", "member" + i + "@mail.ro"));
        }

        LoanResult result = library.lend(new LoanRequest("dune", DAN));

        assertEquals(new LoanResult.NotAvailable("dune"), result);
        assertEquals(3, loanedCopies("dune"));
    }

    @Test
    void borrowingUnknownBookStoresNoLoan() {
        LoanResult result = library.lend(new LoanRequest("nope", ANA));

        assertEquals(new LoanResult.UnknownBook("nope"), result);
        assertTrue(library.loansPerMember().isEmpty());
    }

    @Test
    void loanRequestRejectsBlankBookId() {
        assertThrows(IllegalArgumentException.class, () -> new LoanRequest(" ", ANA));
    }

    @Test
    void loanRequestRejectsBlankMemberEmail() {
        assertThrows(IllegalArgumentException.class, () -> new LoanRequest("dune", " "));
    }

    @Test
    void loanRequestRejectsNullValues() {
        assertThrows(IllegalArgumentException.class, () -> new LoanRequest(null, ANA));
        assertThrows(IllegalArgumentException.class, () -> new LoanRequest("dune", null));
    }

    @Test
    void availableBooksExcludesFullyLoanedBooks() {
        library.lend(new LoanRequest("dune", ANA));
        library.lend(new LoanRequest("hobbit", ANA));

        List<Book> available = library.availableBooks();

        assertEquals(1, available.size());
        assertEquals("dune", available.getFirst().getId());
    }

    @Test
    void loansPerMemberCountsLoansForEachMember() {
        library.lend(new LoanRequest("dune", ANA));
        library.lend(new LoanRequest("hobbit", ANA));
        library.lend(new LoanRequest("dune", DAN));

        assertEquals(Map.of(ANA, 2L, DAN, 1L), library.loansPerMember());
    }

    @Test
    void loansPerMemberIsEmptyWithoutLoans() {
        assertTrue(library.loansPerMember().isEmpty());
    }

    @Test
    void findBookReturnsBookOrEmpty() {
        Optional<Book> dune = library.findBook("dune");

        assertTrue(dune.isPresent());
        assertEquals("dune", dune.get().getId());
        assertEquals(Optional.empty(), library.findBook("nope"));
    }

    @Test
    void describeNamesTheBook() {
        LoanResult loaned = library.lend(new LoanRequest("dune", ANA));
        LoanResult notAvailable = new LoanResult.NotAvailable("hobbit");
        LoanResult unknown = new LoanResult.UnknownBook("nope");

        assertTrue(loaned.describe().contains("dune"));
        assertTrue(notAvailable.describe().contains("hobbit"));
        assertTrue(unknown.describe().contains("nope"));
    }
}

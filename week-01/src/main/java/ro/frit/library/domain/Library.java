package ro.frit.library.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {
    private final List<Book> books;
    private final List<Loan> loans = new ArrayList<>();

    public Library(List<Book> books) {
        this.books = new ArrayList<>(books);
    }

    public LoanResult lend(LoanRequest request) {
        String bookId = request.bookId();
        Optional<Book> foundBook = findBook(bookId);
        if (foundBook.isEmpty()) {
            return new LoanResult.UnknownBook(bookId);
        }

        Book book = foundBook.get();
        if (book.getLoanedCopies() >= book.getCopies()) {
            return new LoanResult.NotAvailable(bookId);
        }

        Loan loan = new Loan(
                "loan_" + (loans.size() + 1),
                book.getId(),
                request.memberEmail(),
                String.valueOf(System.currentTimeMillis())
        );

        Book updatedBook = book.toBuilder()
                .loanedCopies(book.getLoanedCopies() + 1)
                .build();
        books.set(books.indexOf(book), updatedBook);

        loans.add(loan);
        return new LoanResult.Loaned(loan);
    }

    public List<Book> allBooks() {
        return List.copyOf(books);
    }

    public Optional<Book> findBook(String id) {
        return allBooks().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    public List<Book> availableBooks() {
        return allBooks().stream()
                .filter(book -> book.getLoanedCopies() < book.getCopies())
                .toList();
    }

    public Map<String, Long> loansPerMember() {
        return loans.stream()
                .collect(Collectors.groupingBy(
                        Loan::getMemberEmail,
                        Collectors.counting()
                ));
    }
}

package ro.frit.library.domain;

public sealed interface LoanResult permits LoanResult.Loaned, LoanResult.NotAvailable, LoanResult.UnknownBook {
    record Loaned(Loan loan) implements LoanResult {
    }

    record NotAvailable(String bookId) implements LoanResult {
    }

    record UnknownBook(String bookId) implements LoanResult {
    }

    default String describe() {
        return switch (this) {
            case Loaned l -> "Loaned: " + l.loan();
            case NotAvailable n -> "Not available: " + n.bookId();
            case UnknownBook u -> "Unknown book: " + u.bookId();
        };
    }
}

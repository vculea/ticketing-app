package ro.frit.library.domain;

public record LoanRequest(String bookId, String memberEmail) {
    public LoanRequest {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("bookId must not be null or blank");
        }
        if (memberEmail == null || memberEmail.isBlank()) {
            throw new IllegalArgumentException("memberEmail must not be null or blank");
        }
    }
}

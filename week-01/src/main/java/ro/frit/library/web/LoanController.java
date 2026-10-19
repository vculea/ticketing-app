package ro.frit.library.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ro.frit.library.domain.Library;
import ro.frit.library.domain.Loan;
import ro.frit.library.domain.LoanRequest;
import ro.frit.library.domain.LoanResult;

@RestController
@RequiredArgsConstructor
public class LoanController {
    private final Library library;

    public record LoanBody(@NotBlank @Email String memberEmail) {
    }

    public record LoanResponse(String id, String bookId, String memberEmail, String loanedAt) {
        static LoanResponse from(Loan loan) {
            return new LoanResponse(loan.getId(), loan.getBookId(), loan.getMemberEmail(), loan.getLoanedAt());
        }
    }

    @PostMapping("/books/{id}/loans")
    public ResponseEntity<LoanResponse> lend(@PathVariable String id, @Valid @RequestBody LoanBody body) {
        return switch (library.lend(new LoanRequest(id, body.memberEmail()))) {
            case LoanResult.Loaned l -> ResponseEntity.status(HttpStatus.CREATED).body(LoanResponse.from(l.loan()));
            case LoanResult.UnknownBook u ->
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id " + u.bookId());
            case LoanResult.NotAvailable n ->
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "No copy free for book id " + n.bookId());
        };
    }
}

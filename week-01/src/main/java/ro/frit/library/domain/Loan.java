package ro.frit.library.domain;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Loan {
    private String id;
    private String bookId;
    private String memberEmail;
    private String loanedAt;
}

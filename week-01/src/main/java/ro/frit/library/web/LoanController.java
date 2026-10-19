package ro.frit.library.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ro.frit.library.domain.Library;

@RestController
@RequiredArgsConstructor
public class LoanController {
    private final Library library;
}

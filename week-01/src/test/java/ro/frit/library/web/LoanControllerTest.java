package ro.frit.library.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.frit.library.domain.Library;
import ro.frit.library.domain.Loan;
import ro.frit.library.domain.LoanRequest;
import ro.frit.library.domain.LoanResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Library library;

    private static final String BODY = "{\"memberEmail\": \"ana@mail.ro\"}";

    @Test
    void lendsACopy_returns201WithLoan() throws Exception {
        var loan = new Loan("loan_1", "dune", "ana@mail.ro", "1760000000000");
        when(library.lend(new LoanRequest("dune", "ana@mail.ro")))
                .thenReturn(new LoanResult.Loaned(loan));

        mvc.perform(post("/books/dune/loans").contentType(APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("loan_1"))
                .andExpect(jsonPath("$.bookId").value("dune"))
                .andExpect(jsonPath("$.memberEmail").value("ana@mail.ro"))
                .andExpect(jsonPath("$.loanedAt").value("1760000000000"));
    }

    @Test
    void unknownBook_returns404ProblemDetail() throws Exception {
        when(library.lend(new LoanRequest("nope", "ana@mail.ro")))
                .thenReturn(new LoanResult.UnknownBook("nope"));

        mvc.perform(post("/books/nope/loans").contentType(APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("No book with id nope"));
    }

    @Test
    void noCopyFree_returns409() throws Exception {
        when(library.lend(new LoanRequest("dune", "ana@mail.ro")))
                .thenReturn(new LoanResult.NotAvailable("dune"));

        mvc.perform(post("/books/dune/loans").contentType(APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("No copy free for book id dune"));
    }

    @Test
    void blankEmail_returns400_andNeverCallsLibrary() throws Exception {
        mvc.perform(post("/books/dune/loans")
                        .contentType(APPLICATION_JSON)
                        .content("{\"memberEmail\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(library, never()).lend(any());
    }

    @Test
    void invalidEmail_returns400_andNeverCallsLibrary() throws Exception {
        mvc.perform(post("/books/dune/loans")
                        .contentType(APPLICATION_JSON)
                        .content("{\"memberEmail\": \"ana\"}"))
                .andExpect(status().isBadRequest());

        verify(library, never()).lend(any());
    }

    @Test
    void missingEmail_returns400_andNeverCallsLibrary() throws Exception {
        mvc.perform(post("/books/dune/loans")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(library, never()).lend(any());
    }

    @Test
    void brokenJson_returns400_andNeverCallsLibrary() throws Exception {
        mvc.perform(post("/books/dune/loans")
                        .contentType(APPLICATION_JSON)
                        .content("{\"memberEmail\": \""))
                .andExpect(status().isBadRequest());

        verify(library, never()).lend(any());
    }

    @Test
    void notJson_returns415_andNeverCallsLibrary() throws Exception {
        mvc.perform(post("/books/dune/loans")
                        .contentType(TEXT_PLAIN)
                        .content("ana@mail.ro"))
                .andExpect(status().isUnsupportedMediaType());

        verify(library, never()).lend(any());
    }

    @Test
    void bookIdComesFromPath_notFromBody() throws Exception {
        var loan = new Loan("loan_1", "hobbit", "ana@mail.ro", "1760000000000");
        when(library.lend(new LoanRequest("hobbit", "ana@mail.ro")))
                .thenReturn(new LoanResult.Loaned(loan));

        mvc.perform(post("/books/hobbit/loans")
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\": \"dune\", \"memberEmail\": \"ana@mail.ro\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value("hobbit"));
    }}

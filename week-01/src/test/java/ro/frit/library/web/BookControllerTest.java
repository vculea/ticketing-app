package ro.frit.library.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.frit.library.domain.Book;
import ro.frit.library.domain.Library;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Library library;

    private static final Book DUNE = Book.builder()
            .id("dune").title("Dune").author("Frank Herbert").copies(3).loanedCopies(1).build();
    private static final Book HOBBIT = Book.builder()
            .id("hobbit").title("The Hobbit").author("J.R.R. Tolkien").copies(1).build();

    @Test
    void listsEveryBookWithFreeCopies() throws Exception {
        when(library.allBooks()).thenReturn(List.of(DUNE, HOBBIT));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("dune"))
                .andExpect(jsonPath("$[0].freeCopies").value(2))
                .andExpect(jsonPath("$[1].id").value("hobbit"))
                .andExpect(jsonPath("$[1].freeCopies").value(1));
    }

    @Test
    void returnsOneBook() throws Exception {
        when(library.findBook("dune")).thenReturn(Optional.of(DUNE));

        mvc.perform(get("/books/dune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.freeCopies").value(2));
    }

    @Test
    void unknownBook_returns404ProblemDetail() throws Exception {
        when(library.findBook("nope")).thenReturn(Optional.empty());

        mvc.perform(get("/books/nope"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("No book with id nope"));
    }
}

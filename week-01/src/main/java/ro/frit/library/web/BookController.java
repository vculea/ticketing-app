package ro.frit.library.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ro.frit.library.domain.Book;
import ro.frit.library.domain.Library;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final Library library;

    public record BookResponse(String id, String title, String author, int copies, int freeCopies) {
        static BookResponse from(Book book) {
            return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(),
                    book.getCopies(), book.getCopies() - book.getLoanedCopies());
        }
    }

    @GetMapping("/books")
    public List<BookResponse> all() {
        return library.allBooks().stream().map(BookResponse::from).toList();
    }

    @GetMapping("/books/{id}")
    public BookResponse one(@PathVariable String id) {
        return library.findBook(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id " + id));
    }
}

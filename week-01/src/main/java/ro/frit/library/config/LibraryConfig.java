package ro.frit.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.frit.library.domain.Book;
import ro.frit.library.domain.Library;

@Configuration
public class LibraryConfig {

    @Bean
    public Library library(LibraryProperties properties) {
        return new Library(properties.seedBooks().stream()
                .map(seed -> Book.builder()
                        .id(seed.id())
                        .title(seed.title())
                        .author(seed.author())
                        .copies(seed.copies())
                        .build())
                .toList());
    }
}

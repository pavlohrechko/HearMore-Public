package nl.books.books.repository;

import nl.books.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitle (String title);
    // Custom queries can be added here if needed
}

package nl.books.books.service;

import nl.books.books.model.Book;
import nl.books.books.model.User;
import nl.books.books.model.dto.BookDTO;
import nl.books.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    // Save a book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get a book by ID
    public Optional<BookDTO> getBookById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            return Optional.empty();
        } else {
            BookDTO bookDTO = new BookDTO();
            Book book = bookOptional.get();
            bookDTO.setTitle(book.getTitle());
            bookDTO.setAuthor(book.getAuthor());
            bookDTO.setGenre(book.getGenre());
            bookDTO.setDescription(book.getDescription());
            bookDTO.setPublicationDate(book.getPublicationDate());
            bookDTO.setImageFilePath(book.getImageFilePath());
            bookDTO.setAudioFilePath(book.getAudioPreviewFilePath());
            return Optional.of(bookDTO);
        }
    }

    public Optional<BookDTO> getBookByIdWithSubscription(Long id, String jwt) throws Exception {
        // Fetch the book and user
        Optional<Book> bookOptional = bookRepository.findById(id);
        Optional<User> userOptional = userService.getUserFromJwt(jwt);

        if (bookOptional.isEmpty() || userOptional.isEmpty()) {
            return Optional.empty(); // Either book or user not found
        }

        Book book = bookOptional.get();
        User user = userOptional.get();

        // Check user subscriptions
        boolean hasAccess = user.getSubscriptions().stream()
                .anyMatch(sub -> sub.getType().equalsIgnoreCase("basic") || sub.getType().equalsIgnoreCase("ultimate"));

        // Map Book to BookDTO
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setGenre(book.getGenre());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setPublicationDate(book.getPublicationDate());
        bookDTO.setImageFilePath(book.getImageFilePath());

        // Include or exclude the audio file path based on access
        if (hasAccess) {
            bookDTO.setAudioFilePath(book.getAudioFilePath());
        } else {
            bookDTO.setAudioFilePath(null); // Or a placeholder value
        }

        return Optional.of(bookDTO);
    }



    // Delete a book by ID
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public Book getBookByTitle (String title) {
        return bookRepository.findByTitle(title);
    }
}

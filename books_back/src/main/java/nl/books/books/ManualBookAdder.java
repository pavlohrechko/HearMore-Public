package nl.books.books;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ManualBookAdder {

    private static final String BASE_URL = "http://localhost:8080/api/books";

    public static void main(String[] args) {
        // List of books to add
        List<Book> booksToAdd = Arrays.asList(
                new Book("1984", "George Orwell", "Dystopian", "A novel about totalitarianism and surveillance.", LocalDate.of(1949, 6, 8)),
                new Book("Moby Dick", "Herman Melville", "Adventure", "A thrilling tale of the quest for a white whale.", LocalDate.of(1851, 10, 18)),
                new Book("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "A critique of the American Dream in the 1920s.", LocalDate.of(1925, 4, 10)),
                new Book("The Catcher in the Rye", "J.D. Salinger", "Fiction", "A story of teenage rebellion and alienation.", LocalDate.of(1951, 7, 16)),
                new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy", "The prelude to The Lord of the Rings trilogy, a tale of adventure and courage.", LocalDate.of(1937, 9, 21)),
                new Book("Brave New World", "Aldous Huxley", "Dystopian", "A futuristic vision of a world driven by technology and hedonism.", LocalDate.of(1932, 8, 31)),
                new Book("The Alchemist", "Paulo Coelho", "Philosophical Fiction", "A story of self-discovery and pursuing one’s dreams.", LocalDate.of(1988, 1, 1))
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Add each book to the database
        for (Book book : booksToAdd) {
            try {
                HttpEntity<Book> request = new HttpEntity<>(book, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

                if (response.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println("Successfully added: " + book.getTitle());
                } else {
                    System.out.println("Failed to add: " + book.getTitle() + ". Status code: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("Error while adding: " + book.getTitle() + ". Message: " + e.getMessage());
            }
        }
    }

    static class Book {
        private String title;
        private String author;
        private String genre;
        private String description;
        private LocalDate publicationDate;

        public Book(String title, String author, String genre, String description, LocalDate publicationDate) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.description = description;
            this.publicationDate = publicationDate;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDate getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;
        }
    }
}

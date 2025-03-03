package nl.books.books.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioBookGenerationTextDTO {
    private String title;          // Book title
    private String author;         // Book author
    private String genre;          // Book genre
    private String description;    // Book description
    private LocalDate publicationDate; // Publication date (ensure correct format)
    private String text;           // Text to convert into audio
    private String voice;          // Voice model to use for audio
}


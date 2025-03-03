package nl.books.books.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDTO {

        private String title;
        private String author;
        private String genre;
        private String description;
        LocalDate publicationDate;
        private String audioFilePath;
        private String imageFilePath;
}

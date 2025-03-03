package nl.books.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private String description;
    private LocalDate publicationDate;

    @Column(length = 1000000)
    private String text;

    @Column(name = "audio_file_path")
    private String audioFilePath;

    @Column(name = "audio_preview_file_path")
    private String audioPreviewFilePath;

    @Column(name = "image_file_path")
    private String imageFilePath;

    @Column(name = "audio_content_type")
    private String audioContentType;
}

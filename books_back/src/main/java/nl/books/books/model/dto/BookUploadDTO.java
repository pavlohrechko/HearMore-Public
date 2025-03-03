package nl.books.books.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUploadDTO {

    private String title;
    private String author;
    private String genre;
    private String description;
    private LocalDate publicationDate;
    private String voice;
    private MultipartFile imageFile;
}

package nl.books.books.controller;

import nl.books.books.model.Book;
import nl.books.books.model.dto.BookDTO;
import nl.books.books.model.dto.BookUploadDTO;
import nl.books.books.service.AudioGenerationService;
import nl.books.books.service.BookService;
import nl.books.books.service.authentication.TokenValid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private AudioGenerationService audioGenerationService;

    @Autowired
    private BookService bookService;

    @GetMapping("/title/{title}")
    public ResponseEntity<Book> getBookByTitle(@PathVariable String title) {
        Book book = bookService.getBookByTitle(title);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Optional<BookDTO> book = bookService.getBookById(id);
        if (book.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(book.get());
        }
    }

    @TokenValid
    @GetMapping("/auth/{id}")
    public ResponseEntity<BookDTO> getBookByIdWithSubscription(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws Exception {
        Optional<BookDTO> book = bookService.getBookByIdWithSubscription(id, jwt);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound( ).build( ));
    }

    @PostMapping
    public ResponseEntity<Book> uploadBook(
            @RequestPart("metadata") BookUploadDTO bookUploadDTO,
            @RequestPart("file") MultipartFile file,
            @RequestPart("image") MultipartFile image) {

        try {
            // Define the base directory for uploaded files (absolute path)
            String baseDirectoryPath = "/Users/pashahrechko/Storage/education/Projects/books/books_back/";
            File baseDirectory = new File(baseDirectoryPath);

            // Ensure the base directory exists
            if (!baseDirectory.exists()) {
                boolean dirsCreated = baseDirectory.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Failed to create base directory: " + baseDirectoryPath);
                }
            }

            // Save the PDF file
            String pdfFilePath = saveFile(file, "pdf_files");

            // Save the image file
            String imageFilePath = saveFile(image, "images");

            // Parse the PDF and extract text
            String extractedText = extractTextFromPDF(new File(pdfFilePath));

            // Create and save the book with the extracted text and file paths
            Book book = audioGenerationService.createBookWithAudio(bookUploadDTO, extractedText, imageFilePath);

            return ResponseEntity.ok(book);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String saveFile(MultipartFile file, String folder) throws IOException {
        // Specify the directory to save the file
        Path directory = Paths.get("images", folder);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = directory.resolve(file.getOriginalFilename());
        file.transferTo(filePath);
        return filePath.toString();
    }

    private String extractTextFromPDF(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
}

package nl.books.books.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.books.books.model.Book;
import nl.books.books.model.dto.BookUploadDTO;
import nl.books.books.repository.BookRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class AudioGenerationService {

    private static final String API_URL = "https://api.openai.com/v1/audio/speech";
    private static final String API_KEY = ""; // Replace with your API key

    @Autowired
    private BookRepository bookRepository;

    // Directory to save audio files
    private static final String AUDIO_FILES_DIR = "audio_files";

    public Book createBookWithAudio(BookUploadDTO audioBookGenerationTextDTO, String text, String imageFilePath) {
        try {
            // Create a new Book entity from the DTO
            Book book = new Book();
            System.out.println("Creating book with audio");
            book.setTitle(audioBookGenerationTextDTO.getTitle());
            book.setAuthor(audioBookGenerationTextDTO.getAuthor());
            book.setImageFilePath(imageFilePath);
            book.setText(text);
            book.setGenre(audioBookGenerationTextDTO.getGenre());
            book.setDescription(audioBookGenerationTextDTO.getDescription());
            if (audioBookGenerationTextDTO.getPublicationDate() != null) {
                book.setPublicationDate(audioBookGenerationTextDTO.getPublicationDate());
            }

            System.out.println("Book created");

            // Generate audio for the provided text
            Map<String, String> filePaths = generateAndSaveAudio(
                    text,
                    audioBookGenerationTextDTO.getVoice(),
                    audioBookGenerationTextDTO.getTitle()
            );

            // Set file paths in the Book entity
            book.setAudioFilePath(filePaths.get("fullAudioFilePath"));
            book.setAudioPreviewFilePath(filePaths.get("audioPreviewFilePath"));
            book.setAudioContentType("audio/mpeg"); // Assuming the response is MP3

            // Save the book entity with audio file paths to the database
            return bookRepository.save(book);
        } catch (Exception e) {
            System.err.println("Error creating book with audio: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating book with audio", e);
        }
    }

    private Map<String, String> generateAndSaveAudio(String text, String voice, String bookTitle) {
        OkHttpClient client = new OkHttpClient();
        String jsonBody = constructJsonPayload(voice, text);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        System.out.println("Sending audio generation request with payload: " + jsonBody);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("Audio generation successful");
                return saveAudioFile(response.body().byteStream(), bookTitle);
            } else {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                throw new RuntimeException("Failed to generate audio: HTTP " + response.code() + " - " + response.message()
                        + " | Response Body: " + responseBody);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during audio generation", e);
        }
    }

    private String constructJsonPayload(String voice, String text) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("model", "tts-1");
            payload.put("voice", voice);
            payload.put("input", text);
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct JSON payload", e);
        }
    }

    private Map<String, String> saveAudioFile(InputStream audioStream, String bookTitle) throws IOException, InterruptedException {
        // Create a directory for the audio files if it doesn't exist
        Path audioDirPath = Paths.get(AUDIO_FILES_DIR, sanitizeFileName(bookTitle));
        Files.createDirectories(audioDirPath);

        // Create paths for the full and preview audio files
        Path fullAudioFilePath = audioDirPath.resolve("audio.mp3");
        Path previewAudioFilePath = audioDirPath.resolve("audio_preview.mp3");

        System.out.println("Saving full audio file to: " + fullAudioFilePath.toString());

        // Save the full audio file
        try (OutputStream outputStream = Files.newOutputStream(fullAudioFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Generate a 30-second preview using FFmpeg
        try {
            System.out.println("Generating 30-second preview for: " + fullAudioFilePath.toString());
            createAudioPreview(fullAudioFilePath.toString(), previewAudioFilePath.toString());
        } catch (Exception e) {
            System.err.println("Error generating audio preview: " + e.getMessage());
            throw e;
        }

        // Return both file paths
        Map<String, String> filePaths = new HashMap<>();
        filePaths.put("fullAudioFilePath", fullAudioFilePath.toString());
        filePaths.put("audioPreviewFilePath", previewAudioFilePath.toString());
        return filePaths;
    }

    /**
     * Generate a 30-second preview from the full audio file using FFmpeg.
     */
    private void createAudioPreview(String fullAudioPath, String previewAudioPath) throws IOException, InterruptedException {
        // FFmpeg command to trim the audio to 30 seconds
        String[] command = {
                "ffmpeg",
                "-i", fullAudioPath,           // Input file
                "-t", "30",                    // Duration (30 seconds)
                "-acodec", "copy",             // Keep original codec
                previewAudioPath               // Output file
        };

        // Execute the FFmpeg command
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)  // Redirect error output to standard output
                .start();

        // Capture FFmpeg output for debugging
        try (InputStream processInputStream = process.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = processInputStream.read(buffer)) != -1) {
                System.out.write(buffer, 0, bytesRead);
            }
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
        }

        System.out.println("Audio preview generated at: " + previewAudioPath);
    }

    private String sanitizeFileName(String fileName) {
        // Remove invalid characters from the file name
        return fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
}

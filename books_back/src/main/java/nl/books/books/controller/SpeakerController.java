package nl.books.books.controller;

import nl.books.books.model.Speaker;
import nl.books.books.service.SpeakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
    @RequestMapping("/api/speakers")
    public class SpeakerController {

        @Autowired
        private SpeakerService speakerService;

        @GetMapping
        public ResponseEntity<List<Speaker>> getAllSpeakers() {
            List<Speaker> speakers = speakerService.getAllSpeakers();
            return new ResponseEntity<>(speakers, HttpStatus.OK);
        }

        @PostMapping("/create")
        public ResponseEntity<Speaker> createSpeaker(
                @RequestParam String name,
                @RequestParam String description,
                @RequestParam("audio") MultipartFile audioFile,
                @RequestParam("image") MultipartFile imageFile) throws IOException {

            // Handle file storage (you could implement file storage logic here)
            String audioFilePath = saveFile(audioFile);
            String imageFilePath = saveFile(imageFile);

            // Create Speaker object
            Speaker speaker = new Speaker();
            speaker.setName(name);
            speaker.setDescription(description);
            speaker.setAudioFilePath(audioFilePath);
            speaker.setImageFilePath(imageFilePath);

            // Save the speaker using the service
            Speaker savedSpeaker = speakerService.saveSpeaker(speaker);

            return new ResponseEntity<>(savedSpeaker, HttpStatus.CREATED);
        }

        // Method to save files, this could be extended to save to a cloud service or directory
        private String saveFile(MultipartFile file) throws IOException {
            // This is just an example; you can implement file saving logic as needed
            String fileName = file.getOriginalFilename();
            String filePath = "uploads/" + fileName;

            // Save file locally or to a cloud storage
            file.transferTo(new java.io.File(filePath));

            return filePath;
        }
    }


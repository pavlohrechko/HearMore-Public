package nl.books.books.service;

import nl.books.books.model.Speaker;
import nl.books.books.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }

    public Speaker saveSpeaker(Speaker speaker) {
        return speakerRepository.save(speaker);
    }

    public void saveAllSpeakers(List<Speaker> speakers) {
        speakerRepository.saveAll(speakers);
    }

}

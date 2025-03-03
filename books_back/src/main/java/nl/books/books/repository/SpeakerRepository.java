package nl.books.books.repository;

import nl.books.books.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {
    // Custom queries can be added here if needed
}

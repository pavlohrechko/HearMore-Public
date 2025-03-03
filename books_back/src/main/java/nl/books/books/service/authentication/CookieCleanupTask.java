package nl.books.books.service.authentication;

import nl.books.books.repository.CookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CookieCleanupTask {

    @Autowired
    private CookieRepository cookieRepository;


    /**
     * Task to delete the expired cookies in 
     */
    @Scheduled(fixedRate = 3_600_000)
//    @Scheduled(fixedRate = 15_000)
    public void deleteExpiredCookies() {
        LocalDateTime now = LocalDateTime.now();
        cookieRepository.deleteByExpirationDateBefore(now);
    }
}

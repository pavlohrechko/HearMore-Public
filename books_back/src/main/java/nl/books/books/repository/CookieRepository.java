package nl.books.books.repository;

import jakarta.transaction.Transactional;
import nl.books.books.model.CookiesPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CookieRepository extends JpaRepository<CookiesPerson, Long> {

//    Optional<CookiesPerson>
    @Modifying
    @Transactional
    @Query("delete from CookiesPerson c where c.expirationDate < ?1")
    void deleteByExpirationDateBefore(LocalDateTime date);


}

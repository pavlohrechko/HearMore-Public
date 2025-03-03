package nl.books.books.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.books.books.model.Subscription;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullProfileDTO {

    private String username;
    private String email;
    private String imagePath;
    private Set<Subscription> subscriptions;

}

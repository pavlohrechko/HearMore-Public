package nl.books.books.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="Subscriptions")
public class Subscription {
    @Id
    private Long id;

    private String type;

    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> users = new HashSet<>();
}

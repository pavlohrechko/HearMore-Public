package nl.books.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String imagePath;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<CookiesPerson> cookiesPerson = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_junction",
            joinColumns = {@JoinColumn(name = "personId")},
            inverseJoinColumns = {@JoinColumn(name = "roleId")}
    )
    private Set<Role> authorities;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscription_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscription_id")}
    )
    private Set<Subscription> subscriptions = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        return List.of( );
    }

    @Override
    public boolean isAccountNonExpired () {
        return UserDetails.super.isAccountNonExpired( );
    }

    @Override
    public boolean isAccountNonLocked () {
        return UserDetails.super.isAccountNonLocked( );
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return UserDetails.super.isCredentialsNonExpired( );
    }

    @Override
    public boolean isEnabled () {
        return UserDetails.super.isEnabled( );
    }

    public void addCookie(CookiesPerson token) {
        cookiesPerson.add(token);
        token.setUser(this);
    }
}

package nl.books.books.service;

import nl.books.books.exceptions.TokenExcpetion;
import nl.books.books.model.User;
import nl.books.books.model.dto.FullProfileDTO;
import nl.books.books.model.dto.ProfileDTO;
import nl.books.books.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository userRepository;

    // Fetch all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Fetch a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Fetch a user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Create or update a user
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Check if a username or email exists
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public ProfileDTO getProfile(String token) {
        Optional<User> person = getUserFromJwt(token);
        if (person.isEmpty( )) {
            throw new TokenExcpetion("Invalid token");
        } else {
            return new ProfileDTO(
                    person.get( ).getUsername( )
            );
        }
    }

    public FullProfileDTO getFullProfile(String token) {
        Optional<User> personOptional = getUserFromJwt(token);
        if (personOptional.isEmpty()) {
            throw new TokenExcpetion("Invalid token");
        } else {
            User person = personOptional.get();
            return new FullProfileDTO(
                    person.getUsername( ) ,
                    person.getEmail( ) ,
                    person.getImagePath( ) ,
                    person.getSubscriptions( )
            );
        }
    }

    public Optional<User> getUserFromJwt(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            System.err.println("JWT missing or does not start with Bearer");
            return Optional.empty();
        }

        try {
            // Decode JWT
            String token = bearerToken.substring(7); // Remove "Bearer " prefix
            Optional<Jwt> jwtOptional = Optional.ofNullable(jwtDecoder.decode(token));

            if (jwtOptional == null) {
                System.err.println("Decoded JWT is null");
                return Optional.empty();
            }
            if (jwtOptional.isEmpty()) {
                System.err.println("Decoded JWT is empty");
                return Optional.empty();
            } else {
                Jwt jwt = jwtOptional.get( );
                // Fetch user by username (sub claim)
                String username = jwt.getClaims( ).get("sub").toString( );
                Optional<User> personByUsername = userRepository.findByUsername(username);

                // Fetch user by ID if jti claim represents the ID
                String idClaim = jwt.getId( );
                Optional<User> personById = Optional.empty( );
                if (idClaim != null) {
                    try {
                        Long userId = Long.valueOf(idClaim);
                        personById = userRepository.findById(userId);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid user ID in JWT: " + idClaim);
                    }
                }

                // Validate consistency
                if (personById.isPresent( ) && personByUsername.isPresent( ) && personById.get( ).equals(personByUsername.get( ))) {
                    return personById;
                }

                // Fallback to username if ID is unavailable or inconsistent
                return personByUsername;
            }

        } catch (JwtException e) {
            System.err.println("Error decoding JWT: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Unexpected error during JWT processing: " + e.getMessage());
            return Optional.empty();
        }
    }



}

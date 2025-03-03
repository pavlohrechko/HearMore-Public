package nl.books.books.service.authentication;

import nl.books.books.exceptions.TokenExcpetion;
import nl.books.books.model.CookiesPerson;
import nl.books.books.model.User;
import nl.books.books.repository.CookieRepository;
import nl.books.books.repository.UserRepository;
import nl.books.books.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CookieService {

    /**
     * Days for the token expiration from the moment of last login/registration.
     */
    public static int DAYS_TO_EXPIRE = 4;

    @Autowired
    private UserService personService;

    @Autowired
    private UserRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CookieRepository cookieRepository;

    /**
     * Generates the personalised token to authenticate user in the system and calls another
     * method to store the token in database.
     * Hashes in sha256 to shorten the token, then bcrypt hash is stored in the database.
     *
     * @param username username to generate cookies for.
     * @return token pure representation.
     * @throws NoSuchAlgorithmException throws if there's an error with SHA-256
     */
    @Transactional
    public String generateCookies(String username, String ipAddress) throws NoSuchAlgorithmException {
        int rounds = new Random().nextInt(5, 25);
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < rounds; i++) strBuilder.append(UUID.randomUUID());

        String sha256hex = shaBeforeBcrypt(strBuilder.toString());

        storePair(hash(sha256hex), username, ipAddress);
        return strBuilder.toString();
    }

    /**
     * Returns the hashed by BCrypt version of the token.
     *
     * @param token token to hash
     * @return hash of the token
     */
    private String hash(String token) {
        return passwordEncoder.encode(token);
    }

    /**
     * Hashes the input string using the SHA-256 hashing method. Is used to shorten the token before Bcrypt.
     *
     * @param input token to hash.
     * @return hashed representation of token.
     * @throws NoSuchAlgorithmException thrown if there's an error with SHA-256.
     */
    private static String shaBeforeBcrypt(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] sha256hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * sha256hash.length);
        for (byte b : sha256hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Stores a hashed token in the database for the given user.
     *
     * @param hash     hashed token.
     * @param username instance to find user.
     */
    @Transactional
    protected void storePair(String hash, String username, String ipAddress) {
        Optional<User> personOptional = personRepository.findByUsername(username);
        if (personOptional.isEmpty()) throw new TokenExcpetion("User not found.");
        User user = personOptional.get();
        deletePreviousSessions(user, ipAddress);

        CookiesPerson newSession = new CookiesPerson();
        newSession.setExpirationDate(LocalDateTime.now().plusDays(DAYS_TO_EXPIRE));
//        newSession.setExpirationDate(LocalDateTime.now().plusMinutes(1));
        newSession.setToken(hash);
        newSession.setIpAddress(ipAddress);

        user.addCookie(newSession);
        personRepository.save(user);
    }


    /**
     * Returns cookie instance if the received jwt and token are valid and hasn't expired, otherwise null.
     * @param jwt   jwt token received.
     * @param token session token received.
     * @return cookie record if everything is valid, null otherwise.
     * @throws NoSuchAlgorithmException throws exception if there's an error with SHA-256.
     */
    public CookiesPerson isValidSession(String jwt, String token) throws NoSuchAlgorithmException {
        if (jwt.isEmpty() || token.isEmpty()) return null;
        Optional<User> personOptional = personService.getUserFromJwt(jwt);
        if (personOptional.isEmpty()) {
            throw new TokenExcpetion("Invalid token.");
        } else {
            User person = personOptional.get();
            Set<CookiesPerson> sessions = person.getCookiesPerson( );
            String sha256hex = shaBeforeBcrypt(token);


            if (sessions.isEmpty( )) throw new TokenExcpetion("All session tokens has expired.");
            for (CookiesPerson cookie : sessions) {
                if (passwordEncoder.matches(sha256hex , cookie.getToken( ))) {
                    return cookie;
                }
            }
        }
        return null;
    }

    @Transactional
    protected String changeCookies(CookiesPerson cookiesPerson, String ipAddress) throws NoSuchAlgorithmException {
        LocalDateTime requiredRenewal = cookiesPerson.getExpirationDate().minusDays(DAYS_TO_EXPIRE-1);
        String username = cookiesPerson.getUser().getUsername();
        if (LocalDateTime.now().isAfter(requiredRenewal)) {
            cookiesPerson.getUser().getCookiesPerson().remove(cookiesPerson);
            cookieRepository.delete(cookiesPerson);
            return generateCookies(username, ipAddress);
        } else {
            if (!cookiesPerson.getIpAddress().equals(ipAddress)) {
                cookiesPerson.setIpAddress(ipAddress);
                cookieRepository.save(cookiesPerson);
            }
        }
        return null;
    }

    @Transactional
    protected void deletePreviousSessions(User person, String ipAddress) {
        Set<CookiesPerson> set = new HashSet<>(person.getCookiesPerson());
        for (CookiesPerson c : set) {
            if (c.getIpAddress().equals(ipAddress)) {
                person.getCookiesPerson().remove(c);
                cookieRepository.delete(c);
            }
        }
    }

}

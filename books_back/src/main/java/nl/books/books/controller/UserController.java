package nl.books.books.controller;

import nl.books.books.exceptions.TokenExcpetion;
import nl.books.books.exceptions.UserWrongCredentialsException;
import nl.books.books.service.UserService;
import nl.books.books.service.authentication.TokenValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @TokenValid
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(userService.getProfile(token));
        } catch (UserWrongCredentialsException | TokenExcpetion e) {
            return handleUserErrorException(e);
        }
    }

    @GetMapping("/full_profile")
    @TokenValid
    public ResponseEntity<?> getFullProfile(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(userService.getFullProfile(token));
        } catch (UserWrongCredentialsException | TokenExcpetion e) {
            return handleUserErrorException(e);
        }
    }

    @ExceptionHandler({UserWrongCredentialsException.class, TokenExcpetion.class})
    public ResponseEntity<Map<String, String>> handleUserErrorException(RuntimeException e) {
        Map<String, String> error = new HashMap<>(1);
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}

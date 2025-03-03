package nl.books.books.controller;

import jakarta.servlet.http.HttpServletRequest;
import nl.books.books.exceptions.TokenExcpetion;
import nl.books.books.exceptions.UserWrongCredentialsException;
import nl.books.books.model.dto.LoginDTO;
import nl.books.books.model.dto.RegistrationDTO;
import nl.books.books.service.UserService;
import nl.books.books.service.authentication.AuthenticationService;
import nl.books.books.service.authentication.TokenValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/auth")
//ToDo: add cross origin
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.registerUser(registrationDTO));
        } catch (UserWrongCredentialsException e) {
            return handleUserErrorException(e);
        }
    }

//    @PostMapping("/admin")
//    public ResponseEntity<?> registerAdmin(@RequestBody RegisterAdminDTO registerAdminDTO, HttpServletRequest request) {
//        try {
//            ResponseEntity<?> newAdmin = authenticationService.registerAdmin(registerAdminDTO, request.getRemoteAddr());
//            return ResponseEntity.ok(newAdmin);
//        } catch (UserWrongCredentialsException e) {
//            return handleUserErrorException(e);
//        }
//    }

    @GetMapping("/validate")
    @TokenValid
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String jwt) {
        return authenticationService.validateJwt(jwt);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO body, HttpServletRequest request) {
        try {
            return authenticationService.loginUser(body, request.getRemoteAddr());
        } catch (UserWrongCredentialsException | NoSuchAlgorithmException e) {
            return handleUserErrorException((RuntimeException) e);
        }
    }

    @GetMapping("/cookie/test")
    @TokenValid
    public ResponseEntity<?> testCookie(HttpServletRequest request) {
        try {
            String res = "Hello, Well authenticated!" + request.getRemoteAddr();
            return new ResponseEntity<>(res
                    , HttpStatus.OK);
        } catch (TokenExcpetion e) {
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

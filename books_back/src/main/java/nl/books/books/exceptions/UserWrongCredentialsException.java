package nl.books.books.exceptions;

public class UserWrongCredentialsException extends RuntimeException {
    public UserWrongCredentialsException(String message) {
        super(message);
    }
}


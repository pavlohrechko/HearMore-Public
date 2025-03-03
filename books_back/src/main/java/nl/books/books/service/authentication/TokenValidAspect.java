package nl.books.books.service.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nl.books.books.exceptions.TokenExcpetion;
import nl.books.books.model.CookiesPerson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class TokenValidAspect {

    @Autowired
    private CookieService cookieService;

    @Around("@annotation(TokenValid)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get request attributes
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new TokenExcpetion("Unable to retrieve request attributes. The request might not be in scope.");
        }

        HttpServletRequest request = attributes.getRequest();
        String jwtHeader = request.getHeader("Authorization");
        String cookieServiceHeader = request.getHeader("Authentication");
        String ipAddr = request.getRemoteAddr();

        // Validate headers
        if (jwtHeader == null || jwtHeader.isEmpty()) {
            throw new TokenExcpetion("Missing or empty Authorization (JWT) header.");
        }
        if (cookieServiceHeader == null || cookieServiceHeader.isEmpty()) {
            throw new TokenExcpetion("Missing or empty Authentication (cookie) header.");
        }

        // Validate session and cookies
        CookiesPerson cookiesPerson = cookieService.isValidSession(jwtHeader, cookieServiceHeader);
        if (cookiesPerson == null) {
            throw new TokenExcpetion("Invalid session: the provided token does not match.");
        }

        // Update cookies and proceed with the advised method
        String newCookie = cookieService.changeCookies(cookiesPerson, ipAddr);
        Object result = joinPoint.proceed();

        // Construct the response
        Map<String, Object> response = new HashMap<>();
        if (result instanceof ResponseEntity<?> responseEntity) {
            response.put("data", responseEntity.getBody());
        } else {
            response.put("data", result);
        }

        // Add new cookie information if available
        if (newCookie != null) {
            response.put("token-Update", newCookie);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

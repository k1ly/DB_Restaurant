package by.belstu.it.lyskov.dbrestaurant.controller.handler;

import by.belstu.it.lyskov.dbrestaurant.exception.*;
import io.jsonwebtoken.JwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class, ServiceException.class})
    public ResponseEntity<Object> handleDefaultException(Exception e, HttpStatus status, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("url", request.getRequestURL());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity<Object> handleAuthenticationException(Exception e, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("url", request.getRequestURL());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationException(Exception e, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("url", request.getRequestURL());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(Exception e, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", request.getRequestURL());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UsernameNotFoundException.class, UserNotFoundException.class,
            RoleNotFoundException.class, AddressNotFoundException.class,
            DishNotFoundException.class, CategoryNotFoundException.class, OrderNotFoundException.class,
            StatusNotFoundException.class, OrderItemNotFoundException.class, ReviewNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(Exception e, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", request.getRequestURL());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadReferenceException.class)
    public ResponseEntity<Object> handleUserNotFoundException(Exception e, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", request.getRequestURL());
        body.put("exception", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }
}

package application.tracker.service.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import application.tracker.service.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError>  handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError-> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .collect(Collectors.joining(", "));
        ApiError apiError = new ApiError(
            400,
            "VALIDATION_FAILED",
            errorMessage,
            LocalDateTime.now(),
            request.getRequestURI()
        );
          
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ApplicationNotFoundException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "NOT_FOUND", ex.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> illegalAccess(UnauthorizedAccessException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(401, "UNAUTHORIZED", ex.getMessage(), LocalDateTime.now(), path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericExceptions(Exception exception, HttpServletRequest request){
        log.error("Unexpected error", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body( new ApiError(403, "FORBIDDEN", exception.getMessage(), LocalDateTime.now(), request.getRequestURI()));
    }

    

}

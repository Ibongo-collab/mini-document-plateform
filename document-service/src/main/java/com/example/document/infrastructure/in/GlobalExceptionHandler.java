package com.example.document.infrastructure.in;

import com.example.document.domain.exception.DocumentNotFoundException;
import com.example.document.domain.exception.DocumentStorageException;
import com.example.document.domain.exception.DocumentValidationException;
import com.example.document.domain.exception.InvalidDocumentStatusTransitionException;
import com.example.document.infrastructure.in.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ApiError> handleDocumentNotFound(DocumentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DocumentValidationException.class)
    public ResponseEntity<ApiError> handleDocumentValidation(DocumentValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DocumentStorageException.class)
    public ResponseEntity<ApiError> handleDocumentStorage(DocumentStorageException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidDocumentStatusTransitionException.class)
    public ResponseEntity<ApiError> handleDocumentInvalidDocumentTransition(InvalidDocumentStatusTransitionException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Une erreur inattendue est survenue", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue");
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message) {
        ApiError error = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(error);
    }

}

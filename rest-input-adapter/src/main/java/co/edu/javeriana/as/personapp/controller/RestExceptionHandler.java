package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Traduce las excepciones del núcleo y de los adaptadores a códigos HTTP con un cuerpo JSON
 * uniforme: {@code {status, mensaje, timestamp}}.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NoExistException e) {
		return error(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler({ InvalidOptionException.class, IllegalArgumentException.class })
	public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
		return error(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		return error(HttpStatus.BAD_REQUEST, e.getName() + " must be a valid "
				+ (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "value") + ": " + e.getValue());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
		return error(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON");
	}

	@ExceptionHandler({ AlreadyExistsException.class, DataIntegrityViolationException.class })
	public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
		return error(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
		log.error("Unexpected error in REST API", e);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
	}

	private ResponseEntity<ErrorResponse> error(HttpStatus status, String mensaje) {
		log.warn("{} {}", status.value(), mensaje);
		return ResponseEntity.status(status).body(new ErrorResponse(status.value(), mensaje, LocalDateTime.now()));
	}
}

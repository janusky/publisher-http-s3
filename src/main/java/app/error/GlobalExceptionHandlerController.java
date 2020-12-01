package app.error;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import app.payload.StatusResponse;

@ControllerAdvice
public class GlobalExceptionHandlerController {
	@Autowired
	private ErrorHelper errorHelper;

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
		return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AppException.class)
	public ResponseEntity<?> handleAppException(AppException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception ex) throws Exception {
		String errorId = UUID.randomUUID().toString();
		String errorTicket = errorHelper.errorTicket(errorId, ex);
		StatusResponse errorResponse = StatusResponse.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(errorTicket).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

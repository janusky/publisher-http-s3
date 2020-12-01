package app.error;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * It groups together the exceptions handled, which must be related to some
 * message file key {@link MessageSource}.
 * </p>
 * <p>
 * NOTE: It should only be instantiated using the method
 * <code>AppException.of</code>.
 * </p>
 * <p>
 * Extends {@link RuntimeException} to not force the catch. Because it is not
 * necessary if it is caught in
 * {@link GlobalExceptionHandlerController(@ExceptionHandler(AppException.class)).
 * </p>
 * 
 * @author janusky@gmail.com
 * @version 1.0 - 28 nov. 2019
 *
 */
@Component
public class AppException extends RuntimeException {
	/** Default serial. */
	private static final long serialVersionUID = 1L;

	@Autowired
	private MessageSource messageSource;

	private String code;

	AppException() {
	}

	private AppException(String key, String message) {
		super(message);
		this.code = key;
	}

	public AppException of(String key, Object... args) /* throws NoSuchMessageException */ {
		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage(key, args, locale);
		return new AppException(key, message);
	}

	public String getCode() {
		return code;
	}
}
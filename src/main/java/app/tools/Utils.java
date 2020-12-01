package app.tools;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Utils {

	@Autowired
	private MessageSource messageSource;

	/**
	 * <p>
	 * Message finder in <code>MessageSource</code> (internalization).
	 * </p>
	 * 
	 * @param key String Key in properties file.
	 * @param args String[] Array with arguments in key value.
	 * @return String Value of found key with its arguments.
	 */
	public String getMessage(String key, Object... args) {
		String message = null;
		try {
			if (key != null) {
				Locale locale = LocaleContextHolder.getLocale();
				message = messageSource.getMessage(key, args, locale);
			}
		} catch (Exception e) {
			log.warn("find message {} Args: {} Error: {}", key, args, e.getMessage());
		}
		return message;
	}
}

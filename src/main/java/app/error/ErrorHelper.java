package app.error;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.tools.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorHelper {
	//final Logger log = org.slf4j.LoggerFactory.getLogger(ErrorHelper.class);

	@Autowired
	private Utils utils;

	public String errorTicket(Throwable ex) {
		return errorTicket(UUID.randomUUID().toString(), ex);
	}

	public String errorTicket(String errorIdentified, Throwable ex) {
		String key = "error.500";
		String message = utils.getMessage(key);
		return errorTicket(message, ex, errorIdentified);
	}

	public String errorTicket(String message, Throwable ex, String errorIdentified) {
		String errorResp = message;
		if (errorIdentified != null) {
			String errorId = utils.getMessage("error.code", errorIdentified);
			errorResp = message.concat("\n" + errorId);
		}
		log.error(errorResp, ex);
		return errorResp;
	}
}

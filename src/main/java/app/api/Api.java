package app.api;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import app.payload.AppInfoResponse;
import app.tools.ApplicationInfo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class Api {
	public static final String KEY_USER_SESION = "currentUser";

	@Autowired
	private ApplicationInfo applicationInfo;

	@GetMapping(value = { "/", "", "${app.route.api}" })
	public Mono<Map<String, String>> currentUser(Mono<Principal> principal) {
		log.debug("Current User..");
		return principal.map(Principal::getName).map(this::printValue);
	}

	@GetMapping(value = { "/info", "/status", "${app.route.api}/info", "${app.route.api}/status" })
	public ResponseEntity<?> info() {
		log.debug("Info..");
		//{ 
		//	"system": "SYSTEM-ID",
		//	"application": "APPLICATION-ID",
		//	"version": "APPLICATION-VERSION-ID",
		//	"server": "hostname o IP",
		//	“datetime”: “fecha formato ISO 8601”,
		//	"status": "[UP | DOWN]",
		//	"message": "API information to Application"
		//}
		AppInfoResponse report = applicationInfo.report();		
		return ResponseEntity.ok(report);
	}

	private Map<String, String> printValue(String value) {
		return Collections.singletonMap(KEY_USER_SESION, value);
	}
}

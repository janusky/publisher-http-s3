package app.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DbHealthCheck implements HealthIndicator {

	@Autowired
	Utils utilCommon;

	@Override
	public Health health() {
		int errorCode = check(); // perform some specific health check
		if (errorCode != 1) {
			String key = "error.code";
			String message = utilCommon.getMessage(key, "503");
			return Health.down().withDetail(message, "503").build();
		}
		return Health.up().build();
	}

	public int check() {
		// TODO Devolver el resultado según HazelcastConfiguration		
		return 1;
	}
}


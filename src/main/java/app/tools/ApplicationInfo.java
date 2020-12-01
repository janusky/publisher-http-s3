package app.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import app.payload.AppInfoResponse;
import app.payload.InfoDetail;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
// * Component that allows you to retrieve information from the application.
 * </p>
 * 
 * @see AppInfoResponse
 * 
 * @author janusky@gmail.com
 * @version 1.0 - 16 sep. 2019
 *
 */
@Component
@Slf4j
public class ApplicationInfo {
	@Autowired
	private DbHealthCheck dbHealthCheck;

	@Value("${info.app.system:#{buildProperties.group}}")
	private String system;

	@Value("${info.app.name:#{buildProperties.name}}")
	private String application;

	@Value("${info.app.version:#{buildProperties.version}}")
	private String version;

	@Value("${info.app.environment:development}")
	private String environment;

	public AppInfoResponse report() {
		List<InfoDetail> details = new ArrayList<>();
		details.add(dataBaseStatus());
		AppInfoResponse response = AppInfoResponse.builder().system(system).application(application).version(version)
				.environment(environment).details(details).build();
		log.debug(" Application status {}", response);
		return response;
	}

	private InfoDetail dataBaseStatus() {
		Health health = dbHealthCheck.health();

		InfoDetail appInfoDetail = InfoDetail.builder().name("DB Conection").status(health.getStatus().getCode())
				.details(health.getDetails()).build();
		return appInfoDetail;
	}
}

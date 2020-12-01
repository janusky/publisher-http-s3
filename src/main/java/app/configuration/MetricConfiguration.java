package app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

//@Configuration
public class MetricConfiguration {
	@Value("${spring.application.name:app}")
	private String applicationName;

	/**
	 * Register common tags application instead of job. This application tag is
	 * needed for Grafana dashboard.
	 *
	 * @return registry with registered tags.
	 */
	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> {
			registry.config().commonTags("application", applicationName);
			// can add more tags with .commonTags("cf.space.id", spaceId)
		};
	}
}

package app.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityReactive {	
	@Value("${app.route.actuator-uri:/actuator/**}")
	private String actuatorfUri;

	@Value("${app.route.path-public:/info,/status,/}")
	private List<String> appRoutePathPublic;
	
	/**
	 * <p>
	 * Indicates the list of certificate names enabled to consume the service.
	 * </p>
	 * <p>
	 * <strong>NOTE</strong>: If this list is empty it should be interpreted as
	 * allowing any valid certificate.
	 * </p>
	 */
	@Value("${app.certificates-enable}")
	private List<String> certificatesEnable;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    SubjectDnX509PrincipalExtractor principalExtractor =
	            new SubjectDnX509PrincipalExtractor();
		// Allows you to change the default: "CN=(.*?)(?:,|$)"
		// principalExtractor.setSubjectDnRegex("OU=(.*?)(?:,|$)");

	    // List of systems with enabled certificates
	    final List<String> certsEndable = getCertificatesEnable();

	    ReactiveAuthenticationManager authenticationManager = authentication -> {
	    	boolean authOk = false;
	    	if (certsEndable == null || certsEndable.isEmpty()) {
	    		authOk = true;
	    	} else if (authentication != null && authentication.getName() != null) {
	    		authOk = certsEndable.contains(authentication.getName());
	    	}
	    	authentication.setAuthenticated(authOk);
	    	return Mono.just(authentication);
	    };

	    String[] permitAll = appRoutePathPublic.toArray(new String[0]);
	    
		http
	        .x509()
	            .principalExtractor(principalExtractor)
	            .authenticationManager(authenticationManager)
	        .and()
	            .authorizeExchange()
	            .pathMatchers(permitAll).permitAll()
	            .pathMatchers(actuatorfUri).permitAll()
	            .anyExchange().authenticated()
	        .and().csrf().disable();

	    return http.build();
	}

	private List<String> getCertificatesEnable() {
		return certificatesEnable;
	}
}

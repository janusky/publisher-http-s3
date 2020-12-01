package app.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@EnableCaching
@ComponentScan(basePackages = { "*.s3" })
//@ConditionalOnClass(EnableWebFlux.class) //checks that WebFlux is on the classpath
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE) //checks that the app is a reactive web app
public class WebConfig /* implements WebFluxConfigurer */ {
}
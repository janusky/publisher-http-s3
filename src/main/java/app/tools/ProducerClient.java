package app.tools;

import java.net.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import app.model.Post;
import lombok.extern.slf4j.Slf4j;

/*
 * https://docs.spring.io/spring/docs/5.0.0.RELEASE/spring-framework-reference/web-reactive.html#webflux-client
 */
@Component
@Slf4j
public class ProducerClient {
	@Value("${app.route.producer:http://localhost:9000}")
	private String producerUrlPsoc;
	
	@Value("${app.proxy.enabled:false}")
	private Boolean proxyEnabled;

	@Autowired
	private ProxyHelper proxyHelper;

	private RestTemplate restTemplate;
	
	@Autowired
    public void setup() {
		log.info("producer config url={} proxy={}", producerUrlPsoc, proxyEnabled);
		RestTemplate restTemplate = new RestTemplate();
		if (proxyEnabled) {
			log.debug("set proxy restTemplate");
			SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
			Proxy proxy = proxyHelper.getProxy();
			clientHttpReq.setProxy(proxy);
			restTemplate.setRequestFactory(clientHttpReq);
		}
        this.restTemplate = restTemplate;
    }

	public HttpStatus post(Post data) {
		log.debug("post {}", data);
		HttpStatus status = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Post> requestEntity = new HttpEntity<Post>(data, headers);

			ResponseEntity<String> response = restTemplate.exchange(
					producerUrlPsoc, HttpMethod.POST, requestEntity,
					String.class);

			status = response.getStatusCode();
		} catch (HttpClientErrorException e) {
			log.error("post to producer", e);
			if (e.getRawStatusCode() == 401) {
				status = HttpStatus.UNAUTHORIZED;
			}
		}
		log.debug("response {}", status);
		return status;
	}
}
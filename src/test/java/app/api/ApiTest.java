package app.api;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import app.api.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiTest extends TestBaseApi {

	@Value("${app.route.api}")
	private String apiHome;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void callHomeApi() {
		// Invoke -> PublicApi.currentUser()
		String urlApi = "https://" + address + ":" + port + apiHome;
		log.debug("url=>{}", urlApi);
		Map<?, ?> response = restTemplate.getForObject(urlApi, Map.class);

		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.get(Api.KEY_USER_SESION));
		Assertions.assertEquals(response.get(Api.KEY_USER_SESION), CLIENT_CN);
	}

	
}

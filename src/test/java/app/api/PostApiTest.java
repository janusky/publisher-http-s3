package app.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import app.payload.PostResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostApiTest extends TestBaseApi {
	@Value("${app.route.api}/post")
	private String apiPost;

	/*
	 * 1-Run ceph (cd publisher-http-s3)
	 * docker-compose -f src/test/resources/docker/ceph.yml up -d
	 * docker exec ceph s3cmd ls	# Check bucket created
	 * 
	 * 2-Run test unit ..
	 * 
	 * 3-Get objec in bucket (docker exec ceph s3cmd ls s3://sandbox-bk)
	 * # Copy id (example: 20201127/cd010289-3fce-44e6-817c-d8f6fe176bc0)
	 * docker exec ceph s3cmd get s3://sandbox-bk/post/20201127/cd010289-3fce-44e6-817c-d8f6fe176bc0 cd010289-3fce-44e6-817c-d8f6fe176bc0
	 * docker cp ceph:/cd010289-3fce-44e6-817c-d8f6fe176bc0 ./file-one.pdf
	 * 
	 * ===========
	 * https://www.baeldung.com/spring-rest-template-multipart-upload
	 */
	@Test
	public void post() throws Exception {
		// Valid.sync()
		String urlApi = "https://" + address + ":" + port + apiPost;
		//urlApi = "https://local.localhost/api/valids/sync";
		log.debug("url=>{}", urlApi);
		
		String filesLocation = "classpath:files/";
		String fileOne = "file-one.pdf";
		String transaction = "2";

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("transaction", transaction);
		bodyMap.add("files", getFileResource(filesLocation + fileOne));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(bodyMap,
				headers);

		ResponseEntity<PostResponse> response = restTemplate.exchange(urlApi, HttpMethod.POST, requestEntity,
				PostResponse.class);

		Assertions.assertNotNull(response);
		Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		Assertions.assertNotNull(response.getBody().getTransaction());
	}
}

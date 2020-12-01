package app.tools;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import app.model.Attach;
import app.model.Post;
import app.tools.ProducerClient;

@RunWith(JUnitPlatform.class)
@ActiveProfiles("dev")
@SpringBootTest
class ProducerClientTest {

	@Autowired
	private ProducerClient producerClient;
	
	@Test
	void test001Post() {
		String idCustomer = "22222222222";
		String transaction = "1";
		Attach attach = Attach.builder()
				.filename("file1")
				.url("s3://uri_s3_file_file1")
				.size("1.2mb")
				.mimetype("zip")
				.build();
		Post data = Post.builder()
				.transaction(transaction)
				.idCustomer(idCustomer)
				.attach(attach)
				.build();
		HttpStatus resp = producerClient.post(data);
		assertNotNull(resp);
	}

}

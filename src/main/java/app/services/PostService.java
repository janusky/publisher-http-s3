package app.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import app.model.Attach;
import app.model.Post;
import app.s3.StoragePutResultDto;
import app.s3.StorageS3Service;
import app.tools.ProducerClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PostService {
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	@Value("${app.tmpPath:/tmp}")
	private String tmpPath;

	@Value("${app.s3.prefixKey:post}")
	private String prefixKey;

	@Autowired
	private ProducerClient producerClient;

	@Inject
	private StorageS3Service storageS3Service;

	public Mono<Post> put(InputStream file, String filename, String transactionCustomer) {
		log.debug("-> put file {} transaction:{}", filename, transactionCustomer);
		try {
			String transaction = UUID.randomUUID().toString();
			//TODO send to S3 (sync)
			String bucket = storageS3Service.getBucketCurrent();
			StringBuilder keyPut = new StringBuilder(prefixKey);
			keyPut.append("/");
			keyPut.append(DATE_FORMAT.format(new Date()));
			keyPut.append("/");
			keyPut.append(transaction);
			// Create file
			File fileTmp = File.createTempFile(tmpPath + "/" + keyPut, "");
			Files.copy(file, fileTmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			StoragePutResultDto putResultDto = storageS3Service.put(bucket , keyPut.toString(), fileTmp);

			//TODO send to Producer topic
			if (putResultDto != null) {
				return Mono.fromCallable(() -> {
					log.debug("send producer topic {}", keyPut);
					String keyPutUrl = "s3://".concat(putResultDto.getUrl());
					String size = fileTmp.length() + "kb";	// putResultDto.getContentLength().toString()
					String mimetype = "zip";	//putResultDto.getContentType()
					Attach attach = Attach.builder()
							.filename(filename)
							.url(keyPutUrl)
							.size(size)
							.mimetype(mimetype)
							.build();
					Post decla = Post.builder()
							.transaction(transaction)
							.transactionCustomer(transactionCustomer)
							.attach(attach)
							.build();
					HttpStatus resp = producerClient.post(decla);
					log.debug("response topic {}", resp);
					return decla;
				});
			}
			return Mono.error(new RuntimeException("The file was not saved"));
		} catch (IOException ex) {
			log.warn(ex.getMessage());
			return Mono.error(ex);
		}
	}
}

package app.s3;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.inject.Named;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Named
public class StorageS3ServiceImpl implements StorageS3Service {
	protected static Logger log = LoggerFactory.getLogger(StorageS3ServiceImpl.class);

	@Value("${storage.s3.enabled:true}")
	private Boolean s3Enabled;

	@Value("${storage.s3.user:''}")
	private String s3User;
	@Value("${storage.s3.signerType:S3SignerType}")
	private String s3SignerType;
	@Value("${storage.s3.endpoint}")
	private String s3Endpoint;
	@Value("${storage.s3.accessKey}")
	private String s3AccessKey;
	@Value("${storage.s3.secretKey}")
	private String s3SecretKey;

	@Value("${storage.s3.bucket}")
	private String s3Bucket;
	// TODO validar el sessionToken --> porque en el ejemplo lo pasan vacío.
	@Value("${storage.s3.sessionToken:''}")
	private String sessionToken;
	@Value("${storage.s3.region:us-east-1}")
	private String s3Region;

	@Value("${storage.s3.ssl.enabled:true}")
	private Boolean sslEnabled;
	@Value("${storage.s3.ssl.key-store:config/ssl/keyStore.jks}")
    private String keyStore;
	@Value("${storage.s3.ssl.key-store-password:changeit}")
	private String keyStorePassword;
	@Value("${storage.s3.ssl.trust-store:config/ssl/trustStore.jks}")
	private String trustStore;
	@Value("${storage.s3.ssl.trust-store-password:changeit}")
	private String trustStorePassword;

	private AmazonS3 s3Client;

	@Autowired
	public void setup() throws Exception {
		if (!s3Enabled) return;

		log.debug("-> setup S3 Client");
		ClientConfiguration clientConfig = new ClientConfiguration();
		if (!"".contentEquals(s3SignerType)) {
			log.debug("setSignerOverride {}", s3SignerType);
			clientConfig.setSignerOverride(s3SignerType);
		}
		if (sslEnabled) {
			log .debug("ssl enabled");
			SSLConnectionSocketFactory sslContext = loadSslContext();
			clientConfig.getApacheHttpClientConfig().setSslSocketFactory(sslContext);
		} else {
			clientConfig.setProtocol(Protocol.HTTP);
		}
		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(s3AccessKey, s3SecretKey,
				sessionToken);
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(sessionCredentials);
		String region = s3Region;
		if (!"".contentEquals(s3Region)) {
			// region = Region.getRegion(Regions.US_EAST_1).getName();
			region = Regions.DEFAULT_REGION.getName();
			log .debug("set region default {}", region);
		}
		log.info("s3 config url={} s3Bucket={} region={}", s3Endpoint, s3Bucket, region);
		AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
				s3Endpoint, region);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withClientConfiguration(clientConfig)
				.withEndpointConfiguration(endpointConfiguration)
				//.withForceGlobalBucketAccessEnabled(Boolean.TRUE)
				.build();
		this.s3Client = s3Client;
	}

	public StoragePutResultDto put(String bucket, String key, File file) {
		try {
			log.info("put file {} size:{}", key, file.length());
			PutObjectResult po = s3Client.putObject(new PutObjectRequest(bucket, key, file));
			return StoragePutResultDto.builder()
						.url(key)
						.md5(po.getContentMd5())
						.contentLength(po.getMetadata().getContentLength())
						.contentType(po.getMetadata().getContentType())
						.lastModified(po.getMetadata().getLastModified())
						.build();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("Error upload file ", e);
		}
	}

	@Override
	public File get(String tmpPath, String completebucket, String key) {
		try {
			log.debug("get file {} bucket:{}", key, completebucket);
			InputStream stream = s3Client.getObject(completebucket, key).getObjectContent();
			File tmp = File.createTempFile(tmpPath + "/" + key + System.currentTimeMillis(), "");
			Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return tmp;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("Error getting for -->  " + completebucket + " " + key, e);
		}
	}

	@Override
	public File get(String tmpPath, String completebucket, String key, String fileName) {
		try {
			log.debug("get file {} key:{} bucket:{}", fileName, key, completebucket);
			InputStream stream = s3Client.getObject(completebucket, key).getObjectContent();
//			String[] fileNameParsed = fileName.split("\\.");
			File tmp = new File(tmpPath + "/" + fileName);
			Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return tmp;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("Error al obtener archivo para  -->  " + completebucket + " " + key, e);
		}
	}

	@Override
	public void delete(String completebucket, String key) {
		try {
			s3Client.deleteObject(completebucket, key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("Error deleting file ->> " + key, e);
		}
	}
	
	public SSLConnectionSocketFactory loadSslContext() throws Exception {
		char[] keyStorePwd = keyStorePassword.toCharArray();
		char[] trustStorePwd = trustStorePassword.toCharArray();
		SSLContext sslContext = SSLContextBuilder
    			.create()
    			.loadKeyMaterial(ResourceUtils.getFile(keyStore), keyStorePwd, keyStorePwd)
                .loadTrustMaterial(ResourceUtils.getFile(trustStore), trustStorePwd)    			
    			.build();
		return new SSLConnectionSocketFactory(sslContext);        
    }
	
	public List<Bucket> listBuckets() {
		List<Bucket> buckets = s3Client.listBuckets();
		return buckets;
	}
	
	@Override
	public String getBucketCurrent() {
		return s3Bucket;
	}

	public Boolean getS3Enabled() {
		return s3Enabled;
	}

	public void setS3Enabled(Boolean s3Enabled) {
		this.s3Enabled = s3Enabled;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
}

package app.s3;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import app.s3.StoragePutResultDto;
import app.s3.StorageS3Configurarion;
import app.s3.StorageS3Service;
import app.s3.StorageS3ServiceImpl;

@RunWith(JUnitPlatform.class)
@SpringBootTest
@ContextConfiguration(classes = { StorageS3ConfigurarionTest.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
	"storage.s3.user=sandbox",
    "storage.s3.endpoint=172.240.49.25:9280",
    "storage.s3.region=eu-west-0",
    "storage.s3.accessKey=sandboxKey",
    "storage.s3.secretKey=sandboxSecret",
    "storage.s3.bucket=sandbox-bk",
    "storage.s3.enabled=true",
    "storage.s3.ssl.enabled=false",
})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageS3ServiceImplTest {
	@Inject
    ApplicationContext applicationContext;

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static String keyPut;
	private static String dateCurrent = DATE_FORMAT.format(new Date());

	@Autowired
    private StorageS3Service storageS3Service;

//    @Autowired
//    public void init() {
//    	storageS3Service = applicationContext.getBean(StorageS3Service.class);
//    }
    
	@Test
	public void test001List() throws Exception {
		java.util.List<com.amazonaws.services.s3.model.Bucket> buckets = ((StorageS3ServiceImpl) storageS3Service).listBuckets();
		assertNotNull(buckets);
		System.out.println(buckets);
	}

	@Test
	public void test002Put() throws Exception {
		String bucket = storageS3Service.getBucketCurrent();
		keyPut = "posoc-test/" + dateCurrent + "/" + UUID.randomUUID();
		StoragePutResultDto putDto = storageS3Service.put(bucket , keyPut , createSampleFile());
		assertNotNull(putDto);
	}

	@Test
	public void test003Get() throws Exception {
		String bucketName = storageS3Service.getBucketCurrent();
		String tmpPath = "./";
		File file = storageS3Service.get(tmpPath , bucketName, keyPut);
		assertNotNull(file);
		displayTextInputStream(new FileInputStream(file));
	}

	@Test
	public void test004Delete() {
		String bucketName = storageS3Service.getBucketCurrent();
		storageS3Service.delete(bucketName, keyPut);
	}


	private static File createSampleFile() throws IOException {
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.write("01234567890112345678901234\n");
		writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
		writer.write("01234567890112345678901234\n");
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.close();

		return file;
	}
	
	private static void displayTextInputStream(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;

			System.out.println("    " + line);
		}
		System.out.println();
	}
}

@Configuration
@ComponentScan(basePackages = { "*.s3" }, 
	excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StorageS3Configurarion.class) })
class StorageS3ConfigurarionTest {
}
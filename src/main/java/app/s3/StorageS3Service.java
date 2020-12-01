package app.s3;

import java.io.File;

public interface StorageS3Service {
	public StoragePutResultDto put(String bucket, String key, File file);

	public File get(String tmpPath, String completebucket, String key);

	public File get(String tmpPath, String completebucket, String key, String fileName);

	public void delete(String completebucket, String key);
	
	public String getBucketCurrent();
}

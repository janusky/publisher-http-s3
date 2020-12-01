package app.s3;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoragePutResultDto {
	private String url;
	private String md5;
	private Long contentLength;
	private String contentType;
	private String eTag;
	private Date lastModified;
}

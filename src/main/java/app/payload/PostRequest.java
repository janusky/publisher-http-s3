package app.payload;

import javax.validation.constraints.NotEmpty;

import org.springframework.http.codec.multipart.FilePart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
	/**
	 * Transaction identifier.
	 */
	@NotEmpty(message = "Not null")
	private String transacion;

	/**
	 * Files.
	 */
	private Flux<FilePart> files;
}

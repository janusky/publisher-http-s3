package app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import app.model.Post;
import app.payload.PostResponse;
import app.payload.StatusResponse;
import app.services.PostService;
import app.tools.Utils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${app.route.api}/post")
@Slf4j
public class PostApi {

	@Autowired
	private Utils utils;

	@Autowired
	private PostService declaracionService;

	@CrossOrigin
	@PostMapping(value = { "", "/" }, 
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<?> send(@RequestPart("files") Flux<FilePart> files,
			@RequestPart("transaction") final String transaction) {
		log.debug("-> send transaction {}", transaction);

		// TODO 2020/03/11 janusky@gmail.com - Use @Valid or Validate here

		PostResponse vfResponse = PostResponse.builder().build();

		return files.flatMap(it -> {
			log.debug("process file {}", it.filename());
			Mono<DataBuffer> join = DataBufferUtils.join(it.content());
			return join.flatMap(post -> {
				Mono<Post> processElement = null;

				processElement = declaracionService.put(post.asInputStream(), it.filename(), transaction)
						.doFinally(t -> DataBufferUtils.release(post));

				return processElement;
			}).flatMap(r -> {
				vfResponse.setTransaction(r.getTransaction());
				return Mono.just(vfResponse);
			});
		}).onErrorResume(ex -> {
			String errorMessage = utils.getMessage("error.post.send");
			log.error(errorMessage, ex);
			//TODO 20/10/2020 If is fail try by GlobalExceptionHandlerController
			StatusResponse status = StatusResponse.builder()
					.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
					.message(errorMessage)
					.build();
			vfResponse.setStatus(status);
			return Mono.just(vfResponse);
		}).then(Mono.just(vfResponse));
	}
}
# Demo

There is a class [test unit](#test-unit), emulating a client that makes the call to the service.

- POST Service: <${app.route.api}/post>

> NOTE: The value of `${app.route.api}` is found in the properties file (default is `/api/v1`).

In the tests carried out with the [Browser](#browser) the error of `CORS header 'Access-Control-Allow-Origin' missing` occurred. The solution was to incorporate `@CrossOrigin`, but you should consider making modifications of the [CORS](https://www.baeldung.com/spring-webflux-cors#global) configuration.

- <https://github.com/spring-projects/spring-framework/blob/master/src/docs/asciidoc/web/webflux-cors.adoc>
- <https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-cors>

If you make the change, you must modify the existing file

Configuration example **Cors** 

```java
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		WebFluxConfigurer.super.addCorsMappings(registry);
		registry.addMapping("/**")
			.allowedOrigins("*") // any host or put domain(s) here
			.allowedMethods("GET, POST") // put the http verbs you want allow
			.allowedHeaders("Authorization"); // put the http headers you want allow
	}
}
```

## Test Unit

There is the class [PostApiTest.java](src/test/java/app/api/PostApiTest.java) to emulate the operation of the `${app.route.api}/post` service.

This test class uses the file inside the path **src/test/resources/**

- Send file -> src/test/resources/files/file-one.pdf
- Auth ssl -> [ClientAuthenticationConfig.java](src/test/java/app/api/ClientAuthenticationConfig.java)


Right click 'Debug As -> Junit Test' en [PostApiTest.java](src/test/java/app/api/PostApiTest.java)

> NOTE: Test configuration with 'Test Runner: JUnit 5' and 'JRE 8'.

## Browser

You must have the client certificate [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) installed in the Browser use.

* Manage certificates -> Your certificates -> import

* Password `storepass` from [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx)

* chromium-browser src/test/resources/post.html
  * file data -> src/test/resources/files/file-one.pdf

The Browser should display the appropriate HTML to run the service (/api/v1/post).

HTML example [post.hml](src/test/resources/post.html)

> IMPORTANT: See that in the `action` attribute you must write the URL of the service. For example https://localhost:8443/api/v1/post or the local https://local.localhost:8443/api/v1/post post when running as a developer.

```html
<html>
<body>
	<!-- https://developer.mozilla.org/en-US/docs/Learn/Forms/Sending_forms_through_JavaScript -->
	<form id="theForm" action="https://local.localhost:8443/api/v1/post">
		<p>
			<label for="theText">text data:</label> <input id="theText"
				name="transaction" value="1" type="text">
		</p>
		<p>
			<label for="theFile">file data:</label> <input id="theFile"
				name="files" type="file" multiple>
		</p>
		<button>Send Me!</button>
	</form>
</body>

<script type="text/javascript">
	window.addEventListener('load', function() {
		//https://javascript.info/xmlhttprequest
		function sendData() {
			var form = document.getElementById('theForm'); // give the form an ID
			var xhr = new XMLHttpRequest(); // create XMLHttpRequest
			var data = new FormData(form); // create formData object

			xhr.onload = function() {
				console.log(this.responseText); // whatever the server returns
			}

			xhr.open("post", form.action); // open connection
			xhr.send(data);
		}

		const form = document.getElementById("theForm");
		form.addEventListener('submit', function(event) {
			event.preventDefault();
			sendData();
		});
	});
</script>

</html>
```

If POST https://local.localhost:8443/api/v1/post net::ERR_CERT_AUTHORITY_INVALID

Solve

1. Run in browser https://local.localhost:8443/api/v1/
2. Accept SSL
3. Use client.localhost.pfx (org-TEST -> client.localhost)

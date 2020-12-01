# Demo

Se cuenta con clase [test unit](#test-unit), emulando un cliente que realiza la llamada al servicio.

- Servicio POST: <${app.route.api}/post>

> NOTA: El valor de `${app.route.api}` se encuentra en el archivo de propiedades (por defecto es `/api/v1`).

En las pruebas realizadas con el [Browser](#browser) ocurrió el error de `CORS header 'Access-Control-Allow-Origin' missing`. La solución fue incorporar `@CrossOrigin`, pero debe evaluar hacer modificaciones en la configuración de [CORS](https://www.baeldung.com/spring-webflux-cors#global).

- <https://github.com/spring-projects/spring-framework/blob/master/src/docs/asciidoc/web/webflux-cors.adoc>
- <https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-cors>

De realizar el cambio, debe modificar el archivo existente `app.configuration.SecurityReactive`.
  
Ejemplo de configuración **Cors**

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

Existe la clase [PostApiTest.java](src/test/java/app/api/PostApiTest.java) para emular el funcionamiento del servicio `${app.route.api}/post`.

Dicha clase de test utiliza los archivos dentro del path **src/test/resources/**

- Send file -> src/test/resources/files/file-one.pdf
- Auth ssl -> [ClientAuthenticationConfig.java](src/test/java/app/api/ClientAuthenticationConfig.java)


Click derecho 'Debug As -> Junit Test' en [PostApiTest.java](src/test/java/app/api/PostApiTest.java)

> NOTA: Configuración del test con 'Test Runner: JUnit 5' y 'JRE 8'.

## Browser

Debe contar con el certificado cliente [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) instalado en el Browser/Navegador utilizado.

* Administrar certificados -> Tus certificados -> importar

* Password `storepass` de [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx)

* chromium-browser src/test/resources/post.html
  * file data -> src/test/resources/files/file-one.pdf

El navegador debería mostrar el HTML apropiado para ejecutar el servicio (/api/v1/post).

HTML ejemplo [post.hml](src/test/resources/post.html)

> IMPORTANTE: Notar que en el atributo `action` se debe escribir la URL del servicio. Por ejemplo https://localhost:8443/api/v1/post o bien la local https://local.localhost:8443/api/v1/post cuando se ejecuta como desarrollador.

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

# publisher-http-s3

Spring WebFlux application to publish file and metadata, interacting with S3 and HTTP Services.

## Technologies

- Java 8|11
- [Spring Boot Reactive](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) ([WebFlux](https://www.baeldung.com/spring-webflux))
- [Lombok](https://projectlombok.org)
- [Autenticación SSL](DEVELOPMENT.md#ssl)
- [Maven](https://maven.apache.org/)
- ~~OpenAPI 3~~

## Use

Download the desired version (for example: `master`)

>IMPORTANT - Have the appropriated configuration (maven > 3 y java 8)
>```
>$ mvn -version
>Apache Maven 3.3.9
>Maven home: /home/manuel/DEV/maven
>Java version: 1.8.0_212, vendor: Azul Systems, Inc.
>Java home: /home/manuel/.sdkman/candidates/java/8.0.212-zulu/jre
>```

Execute [publisher-http-s3](README.md) from a terminal or by configuring the IDE

```sh
# Download project
git clone https://github.com/janusky/publisher-http-s3.git

# Run in terminal
cd publisher-http-s3
mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests

# Since the Eclipse in VM Arguments
# Right click 'Debug As -> Java Application' in Application.java 
-Dspring.profiles.active=dev
-Duser.timezone=-03:00
```

You must have the client certificate [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) installed in the Browser use.

* Manage certificates -> Your certificates -> import

* Password `storepass` from [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx)

To access: <https://local.localhost:8443> o <https://localhost:8443/info>

More info: [Run publisher-http-s3](DEVELOPMENT.md#Run)

If problem arise: [possible-drawbacks](DEVELOPMENT.md#possible-drawbacks)

## Documentos

1. [Demo - Test service](DEMO.md)
1. [Development guide](DEVELOPMENT.md)
1. [Installation instruction](INSTALL.md)
1. [Example installation](INSTALL-EXAMPLE.md)

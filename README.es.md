# publisher-http-s3

Aplicación WebFlux para publicar archivo y metadata, interactuando con S3 y Servicios HTTP.

## Tecnologías

- Java 8|11
- [Spring Boot Reactive](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) ([WebFlux](https://www.baeldung.com/spring-webflux))
- [Lombok](https://projectlombok.org)
- [Autenticación SSL](DEVELOPMENT.es.md#ssl)
- [Maven](https://maven.apache.org/)
- ~~OpenAPI 3~~

## Uso

Descargar la versión deseada (por ejemplo: `master`)

>IMPORTANTE - Contar con la configuración apropiada (maven > 3 y java 8)
>```
>$ mvn -version
>Apache Maven 3.3.9
>Maven home: /home/manuel/DEV/maven
>Java version: 1.8.0_212, vendor: Azul Systems, Inc.
>Java home: /home/manuel/.sdkman/candidates/java/8.0.212-zulu/jre
>```

Ejecutar [publisher-http-s3](README.es.md) desde una terminal o configurando el IDE

```sh
# Descargar proyecto
git clone https://github.com/janusky/publisher-http-s3.git

# Desde terminal
cd publisher-http-s3
mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests

# Desde el eclipse en VM Arguments
# Click derecho 'Debug As -> Java Application' en Application.java 
-Dspring.profiles.active=dev
-Duser.timezone=-03:00
```

Debe contar con el certificado cliente [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) instalado en el Browser/Navegador utilizado.

* Administrar certificados -> Tus certificados -> importar

* Password `storepass` de [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx)

Acceder: <https://local.localhost:8443/> o <https://localhost:8443/info>

Más info: [Ejecutar publisher-http-s3](DEVELOPMENT.es.md#ejecutar)

si surgen inconvenientes: [Ver](DEVELOPMENT.es.md#inconvenientes-posibles)

## Documentos

1. [Demo - Probar servicio](DEMO.es.md)
1. [Guía de desarrollo](DEVELOPMENT.es.md)
1. [Instructivo de instalación](INSTALL.es.md)
1. [Instalación ejemplo](INSTALL-EXAMPLE.es.md)

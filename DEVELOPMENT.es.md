# Desarrollo

Se utilizó programación reactiva con [Spring Boot WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) y [Reactor Netty](https://projectreactor.io/docs/netty/snapshot/reference/index.html).

- [Spring Web Reactive](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
- [Reactor Netty](https://projectreactor.io/docs/netty/snapshot/reference/index.html)

![](https://rawcdn.githack.com/hantsy/spring-reactive-sample/9cb8538f8edc836e5ffee9e47a0814a549e6594e/webflux.png)
![](https://docs.spring.io/spring/docs/current/spring-framework-reference/images/spring-mvc-and-webflux-venn.png)

## Configuración de entorno

Las configuraciones están sujetas al entorno del desarrollador

- [Eclipse IDE](https://www.eclipse.org/downloads/)

> NOTA: El IDE debe permitir trabajar con Java 8/11. En caso de no cambiar la versión actual del proyecto.

- [Java 8/11 o >](https://adoptopenjdk.net/)

	- [openjdk-11.0.2_windows-x64_bin.zip](https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip)
	- <https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot>
	- <https://jdk.java.net/11/>

- [Maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html). Se puede utilizar el existente en Eclipse IDE.

- [Spring Tool Suite](https://spring.io/tools/sts/all). En mi caso (janusky@gmail.com) lo instalé desde Marketplace de eclipse.

- [Lombok](https://projectlombok.org)

	- <https://projectlombok.org/setup/eclipse> ([lombok setup](#lombok))
	- <https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/>

## Ejecutar

Ejecutar [publisher-http-s3](README.es.md) desde una terminal o configurando el IDE

```sh
# Desde terminal
cd publisher-http-s3
mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests

# Desde el eclipse en VM Arguments
# Click derecho 'Debug As -> Java Application' en Application.java 
-Dspring.profiles.active=dev
-Duser.timezone=-03:00
```

## SSL

La aplicación cuenta con verificación de certificado cliente `client.localhost`. También tiene configurado un certificado `*.localhost` por el cual puede ser reconocido mediante la entidad autorizante de confianza apropiada `CA Local`.

En desarrollo se utilizan los archivos

* TrustStore [trust-store.pfx](config/ssl/trust-store.pfx) - Entidad de confianza del archivo cliente **client.localhost**.

* KeyStore [key-store.pfx](config/ssl/key-store.pfx) - Contiene el certificado presentado por la aplicación.

* Client [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) (password `storepass`) - Certificado cliente firmado por `trust-store.pfx`, utilizado para consumir los servicios provistos por la aplicación **publisher-http-s3**

### Creating the Certificate Authority

Creación de **Trust Store**

```sh
# Crear la clave de CA (password: storepass)
openssl genrsa -des3 -out localhost.key 4096

# Crear certificado CA
openssl req -new -x509 -days 7300 -key localhost.key -out localhost.crt \
  -subj "/C=AR/ST=CABA/L=CABA/O=TEST/OU=TEST/CN=*.localhost"

# Exportar CA como PKCS #12 (password: storepass)
openssl pkcs12 -export -out trust-store.pfx -inkey localhost.key -in localhost.crt
```

Certificado cliente **client.localhost**

```sh
# Crear certificado Cliente (password: storepass)
openssl genrsa -des3 -out client.localhost.key 4096

# Then, create a Certificate Signing Request (CSR)
openssl req -new -key client.localhost.key -out client.localhost.csr \
  -subj "/C=AR/ST=CABA/L=CABA/O=TEST/OU=TEST/CN=client.localhost"

# Firmar el certificado cliente con nuestra CA (Certificate Authority)
openssl x509 -req -days 3065 -in client.localhost.csr -CA localhost.crt -CAkey localhost.key -set_serial 01 -out client.localhost.crt

# Crear PKCS #12 (PFX) del Cliente (password: storepass)
openssl pkcs12 -export -out client.localhost.pfx -inkey client.localhost.key -in client.localhost.crt -certfile localhost.crt
```

Certificado presentado por la aplicación **publisher-http-s3**

```sh
# Generate a certificate request starting from an existing certificate
openssl x509 -x509toreq -in localhost.crt -out local.localhost.csr -signkey localhost.key

# Create a self-signed certificate
openssl x509 -signkey localhost.key -in local.localhost.csr -req -days 3650 -out local.localhost.crt

# Creating a PKCS #12 (PFX)
openssl pkcs12 -export -out key-store.pfx -inkey localhost.key -in local.localhost.crt
```

## Crear entregable (release)

Configurar usuario del repositorio **Nexus** en el archivo `~/.m2/settings.xml`.

- <https://maven.apache.org/guides/mini/guide-encryption.html>

### Hacer release con maven-release-plugin

Debe contar con la clave para el repositorio indicado en tag `<distributionManagement></distributionManagement>`

```sh
# NOTA: Si no se crea el branch release, los cambios se realizan en master.
# 1 - Crear el branch release
git checkout -b release/1.0.0

# 2 - Ejecuta los cambios en el branch y crea el tag
mvn -Darguments="-DskipTests" release:clean release:prepare release:perform
```

## Inconvenientes posibles

Problemas que se pueden presentar en configuración/desarrollo.

### Lombok

Los fuentes `.java` no compilan cuando utiliza `lombok`.

1 Download Lombok Jar File

- <https://projectlombok.org/downloads/lombok.jar>

2 Start Lombok Installation

```sh
java -jar lombok.jar
```

3 Give Lombok Install Path

Now click on the "Specify Location" button and locate the eclipse.exe path under eclipse installation folder like this.

### Could not resolve dependencies

Según la configuración del archivo `${user.home}/.m2/settings.xml` puede no resolver algunas dependencias.

**[ERROR]** Failed to execute goal on project publisher-http-s3: Could not resolve dependencies for project janusky:publisher-http-s3:jar:0.0.1-SNAPSHOT: Could not find artifact app.janusky:validate-certificate:jar:0.0.7 in Enterprise (https://nexus.server/nexus/repository/public) -> [Help 1]

**Solve**: Agregar los repositorios indicados

* https://nexus.server/nexus/repository/public
* https://nexus.server/nexus/repository/external/
* https://nexus.server/nexus/repository/sandbox-maven/

También se copia ejemplo de `settings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<settings
    xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository>${user.home}/.m2/repository</localRepository>
    <proxies>
        <proxy>
            <id>proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>my.proxy</host>
            <port>80</port>
            <nonProxyHosts>localhost|*.localhost</nonProxyHosts>
        </proxy>
        <proxy>
            <id>proxy_https</id>
            <active>true</active>
            <protocol>https</protocol>
            <host>my.proxy.https</host>
            <port>80</port>
            <nonProxyHosts>localhost|*.localhost</nonProxyHosts>
        </proxy>
    </proxies>
    <servers>
        <!--Maven Password Encryption
            https://maven.apache.org/guides/mini/guide-encryption.html
        -->
        <server>
            <id>nexus.prod</id>
            <username>myuser</username>
            <password>{pws}</password>
        </server>
        <server>
            <id>nexus.test</id>
            <username>myuser</username>
            <password>{pws}</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>nexus</id>
            <repositories>
                <repository>
                    <id>public</id>
                    <url>https://nexus.server/nexus/repository/public</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>external</id>
                    <url>https://nexus.server/nexus/repository/external/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>sandbox</id>
                    <name>sandbox</name>
                    <url>https://nexus.server/nexus/repository/sandbox-maven/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>repo_public</id>
                    <name>REPO Plugin Repository</name>
                    <url>https://nexus.server/nexus/repository/public</url>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <releases>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>nexus</activeProfile>
    </activeProfiles>
</settings>
```

## Referencias

**spring-boot-reactive**

* <https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html>
* <https://www.baeldung.com/spring-webflux>
* <https://www.baeldung.com/spring-session-reactive>
* <https://www.baeldung.com/spring-5-webclient>

**reactor-netty**

* <https://www.baeldung.com/spring-boot-reactor-netty>
* <https://projectreactor.io/docs/netty/snapshot/reference/index.html>

**Logging**

* <https://www.baeldung.com/java-logging-intro>

**jackson**

* <https://www.baeldung.com/jackson-vs-gson>
* <https://interviewbubble.com/performance-comparison-of-json-libraries-jackson-vs-gson-vs-fastjson-vs-json-simple-vs-jsonp/>
* <https://blog.overops.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/>

**errors**

* <https://www.baeldung.com/spring-webflux-errors>
* <https://medium.com/@akhil.bojedla/exception-handling-spring-webflux-b11647d8608>

**SSL**

* <https://www.baeldung.com/java-keystore-truststore-difference>
* <https://blog.guillen.io/2016/10/21/jks-java-keystore/>

**file upload**

* <https://www.baeldung.com/spring-file-upload>

# Desarrollo

Reactive programming was used with [Spring Boot WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) and [Reactor Netty](https://projectreactor.io/docs/netty/snapshot/reference/index.html).

- [Spring Web Reactive](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
- [Reactor Netty](https://projectreactor.io/docs/netty/snapshot/reference/index.html)

![](https://rawcdn.githack.com/hantsy/spring-reactive-sample/9cb8538f8edc836e5ffee9e47a0814a549e6594e/webflux.png)
![](https://docs.spring.io/spring/docs/current/spring-framework-reference/images/spring-mvc-and-webflux-venn.png)

## Environment settings

Settings are subject to developer environment

- [Eclipse IDE](https://www.eclipse.org/downloads/)

>NOTE: The IDE must allow working with Java 8/11. In case of not changing the current version of the project.

- [Java 8/11 o >](https://adoptopenjdk.net/)

	- [openjdk-11.0.2_windows-x64_bin.zip](https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip)
	- <https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot>
	- <https://jdk.java.net/11/>

- [Maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html). You can use the existing one in Eclipse IDE.

- [Spring Tool Suite](https://spring.io/tools/sts/all). In may case (janusky@gmail.com) I installed from eclipse Marketplace.

- [Lombok](https://projectlombok.org)

	- <https://projectlombok.org/setup/eclipse> ([lombok setup](#lombok))
	- <https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/>

## Run

Execute [publisher-http-s3](README.md) from a terminal or by configuring the IDE

```sh
# Terminal
cd publisher-http-s3
mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests

# Since the Eclipse in VM Arguments
# Right click 'Debug As -> Java Application' in Application.java 
-Dspring.profiles.active=dev
-Duser.timezone=-03:00
```

## SSL

The application has verification of client certificate `client.localhost`. It also has a `* .localhost` certificate configured by which it can be recognized by the appropriate trusted authorizing authority `Local CA`.

In development files are used

* TrustStore [trust-store.pfx](config/ssl/trust-store.pfx) - Trusted entity of the client file **client.localhost**.

* KeyStore [key-store.pfx](config/ssl/key-store.pfx) - Contains the certificate presented by the application.

* Client [client.localhost.pfx](src/test/resources/ssl/client.localhost.pfx) (password `storepass`) - Client certificate signed by` trust-store.pfx`, used to consume the services provided by the **publisher-http-s3** app.

### Creating the Certificate Authority

Creation of **Trust Store**

```sh
# Create the CA key (password: storepass)
openssl genrsa -des3 -out localhost.key 4096

# Create CA certificate
openssl req -new -x509 -days 7300 -key localhost.key -out localhost.crt \
  -subj "/C=AR/ST=CABA/L=CABA/O=TEST/OU=TEST/CN=*.localhost"

# Export CA as PKCS #12 (password: storepass)
openssl pkcs12 -export -out trust-store.pfx -inkey localhost.key -in localhost.crt
```

Certificado cliente **client.localhost**

```sh
# Create client certificate (password: storepass)
openssl genrsa -des3 -out client.localhost.key 4096

# Then, create a Certificate Signing Request (CSR)
openssl req -new -key client.localhost.key -out client.localhost.csr \
  -subj "/C=AR/ST=CABA/L=CABA/O=TEST/OU=TEST/CN=client.localhost"

# Sign the client certificate with our CA (Certificate Authority)
openssl x509 -req -days 3065 -in client.localhost.csr -CA localhost.crt -CAkey localhost.key -set_serial 01 -out client.localhost.crt

# Create customer PKCS #12 (PFX) (password: storepass)
openssl pkcs12 -export -out client.localhost.pfx -inkey client.localhost.key -in client.localhost.crt -certfile localhost.crt
```

Certificate presented by the application **publisher-http-s3**

```sh
# Generate a certificate request starting from an existing certificate
openssl x509 -x509toreq -in localhost.crt -out local.localhost.csr -signkey localhost.key

# Create a self-signed certificate
openssl x509 -signkey localhost.key -in local.localhost.csr -req -days 3650 -out local.localhost.crt

# Creating a PKCS #12 (PFX)
openssl pkcs12 -export -out key-store.pfx -inkey localhost.key -in local.localhost.crt
```

## Release

Configure **Nexus** repository user in `~/.m2/settings.xml` file.

- <https://maven.apache.org/guides/mini/guide-encryption.html>

### Release with maven-release-plugin

You must have the key for the repository indicated in tag `<distributionManagement></distributionManagement>`

```sh
# NOTE: If the release branch is not created, the changes are made to master.
# 1 - Create the release branch
git checkout -b release/1.0.0

# 2 - Execute the changes on the branch and create the tag
mvn -Darguments="-DskipTests" release:clean release:prepare release:perform
```

## Possible drawbacks

Problems that can occur in configuration/development.

### Lombok

The `.java` sources do not compile when you use `lombok`.

1 Download Lombok Jar File

- <https://projectlombok.org/downloads/lombok.jar>

2 Start Lombok Installation

```sh
java -jar lombok.jar
```

3 Give Lombok Install Path

Now click on the "Specify Location" button and locate the eclipse.exe path under eclipse installation folder like this.

### Could not resolve dependencies

Depending on the configuration of the file `${user.home}/.m2/settings.xml` it may not resolve some dependencies.

**[ERROR]** Failed to execute goal on project publisher-http-s3: Could not resolve dependencies for project janusky:publisher-http-s3:jar:0.0.1-SNAPSHOT: Could not find artifact app.janusky:validate-certificate:jar:0.0.7 in Enterprise (https://nexus.server/nexus/repository/public) -> [Help 1]

**Solve**: Add the indicated repositories

* https://nexus.server/nexus/repository/public
* https://nexus.server/nexus/repository/external/
* https://nexus.server/nexus/repository/sandbox-maven/

Example of `settings.xml` is also copied

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

## References

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

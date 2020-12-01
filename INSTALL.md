# Install

Application as a service within a Linux operating system.

- [Systemd Service](https://www.baeldung.com/spring-boot-app-as-a-service#2-systemd)

- <https://medium.com/@sulmansarwar/run-your-java-application-as-a-service-on-ubuntu-544531bd6102>

- <https://docs.spring.io/spring-boot/docs/1.3.1.RELEASE/reference/html/deployment-install.html>

- <https://www.freedesktop.org/software/systemd/man/systemd.service.html>

## Preconditions

Have a instance where to install (server.instance)

The required characteristics of the instance/pc

- RHEL 7.6
- JDK 8/11 installed (/usr/bin/java)
- Certificates for SSL

## Installation

The distribution of the version to install must be downloaded from the [deliverables repository](https://nexus.server/nexus/service/rest/repository/browse/sandbox-maven/janusky/publisher-http-s3/).

Before starting, the environment variables required for correct installation must be defined.

> IMPORTANT: In the values of the variables where it says REPLACE, whoever performs the installation must complete.

Define installation and execution variables

```sh
# Login to instance
ssh appserv@server.instance

# Variables
export APP_VERSION=1.0.0
export APP_HOME=/application

export APP_CONFIG_FILE=$APP_HOME/application-default.yml
export APP_PORT=REMPLAZAR
export APP_API=/api
export APP_KEY_STORE_TYPE=PKCS12
export APP_KEY_STORE_FILE=REMPLAZAR
export APP_KEY_STORE_PASSWORD=REMPLAZAR
export APP_TRUST_STORE_TYPE=PKCS12
export APP_TRUST_STORE_FILE=REMPLAZAR
export APP_TRUST_STORE_PASSWORD=REMPLAZAR
export APP_S3_ACCESS_KEY=REMPLAZAR
export APP_S3_SECRET_KEY=REMPLAZAR
export APP_S3_BUCKET=REMPLAZAR
export APP_S3_ENDPOINT=REMPLAZAR
export APP_S3_KEY_STORE_FILE=REMPLAZAR
export APP_S3_KEY_STORE_PASSWORD=REMPLAZAR
export APP_S3_TRUST_STORE_FILE=REMPLAZAR
export APP_S3_TRUST_STORE_PASSWORD=REMPLAZAR
```

**1** Install version `1.0.0` in `${APP_HOME}`

```sh
# Create directory where application is installed
sudo mkdir -p ${APP_HOME}

# Download resources for installation
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/janusky/publisher-http-s3/${APP_VERSION}/publisher-http-s3-${APP_VERSION}.jar -o ${APP_HOME}/publisher-http-s3-${APP_VERSION}.jar
```

**2** Application configuration file for execution

Apply according to Support/Production recommendations.

```sh
# Version retrieved file name
export FILE_CONFIG_NAME=application.yml

# Download configuration file
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/janusky/publisher-http-s3/$APP_VERSION/publisher-http-s3-$APP_VERSION-config.tar.gz | sudo tar -C /tmp -xz config/$FILE_CONFIG_NAME

# A new file is created with the values read from the variables
envsubst < /tmp/config/$FILE_CONFIG_NAME > file.yml && sudo mv file.yml $APP_CONFIG_FILE
```

**3** Create run file (OPTIONAL)

It is optional, if **service unit** is used without running the `publisher-http-s3.sh` file.

Execution file `publisher-http-s3.sh`

```sh
# Use only if variables where not indicated
#ssh username@server.instance
#export APP_HOME=${APP_HOME:-/application}
#export APP_VERSION=${APP_VERSION:-1.0.0}

# Default values
export APP_JAVA_OPS=${APP_JAVA_OPS:-"-Xms512m -Xmx512m"}
# JAVA variables (Optionals)
export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(readlink $(readlink $(which javac)))))}
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar

# File publisher-http-s3.sh
cat <<\EOF > publisher-http-s3.sh
#!/bin/sh

# Java (Opcional)
export JAVA_HOME=${JAVA_HOME}
export PATH=${PATH}
export CLASSPATH=${CLASSPATH}

# Si el archivo de configuración no se llama application-default.yml
# export SPRING_CONFIG_NAME=config.yml
# export SPRING_CONFIG_LOCATION=file:///${APP_HOME}

# Ejecutar jar
java ${APP_JAVA_OPS} -jar ${APP_HOME}/publisher-http-s3-${APP_VERSION}.jar ${APP_ARGS}

EOF

# Replace variables 
envsubst < publisher-http-s3.sh | sudo tee publisher-http-s3.sh

# Execution and location permission 
sudo chmod +x publisher-http-s3.sh && sudo mv publisher-http-s3.sh $APP_HOME
```

**4** Install as a service and run

This installation of the **service unit** does not use the `publisher-http-s3.sh` file (previous step)

>More info: [create-systemd-unit-file-for-java](https://mincong.io/2018/07/03/create-systemd-unit-file-for-java) 

```sh
# Enter installation path
cd $APP_HOME

export APP_JAVA_OPS=${APP_JAVA_OPS:-"-Xms512m -Xmx512m"}

# Create service file `publisher-http-s3.service`
sudo bash -c "cat >> $APP_HOME/publisher-http-s3.service" <<EOF
[Unit]
Description=publisher-http-s3

[Service]
WorkingDirectory=${APP_HOME}
ExecStart=/usr/bin/java ${APP_JAVA_OPS} -jar publisher-http-s3-${APP_VERSION}.jar ${APP_ARGS}
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target

EOF

# Enable service (RHEL7)
sudo systemctl enable $APP_HOME/publisher-http-s3.service

# Run the service
sudo systemctl daemon-reload
sudo systemctl start publisher-http-s3

# Check
systemctl status publisher-http-s3
```

# Install

Aplicación como servicio dentro de un sistema operativo Linux.

- [Systemd Service](https://www.baeldung.com/spring-boot-app-as-a-service#2-systemd)

- <https://medium.com/@sulmansarwar/run-your-java-application-as-a-service-on-ubuntu-544531bd6102>

- <https://docs.spring.io/spring-boot/docs/1.3.1.RELEASE/reference/html/deployment-install.html>

- <https://www.freedesktop.org/software/systemd/man/systemd.service.html>

## Precondiciones

Contar con una instancia donde instalar (server.instancia).

Las características requeridas de la instancia/equipo

- RHEL 7.6
- JDK 8/11 instalado (/usr/bin/java)
- Certificados para SSL

## Instalación

Se debe descargar la distribución de la versión a instalar desde el [repositorio de entregables](https://nexus.server/nexus/service/rest/repository/browse/sandbox-maven/janusky/publisher-http-s3/).

Antes de comenzar se deben definir las variables de entorno requeridas para la correcta instalación

> IMPORTANTE: En los valores de las variables donde dice REMPLAZAR quien realiza la instalación debe completar.

Definir variables de instalación y ejecución

```sh
# Ingresar a instancia (cloud)
ssh appserv@server.instancia

# Instalación
export APP_VERSION=1.0.0
export APP_HOME=/application

# Ejecución (configuración)
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

**1** Instalar la versión `1.0.0` en `${APP_HOME}`

```sh
# Crear directorio donde se instala aplicación
sudo mkdir -p ${APP_HOME}

# Descargar los recursos para instalación
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/janusky/publisher-http-s3/${APP_VERSION}/publisher-http-s3-${APP_VERSION}.jar -o ${APP_HOME}/publisher-http-s3-${APP_VERSION}.jar
```

**2** Archivo de configuración de aplicación para ejecución

Aplicar según recomendaciones de Soporte/Producción.

```sh
# Nombre de archivo recuperado de la versión
export FILE_CONFIG_NAME=application.yml

# Descargar archivo de configuración
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/janusky/publisher-http-s3/$APP_VERSION/publisher-http-s3-$APP_VERSION-config.tar.gz | sudo tar -C /tmp -xz config/$FILE_CONFIG_NAME

# Se crea un archivo nuevo con los valores leídos de las variables
envsubst < /tmp/config/$FILE_CONFIG_NAME > file.yml && sudo mv file.yml $APP_CONFIG_FILE
```

**3** Crear archivo de ejecución (OPCIONAL)

Es opcional, si se utiliza **service unit** sin ejecución de archivo `publisher-http-s3.sh`.

Archivo ejecución `publisher-http-s3.sh`

```sh
# Usar solo si las variables no fueron inicadas
#ssh username@server.instancia
#export APP_HOME=${APP_HOME:-/application}
#export APP_VERSION=${APP_VERSION:-1.0.0}

# Variables con valores por defecto
export APP_JAVA_OPS=${APP_JAVA_OPS:-"-Xms512m -Xmx512m"}
# Variables JAVA (Opcional)
export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(readlink $(readlink $(which javac)))))}
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar

# Archivo publisher-http-s3.sh
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

# Reemplazar variables 
envsubst < publisher-http-s3.sh | sudo tee publisher-http-s3.sh

# Permiso de ejecución y ubicación
sudo chmod +x publisher-http-s3.sh && sudo mv publisher-http-s3.sh $APP_HOME
```

**4** Instalar como servicio y ejecutar

En esta instalación de la **service unit** no se utiliza el archivo `publisher-http-s3.sh` (paso anterior).

>Más info: [create-systemd-unit-file-for-java](https://mincong.io/2018/07/03/create-systemd-unit-file-for-java) 

```sh
# Ingresar en ruta de instalación
cd $APP_HOME

export APP_JAVA_OPS=${APP_JAVA_OPS:-"-Xms512m -Xmx512m"}

# Crear archivo de servicio `publisher-http-s3.service`
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

# Habilitar servicio (RHEL7)
sudo systemctl enable $APP_HOME/publisher-http-s3.service

# Habilitar servicio
sudo systemctl daemon-reload
sudo systemctl start publisher-http-s3

# Chequear
systemctl status publisher-http-s3
```

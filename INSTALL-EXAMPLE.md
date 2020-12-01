# Install Example

Installation from the [instructions](INSTALL.md) existing in the project.

- <https://github.com/janusky/publisher-http-s3/blob/master/INSTALL.md>

```sh
# Into of the server
ssh username@server.instancia

# Variables
export APP_VERSION=0.0.3
export APP_HOME=/application
export APP_CONFIG_FILE=$APP_HOME/application-default.yml
export APP_PORT=443
export APP_API=/api
export APP_KEY_STORE_TYPE=PKCS12
export APP_KEY_STORE_FILE=$APP_HOME/key-store.pfx
export APP_KEY_STORE_PASSWORD=storepass
export APP_TRUST_STORE_TYPE=PKCS12
export APP_TRUST_STORE_FILE=$APP_HOME/trust-store.pfx
export APP_TRUST_STORE_PASSWORD=storepass
# S3
export APP_S3_ACCESS_KEY=sandboxKey
export APP_S3_SECRET_KEY=sandboxSecret
export APP_S3_BUCKET=sandbox-bk
export APP_S3_ENDPOINT=localhost:9280
export APP_S3_KEY_STORE_FILE=$APP_HOME/key-store-ceph.jks
export APP_S3_KEY_STORE_PASSWORD=changeit
export APP_S3_TRUST_STORE_FILE=$APP_HOME/trust-store-ceph.jks
export APP_S3_TRUST_STORE_PASSWORD=changeit

# Create directory where application is installed
sudo mkdir -p ${APP_HOME}

# Download resources for installation
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/janusky/publisher-http-s3/${APP_VERSION}/publisher-http-s3-${APP_VERSION}.jar -o ${APP_HOME}/publisher-http-s3-${APP_VERSION}.jar

# Copy certificates (assumed to exists in /tmp/ssl)
sudo cp -r /tmp/ssl/* $APP_HOME

# Version retrieved file name
export FILE_CONFIG_NAME=application.yml

# Download configuration file
sudo curl --noproxy '*' -k https://nexus.server/nexus/repository/sandbox-maven/dearsi/publisher-http-s3/$APP_VERSION/publisher-http-s3-$APP_VERSION-config.tar.gz | sudo tar -C /tmp -xz config/$FILE_CONFIG_NAME

# A new file is created with the values read from the variables
envsubst < /tmp/config/$FILE_CONFIG_NAME > file.yml && sudo mv file.yml $APP_CONFIG_FILE

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
#TimeoutStopSec=10
#Restart=on-failure
#RestartSec=5

[Install]
WantedBy=multi-user.target

EOF

# Enable service (RHEL7)
# sudo ln -s $APP_HOME/publisher-http-s3.service /etc/systemd/system/
sudo systemctl enable $APP_HOME/publisher-http-s3.service

# Run the service
sudo systemctl daemon-reload
sudo systemctl start publisher-http-s3

# Check
systemctl status publisher-http-s3
```

To access: <https://local.localhost/info>

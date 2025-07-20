# Use Eclipse Temurin for Java 17 LTS
FROM eclipse-temurin:17-jdk

# Copy Zscaler CA certificate first
COPY docker/ca/zscaler-root-ca.pem /usr/local/share/ca-certificates/zscaler-root-ca.pem

RUN apt-get update && apt-get install -y ca-certificates wget && \
    update-ca-certificates && \
    wget --no-check-certificate -O /usr/local/share/ca-certificates/isrgrootx1.pem https://letsencrypt.org/certs/isrgrootx1.pem && \
    update-ca-certificates && \
    keytool -import -trustcacerts -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -alias zscaler-root-ca -file /usr/local/share/ca-certificates/zscaler-root-ca.pem && \
    keytool -import -trustcacerts -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt -alias isrgrootx1 -file /usr/local/share/ca-certificates/isrgrootx1.pem && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/worker-background-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/EmailTrigger-1.0-SNAPSHOT.jar azurefunction.jar
ENTRYPOINT exec java $JAVA_OPTS -jar azurefunction.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar azurefunction.jar

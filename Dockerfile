FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/cloud_storage-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
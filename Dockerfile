FROM openjdk:21-jdk-slim
EXPOSE 8099
ADD target/ofx-ms-sse.jar ofx-ms-sse.jar
ENTRYPOINT [ "java", "-jar", "/ofx-ms-sse.jar" ]
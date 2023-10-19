FROM eclipse-temurin:11-centos7
ARG JAR_DIR=build/libs/
ARG JAR_FILE=octopus-test-*.jar
WORKDIR app
COPY $JAR_DIR$JAR_FILE ./
RUN mv $JAR_FILE app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
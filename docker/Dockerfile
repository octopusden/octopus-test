FROM eclipse-temurin:21-jre
ARG JAR_DIR=build/libs/
ARG JAR_FILE=octopus-test-*.jar
WORKDIR /app
COPY $JAR_DIR$JAR_FILE ./
RUN mv $JAR_FILE app.jar
USER 1001
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 CMD ["sh", "-c", "test \"$(cat /proc/1/comm)\" = \"java\""]
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080

FROM amazoncorretto:11
WORKDIR /ranabauts
EXPOSE 8080
COPY /build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

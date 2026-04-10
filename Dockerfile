FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean install -DskipTests

CMD ["java", "-jar", "target/catalogo-0.0.1-SNAPSHOT.jar"]
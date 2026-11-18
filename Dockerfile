from eclipse-temurin:17-jdk as build
workdir /app

copy .mvn .mvn 
copy mvnw pom.xml ./
copy src ./src/
run ./mvnw -q clean package -DskipTests

from eclipse-temurin:17-jre
workdir /app
copy --from=build /app/target/*.jar ./app.jar
expose 9090
entrypoint ["java", "-jar", "app.jar"]
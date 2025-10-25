# Stage 1: build com Maven
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src

# Compilar o projeto
RUN mvn clean install -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:23-jdk AS runtime
WORKDIR /app

# Copiar o JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Definir o comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]

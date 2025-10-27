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
EXPOSE 8000

# FIX PRINCIPAL: Configurar variáveis de ambiente necessárias
# A aplicação precisa destas variáveis para iniciar
ENV SERVER_PORT=8000

# Iniciar a aplicação com as propriedades corretas
# Spring Boot automaticamente converte variáveis de ambiente UPPER_SNAKE_CASE
# para propriedades application.property.format
ENTRYPOINT ["java", "-Dserver.port=8000", "-jar", "app.jar"]

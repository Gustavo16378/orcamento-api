# Etapa 1: Build (compila o código e gera o JAR dentro do container)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Compila o projeto (gera o JAR)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime (só roda o JAR gerado)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=build /app/target/orcamento-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
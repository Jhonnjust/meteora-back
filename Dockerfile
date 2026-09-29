# ---- Etapa 1: build com Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copia só o pom.xml primeiro para aproveitar cache de dependencias entre builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: imagem final, só com o JRE (menor e mais rapida de subir) ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/meteora-backend-1.0.0.jar app.jar

# Render injeta a variavel PORT dinamicamente; application-prod.properties ja le
# server.port=${PORT:8080}, entao nao precisamos fixar a porta aqui.
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

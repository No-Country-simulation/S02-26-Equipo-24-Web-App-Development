# Imagen base con Java 21
FROM eclipse-temurin:21-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app


COPY . .

# Dar permisos al wrapper de Maven
RUN chmod +x mvnw

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto
EXPOSE 8080

# Comando para iniciar la app
CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT}"]

FROM openjdk:17-jdk-slim

# Pindahkan file .war ke dalam container
COPY target/api-gateway-scanning-document-0.0.1-SNAPSHOT.war /app/api-gateway-scanning-document-0.0.1-SNAPSHOT.war

# Perintah untuk menjalankan aplikasi ketika container dimulai
CMD ["java", "-jar", "/app/api-gateway-scanning-document-0.0.1-SNAPSHOT.war"]

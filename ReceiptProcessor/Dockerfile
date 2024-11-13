FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /root/

COPY --from=build /ReceiptProcessor/target/ReceiptProcessor-0.0.1-SNAPSHOT.jar ./

CMD ["java", "-jar", "receipt-processor.jar"]

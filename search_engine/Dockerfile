# Stage 1: Install Python packages
FROM python:3.9-alpine as builder

RUN apk add --no-cache bash curl
RUN pip install --prefix=/install torch transformers onnx

# Stage 2: Build the final image
FROM eclipse-temurin:21-jre-alpine

LABEL author="Giovanni Pio Grieco"
WORKDIR /app
ADD src/main/resources/application.properties application.properties
ADD target/altair-0.0.1.jar altair-0.0.1.jar
ADD data data
ADD index index
ADD model model

# install dependencies using apk
RUN apk add --no-cache bash curl python3

# Copy installed Python packages from the builder stage
COPY --from=builder /install /usr/local

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "altair-0.0.1.jar"]
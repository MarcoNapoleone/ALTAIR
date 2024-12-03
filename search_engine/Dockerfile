FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache python3 py3-pip

RUN rm -f /usr/lib/python3*/EXTERNALLY-MANAGED

# Install pip packages with extra index URL for torch
RUN pip install --extra-index-url https://download.pytorch.org/whl/cpu torch
RUN pip install transformers
#RUN pip install onnx

ADD model model
ADD index index
ADD data data
ADD src/main/resources/application.properties application.properties
ADD target/altair-0.0.1.jar altair-0.0.1.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "altair-0.0.1.jar"]
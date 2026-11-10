FROM gradle:8.14-jdk17 AS builder

WORKDIR /app

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/scripts/convert-to-hls.sh /app/scripts/convert-to-hls.sh

RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && chmod +x /app/scripts/convert-to-hls.sh \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

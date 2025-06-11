# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.* gradle.properties settings.gradle* /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle clean build -i --stacktrace || true

# Stage 2: Build Application
FROM gradle:latest AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /home/gradle/app
WORKDIR /home/gradle/app
RUN gradle buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM openjdk:23 AS runtime
EXPOSE 8081
RUN mkdir /app
COPY --from=build /home/gradle/app/build/libs/*.jar /app/locdots-backend.jar
ENTRYPOINT ["java","-jar","/app/locdots-backend.jar"]

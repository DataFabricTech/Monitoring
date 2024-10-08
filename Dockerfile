ARG IMAGE=openjdk:21-jdk-slim

##############################################################
###             Stage : Source Copy              ###
###############################################################
FROM ${IMAGE} as setter

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY src ./src
COPY settings.gradle.kts ./
COPY build.gradle.kts ./

###############################################################
###             Stage : Source Analysis & Test              ###
###############################################################
FROM setter as tester

ARG SKIP_TEST=false

WORKDIR /app

RUN --mount=type=cache,target=$HOME/.m2 if [ "$SKIP_TEST" != "true" ] ; \
    then \
                ./gradlew test \
    fi


###############################################################
###                      Stage : Build                      ###
###############################################################
FROM setter as builder

ARG PROJECT_VERSION=v1.0.0

WORKDIR /app

RUN --mount=type=cache,target=$HOME/.m2 \
    ./gradlew build -x test -Dorg.gradle.workers.max=20

###############################################################
###             Stage : Make Product Image                  ###
###############################################################
FROM ${IMAGE} as product

## Labelling
LABEL company="Mobigen" \
        team="mobigen-platform-team" \
        email="irisdev@mobigen.com"

WORKDIR /app

ENV PROFILE "prod"

RUN mkdir -p /app/conf

## Install Locale(ko_KR.UTF-8)
RUN apt-get update && apt-get install -y locales \
    && sed -i 's/^# \(ko_KR.UTF-8\)/\1/' /etc/locale.gen \
    && locale-gen && localedef -f UTF-8 -i ko_KR ko_KR.UTF-8 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/monitoring-1.0-SNAPSHOT.jar /app/service.jar
COPY --from=builder /app/build/resources/main/* /app/conf/

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/app/service.jar"]
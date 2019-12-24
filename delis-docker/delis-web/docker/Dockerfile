FROM openjdk:8-jdk-alpine

ARG VERSION
ENV VERSION ${VERSION}

ENV WORK_DIR /app

ARG DELIS_UID="987"
ARG DELIS_GID="987"

RUN mkdir -p "$WORK_DIR" && \
    addgroup -S -g $DELIS_GID delis && \
    adduser -S -u $DELIS_UID -G delis delis

COPY ./app $WORK_DIR

WORKDIR $WORK_DIR

USER delis

CMD [ "java", "org.springframework.boot.loader.JarLauncher" ]

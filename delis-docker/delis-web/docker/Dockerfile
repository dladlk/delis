FROM openjdk:8-jdk-alpine

ARG VERSION
ENV VERSION ${VERSION}

ENV WORK_DIR /app

ARG DELIS_UID="987"
ARG DELIS_GID="987"

RUN mkdir -p "$WORK_DIR" && \
    addgroup -S -g $DELIS_GID delis && \
    adduser -S -u $DELIS_UID -G delis delis

COPY ./app/org $WORK_DIR/org
COPY ./app/BOOT-INF/lib $WORK_DIR/BOOT-INF/lib
COPY ./app/oxalis/*.jar $WORK_DIR/BOOT-INF/lib/
COPY ./app/delis/*.jar $WORK_DIR/BOOT-INF/lib/
COPY ./app/BOOT-INF/classes $WORK_DIR/BOOT-INF/classes
COPY ./app/META-INF $WORK_DIR/META-INF

WORKDIR $WORK_DIR

USER delis

CMD [ "java", "org.springframework.boot.loader.JarLauncher" ]

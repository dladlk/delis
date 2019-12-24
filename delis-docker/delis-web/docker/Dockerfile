FROM openjdk:8-jdk-alpine

ARG VERSION
ENV VERSION ${VERSION}

ENV WORK_DIR /app

RUN mkdir -p $WORK_DIR

COPY ./app $WORK_DIR

WORKDIR $WORK_DIR

CMD [ "java", "org.springframework.boot.loader.JarLauncher" ]

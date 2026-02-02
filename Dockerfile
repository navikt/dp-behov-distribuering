FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:6c1da3fff4517cbe0c71e54d5a8daa6df89fbb5daa892aa707ba1adb1f41c584

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

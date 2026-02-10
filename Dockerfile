FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:a9dc3dbfab8034fbdb627d7a9d29130538f3b79f5a6667fd95792adc694e8954

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

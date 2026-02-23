FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:b5e9f0f4746359cdda31eb5950ac4a775d0ad541c15623773582cf9310944c27

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

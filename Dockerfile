FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:903645783604014bbe343915dbc389382d018fc96e4233349c6a5f6ba010a0cc

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

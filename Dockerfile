FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:7daa12cb469fdea0e94610a18e107f1a3415dead8698e18ca097cf0e6ad373c0

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

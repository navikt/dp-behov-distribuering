FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:8c50c419625d4b1d512366bfefeb8b0bc7275e809129f87a09ade04b5f068e3b

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

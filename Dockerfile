FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:deae1152d3277b9854b043a975fd3c2e423d29a40e84683cd478c838cf74b879

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

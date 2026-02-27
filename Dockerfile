FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:f5b7f80902b854bb77bb4cc9038c5ce0e0eb2a09e3b62676b447466d4e14fb62

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

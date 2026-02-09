FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:8654adb729d152617f8590c7267d02f3f540430a553870aca7f6e51fc16792e7

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

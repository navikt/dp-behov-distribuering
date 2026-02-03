FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:18cbf847eada5f82c8c41f191c4a24fcd29b8720e86d7bff8657f4dba44d10ab

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

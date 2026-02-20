FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:22fa63e5f2f5df5a4d0278918149f101394f8e25c7cf80a1a2cd331d751b4c18

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

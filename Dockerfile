FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:7a975251ddc3380c9c5b5521266ea9bc4db226ad4fc42ef168397c403a9f5bdf

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

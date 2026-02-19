FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:98355311df239411b5e12a221521644445a2dbcfd8aea0cc7157c15373c0af9b

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY build/libs/dp-behov-distribuering-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

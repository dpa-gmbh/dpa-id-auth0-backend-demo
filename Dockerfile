FROM public.ecr.aws/docker/library/amazoncorretto:21-alpine

EXPOSE 8080

RUN  mkdir -p /usr/local/bin
COPY target/dpa-id-auth0-backend-demo.jar /usr/local/bin
COPY src/scripts/start-service.sh /usr/local/bin
RUN  chmod +x /usr/local/bin/start-service.sh
RUN  ls -l /usr/local/bin

ENTRYPOINT /usr/local/bin/start-service.sh
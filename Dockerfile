FROM postgres:18

RUN echo "ru_RU.UTF-8 UTF-8" >> /etc/locale.gen
RUN locale-gen

COPY init.sql /docker-entrypoint-initdb.d/init.sql
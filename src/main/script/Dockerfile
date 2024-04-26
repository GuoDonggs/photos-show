FROM debian:latest AS TMP


COPY openjdk21.tar.gz  /openjdk21.tar.gz


RUN tar -xvif /openjdk21.tar.gz -C /usr/local && mv /usr/local/jdk-21.0.2 /usr/local/java



ENTRYPOINT ["top", "-b"]

FROM debian

COPY --from=TMP /usr/local/java /usr/local/java

# 设置中文支持
ENV  LANG C.UTF-8

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

WORKDIR /www

ENV JAVA_HOME=/usr/local/java
ENV PATH=$PATH:$JAVA_HOME/bin

CMD ["bash"]


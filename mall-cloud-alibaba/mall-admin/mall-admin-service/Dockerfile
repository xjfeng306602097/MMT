# 基础镜像
FROM shaw1993/jdk11-skw-agent:latest

# 维护者信息
MAINTAINER xiaojunfeng <xiaojunfeng@163.com>

ARG profile

ENV SPRING_PROFILES_ACTIVE ${profile}

# 设置容器时区为当前时区
RUN if [ "${profile}" = "dev" ] ; then /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime ; else /bin/cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime ; fi && \
    if [ "${profile}" = "dev" ] ; then echo 'Asia/Shanghai' >/etc/timezone ; else echo 'Asia/Bangkok' >/etc/timezone ; fi

# /tmp 目录作为容器数据卷目录，SpringBoot内嵌Tomcat容器默认使用/tmp作为工作目录，任何向 /tmp 中写入的信息不会记录进容器存储层
# 在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录
VOLUME /tmp

WORKDIR /

# 复制主机文件至镜像内，复制的目录需放置在 Dockerfile 文件同级目录下
ADD target/mall-admin-service.jar app.jar

# 容器启动执行命令
ENTRYPOINT ["java", "-Xmx256m", "-javaagent:/skywalking/agent/skywalking-agent.jar", "-Dskywalking.agent.service_name=mall-admin", "-Dskywalking.collector.backend_service=10.58.5.242:11800", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

# 声明容器提供服务端口
EXPOSE 8100




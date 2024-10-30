# 使用官方的 OpenJDK 作为基础镜像
FROM openjdk:17

# 为应用创建目录
VOLUME /tmp

# 将应用的 JAR 文件复制到容器中
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 暴露应用运行的端口
EXPOSE 8080

# 运行 Spring Boot 应用
ENTRYPOINT ["java","-jar","/app.jar"]
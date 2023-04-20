FROM maven

COPY . .
RUN mvn clean install
ENTRYPOINT ["java","-jar","target/FS_Discord_Bot-1.0-SNAPSHOT-jar-with-dependencies.jar"]

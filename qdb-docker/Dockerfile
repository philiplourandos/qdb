FROM mcr.microsoft.com/java/jdk:13u2-zulu-alpine

RUN wget -t 0 https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar 

EXPOSE 8082 1521

CMD java -cp h2*.jar org.h2.tools.Server \
 	-web -webAllowOthers -webPort 8082 \
 	-tcp -tcpAllowOthers -tcpPort 1521 -ifNotExists \
	-baseDir /root
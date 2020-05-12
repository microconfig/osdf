# Use Java 8 slim JRE
FROM openjdk:8-jre-slim

USER root
# JMeter version
ARG JMETER_VERSION="5.1.1"

# Install few utilities
RUN apt-get clean && \
    apt-get update && \
    apt-get -qy install \
                curl \
                telnet \
                iputils-ping \
                unzip \
                nano

# Set JMeter related environment variables
ENV JMETER_HOME /opt/apache-jmeter-${JMETER_VERSION}
ENV JMETER_BIN ${JMETER_HOME}/bin
ENV JMETER_DOWNLOAD_URL https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
# Set default values for allocation of system resources (memory) which will be used by JMeter
ENV Xms 256m
ENV Xmx 512m
ENV MaxMetaspaceSize 1024m
# Change timezone to local time
ENV TZ="Europe/Moscow"
RUN export TZ=$TZ
# Install jmeter
RUN mkdir -p /tmp/dependencies \
 && curl -L ${JMETER_DOWNLOAD_URL} > /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz \
 && mkdir -p /opt \
 && tar -xzf /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz -C /opt \
 && rm -rf /tmp/dependencies

# Set JMeter home
ENV PATH $PATH:$JMETER_BIN
# copy our entrypoint
COPY entrypoint.sh /
COPY log4j2.xml /opt/apache-jmeter-${JMETER_VERSION}/bin/log4j2.xml
RUN chmod +x ./entrypoint.sh 

# Run command to allocate the default system resources to JMeter at 'docker run'
ENTRYPOINT [ "/entrypoint.sh" ] 
# Use jmeter base image
FROM slav9n4ik/jmeter-base:latest


# Ports to be exposed from the container for JMeter Slaves/Server
EXPOSE 1099 50000

RUN useradd -m jmeter
# Application to run on starting the container
ENTRYPOINT $JMETER_HOME/bin/jmeter-server \
                         -Dserver.rmi.localport=50000 \
                         -Dserver_port=1099 \
                         -Dserver.rmi.ssl.disable=true 

#docker run -dit --name slave01 -p 1099:1099 -p 50001:50000 slav9n4ik/jmeter-slave:latest
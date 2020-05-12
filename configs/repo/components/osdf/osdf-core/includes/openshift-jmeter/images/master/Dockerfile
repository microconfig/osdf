# Use jmeter base image
FROM slav9n4ik/jmeter-base:latest

# Ports to be exposed from the container for JMeter Master
EXPOSE 60000

ENV SLAVE_HOSTS "default"
ENV PATH_TO_JMETERPLAN "default"

ENTRYPOINT jmeter -n -t ${PATH_TO_JMETERPLAN} \
                        -R${SLAVE_HOSTS} \
                        -Dserver.rmi.ssl.disable=true
                        

#docker run -dit --name master slav9n4ik/jmeter-master:latest /bin/bash
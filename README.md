[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.microconfig/osdf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.microconfig/osdf)

# OSDF

Commandline tool for managing OpenShift applications with the power of [Microconfig](https://https://microconfig.io/).

Store Kubernetes resources and application configs without copy-paste of common parts using Microconfig and deploy and manage applications using OSDF

It's higly recommended to get familliar with [microconfig](https://https://microconfig.io/) first.

Docs are available in [repository wiki](https://github.com/microconfig/osdf/wiki)

### OSDF Commands
Deploy all applications
```
~ osdf deploy
Deploying: frontend backend auth

Deploying group - [frontend, backend, auth]
frontend OK
backend OK
auth FAILED
```
Show statuses
```
~ osdf status
COMPONENT    VERSION    CONFIGS    STATUS       REPLICAS
frontend     1.1        develop    READY        1/1
backend      1.0        master     READY        1/1
auth         1.0        master     FAILED       1/1
```
Manage applications
```
~ osdf stop frontend
~ osdf restart frontend
```
Show logs
```
~ osdf logs backend
```
Manage app versions, config versions and envs
```
~ osdf env dev                       # switch environment to dev
~ osdf versions -cv develop -pv 1.1  # set config version to develop (git branch) and project version to 1.1
~ osdf deploy                        # and deploy apps with these versions
```


## Installation

Download latest release and run it as follows
```
java -jar osdf-<version>.jar install
```
This will create `.osdf` folder in home directory and add `osdf` binary to `PATH`

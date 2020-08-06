[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.microconfig/osdf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.microconfig/osdf)

# OSDF

Commandline tool for managing OpenShift applications with the power of [Microconfig](https://https://microconfig.io/).

Store Kubernetes resources and application configs without copy-paste of common parts using Microconfig and deploy and manage applications using OSDF

It's higly recommended to get familliar with [microconfig](https://https://microconfig.io/) first.

## Overview
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

## Installation

Download latest release and run it as follows
```
java -jar osdf-<version>.jar install
```
This will create `.osdf` folder in home directory and add `osdf` binary to `PATH`

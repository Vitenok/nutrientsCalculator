#!/usr/bin/env bash

export JAVA_HOME=${OPENSHIFT_DATA_DIR}jdk1.8.0_40 #/etc/alternatives/java_sdk_1.8.0
export PATH=$JAVA_HOME/bin:$PATH
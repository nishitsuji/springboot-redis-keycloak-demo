#!/bin/bash
if [ ${ENV_BOOT} = "local" ]; then
  # local停止したいけど、方法わからないので空w
else
  # server
  ps aux | grep java | grep Sl | grep -G springboot-redis-keycloak-demo-*-SNAPSHOT.jar | grep -v grep | awk '{ print "kill -9", $2 }'| sudo sh
fi


#!/bin/bash
export ENV_BOOT=local
export LOCAL_DEBUG="false"#なんかバグっているのであとで修正

# Redis
export REDIS_CLUSTER_ENDPOINT="localhost"
export REDIS_CLUSTER_PORT="26379"
export REDIS_CLUSTER_TOKEN="MTIxMg=="
# Keycloak
export KEYCLOAK_CLUSTER_ENDPOINT="http://localhost:28080/auth"
#Spring Boot
export SPRING_BOOT_PORT="8000"

if [ ${ENV_BOOT} = "local" ]; then

  docker-compose -f env/demo-keycloak/docker-compose.yaml up -d

  # local
  if [ ${LOCAL_DEBUG} = "true" ]; then
    sh -E ./gradlew bootRun -Pargs=-Dspring.profiles.active=${ENV_BOOT} --debug-jvm
  else
    sh -E ./gradlew bootRun -Pargs=-Dspring.profiles.active=${ENV_BOOT} --stacktrace
  fi
else
  # server
  sh -E java -jar /opt/application/springboot-redis-keycloak-demo-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=${ENV_BOOT}
fi
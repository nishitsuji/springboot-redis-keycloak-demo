package com.example.springbootrediskeycloakdemo.context.auth.service;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.common.VerificationException;
import org.springframework.security.core.Authentication;

public interface AccessGrantService {

  Authentication login(KeycloakDeployment deployment, AuthInfo authInfo)
      throws VerificationException;

  void entryKeycloakAccountForRedis(SimpleKeycloakAccount account, AuthInfo authInfo);

  Authentication redisAuthorization(KeycloakDeployment keycloakDeployment, String tokenString);
}

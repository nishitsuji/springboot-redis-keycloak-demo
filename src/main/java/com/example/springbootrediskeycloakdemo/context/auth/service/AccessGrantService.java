package com.example.springbootrediskeycloakdemo.context.auth.service;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.model.AuthorityInfo;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessTokenResponse;

public interface AccessGrantService {

  AccessTokenResponse login(KeycloakDeployment deployment, AuthInfo authInfo);

  void entryKeycloakAccountForRedis(SimpleKeycloakAccount account, AuthInfo authInfo);

  AuthorityInfo redisAuthorization(KeycloakDeployment keycloakDeployment, String tokenString);
}

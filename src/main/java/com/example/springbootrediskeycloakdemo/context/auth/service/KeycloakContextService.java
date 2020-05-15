package com.example.springbootrediskeycloakdemo.context.auth.service;

import com.example.springbootrediskeycloakdemo.context.auth.model.UserCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.keycloak.adapters.KeycloakDeployment;
import org.springframework.security.core.Authentication;

public interface KeycloakContextService {
  KeycloakDeployment resolveDeployment(
      HttpServletRequest request,
      HttpServletResponse response,
      String realmName,
      boolean alwaysRefreshToken);

  Authentication convertUserCredential(
      KeycloakDeployment keycloakDeployment, UserCredential credential);
}

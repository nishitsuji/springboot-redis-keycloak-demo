package com.example.springbootrediskeycloakdemo.settings.security.auth.wapper;

import lombok.Setter;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

/**
 * {@link KeycloakSecurityContextRequestFilter}のトークンリフレッシュ機能を制御するためのWrapper
 */
public class RefreshableKeycloakSecurityContextWrapper extends RefreshableKeycloakSecurityContext {

  @Setter
  private boolean refreshable;

  public RefreshableKeycloakSecurityContextWrapper(
      KeycloakDeployment deployment,
      AdapterTokenStore tokenStore,
      String tokenString,
      AccessToken token,
      String idTokenString,
      IDToken idToken,
      String refreshToken) {
    super(deployment, tokenStore, tokenString, token, idTokenString, idToken, refreshToken);
  }

  public RefreshableKeycloakSecurityContextWrapper(
      KeycloakDeployment deployment,
      AdapterTokenStore tokenStore,
      String tokenString,
      AccessToken token,
      String idTokenString,
      IDToken idToken,
      String refreshToken,
      boolean refreshable) {
    super(deployment, tokenStore, tokenString, token, idTokenString, idToken, refreshToken);
    this.refreshable = refreshable;
  }

  @Override
  public boolean isActive() {
    if (!refreshable) return super.isActive();
    return true;
  }
}

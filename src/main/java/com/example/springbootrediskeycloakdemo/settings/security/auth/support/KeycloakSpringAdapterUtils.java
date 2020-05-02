package com.example.springbootrediskeycloakdemo.settings.security.auth.support;

import com.example.springbootrediskeycloakdemo.settings.security.auth.wapper.RefreshableKeycloakSecurityContextWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.TokenVerifier;
import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class KeycloakSpringAdapterUtils {

  public static RefreshableKeycloakSecurityContextWrapper createKeycloakSecurityContext(
      KeycloakDeployment deployment, AccessTokenResponse accessTokenResponse)
      throws VerificationException {

    String tokenString = accessTokenResponse.getToken();
    String idTokenString = accessTokenResponse.getIdToken();
    return createKeycloakSecurityContext(
        deployment, tokenString, idTokenString, accessTokenResponse.getRefreshToken());
  }

  public static RefreshableKeycloakSecurityContextWrapper createKeycloakSecurityContext(
      KeycloakDeployment deployment, String tokenString, String idTokenString, String refreshToken)
      throws VerificationException {
    AccessToken accessToken = TokenVerifier.create(tokenString, AccessToken.class).getToken();
    IDToken idToken = null;

    if (!StringUtils.isEmpty(idTokenString)) {
      try {
        JWSInput input = new JWSInput(idTokenString);
        idToken = input.readJsonContent(IDToken.class);
      } catch (JWSInputException e) {
        throw new VerificationException("Unable to verify ID token", e);
      }
    }

    return new RefreshableKeycloakSecurityContextWrapper(
        deployment, null, tokenString, accessToken, idTokenString, idToken, refreshToken);
  }

  public static Collection<? extends GrantedAuthority> createGrantedAuthorities(
      RefreshableKeycloakSecurityContext context, GrantedAuthoritiesMapper mapper) {
    Assert.notNull(context, "RefreshableKeycloakSecurityContext cannot be null");
    List<KeycloakRole> grantedAuthorities = new ArrayList<>();

    for (String role : AdapterUtils.getRolesFromSecurityContext(context)) {
      grantedAuthorities.add(new KeycloakRole(role));
    }

    return mapper != null
        ? mapper.mapAuthorities(grantedAuthorities)
        : Collections.unmodifiableList(grantedAuthorities);
  }

  public static OidcKeycloakAccount createAccount(
      KeycloakDeployment deployment, RefreshableKeycloakSecurityContext context) {
    Set<String> roles = AdapterUtils.getRolesFromSecurityContext(context);
    KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal =
        AdapterUtils.createPrincipal(deployment, context);
    return new SimpleKeycloakAccount(principal, roles, context);
  }
}

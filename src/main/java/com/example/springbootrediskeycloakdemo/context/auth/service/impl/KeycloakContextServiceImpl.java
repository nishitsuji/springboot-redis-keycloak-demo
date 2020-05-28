package com.example.springbootrediskeycloakdemo.context.auth.service.impl;

import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createAccount;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createGrantedAuthorities;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createKeycloakSecurityContext;

import com.example.springbootrediskeycloakdemo.context.auth.model.UserCredential;
import com.example.springbootrediskeycloakdemo.context.auth.service.KeycloakContextService;
import java.util.Collection;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class KeycloakContextServiceImpl implements KeycloakContextService {

  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;
  @Autowired private AdapterDeploymentContext context;
  @Autowired private AdapterConfig adapterConfig;

  @Override
  public KeycloakDeployment resolveDeployment(
      HttpServletRequest request,
      HttpServletResponse response,
      String realmName,
      boolean alwaysRefreshToken) {
    final HttpFacade facade = new SimpleHttpFacade(request, response);
    final KeycloakDeployment deployment = context.resolveDeployment(facade);
    if (Objects.isNull(deployment)) {
      throw new RuntimeException("KeycloakDeployment is null");
    }
    if (!StringUtils.isEmpty(realmName)) deployment.setRealm(realmName);
    deployment.setDelegateBearerErrorResponseSending(true);
    deployment.setAlwaysRefreshToken(alwaysRefreshToken);
    deployment.setAuthServerBaseUrl(adapterConfig);
    return deployment;
  }

  @Override
  public Authentication convertUserCredential(
      KeycloakDeployment keycloakDeployment, UserCredential credential) {
    try {
      final RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext =
          createKeycloakSecurityContext(
              keycloakDeployment,
              credential.getTokenString(),
              credential.getIdTokenString(),
              credential.getRefreshToken());

      final Collection<? extends GrantedAuthority> authorities =
          createGrantedAuthorities(refreshableKeycloakSecurityContext, grantedAuthoritiesMapper);

      return new KeycloakAuthenticationToken(
          createAccount(keycloakDeployment, refreshableKeycloakSecurityContext),
          false,
          authorities);
    } catch (final VerificationException e) {
      throw new BadCredentialsException("Unable to validate token", e);
    } catch (final Exception e) {
      throw new AuthenticationServiceException("Error authenticating with Keycloak server", e);
    }
  }
}

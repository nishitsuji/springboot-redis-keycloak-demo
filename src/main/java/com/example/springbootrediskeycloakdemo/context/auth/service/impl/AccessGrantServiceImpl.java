package com.example.springbootrediskeycloakdemo.context.auth.service.impl;

import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createAccount;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createGrantedAuthorities;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createKeycloakSecurityContext;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
import java.util.Collection;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class AccessGrantServiceImpl implements AccessGrantService {

  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  @Override
  public Authentication login(final KeycloakDeployment keycloakDeployment, final AuthInfo authInfo)
      throws VerificationException {

    final RestTemplate restTemplate = new RestTemplate();
    final HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    final HttpEntity<MultiValueMap> entity =
        new HttpEntity<>(authInfo.getQueryParamMap(authInfo), headers);
    final AccessTokenResponse tokenResponse =
        restTemplate.postForObject(
            keycloakDeployment.getTokenUrl(), entity, AccessTokenResponse.class);

    RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext;
    Collection<? extends GrantedAuthority> authorities;
    KeycloakAuthenticationToken token;
    try {
      refreshableKeycloakSecurityContext =
          createKeycloakSecurityContext(keycloakDeployment, tokenResponse);
      authorities =
          createGrantedAuthorities(refreshableKeycloakSecurityContext, grantedAuthoritiesMapper);
      token =
          new KeycloakAuthenticationToken(
              createAccount(keycloakDeployment, refreshableKeycloakSecurityContext),
              false,
              authorities);
    } catch (VerificationException e) {
      throw new BadCredentialsException("Unable to validate token", e);
    } catch (Exception e) {
      throw new AuthenticationServiceException("Error authenticating with Keycloak server", e);
    }

    return token;
  }
}

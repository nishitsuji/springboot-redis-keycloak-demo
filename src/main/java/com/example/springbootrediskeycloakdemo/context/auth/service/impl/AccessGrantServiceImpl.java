package com.example.springbootrediskeycloakdemo.context.auth.service.impl;

import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createAccount;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createGrantedAuthorities;
import static com.example.springbootrediskeycloakdemo.settings.security.auth.support.KeycloakSpringAdapterUtils.createKeycloakSecurityContext;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.model.AuthorityInfo;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
import com.example.springbootrediskeycloakdemo.enums.AlgorithmCode;
import com.example.springbootrediskeycloakdemo.support.CipherUtils;
import com.example.springbootrediskeycloakdemo.support.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collection;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.TokenVerifier;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
  @Autowired private StringRedisTemplate redisTemplate;

  private final String encryptionKey = "1234567890123456";

  @Override
  public Authentication login(
      final KeycloakDeployment keycloakDeployment, final AuthInfo authInfo) {

    final RestTemplate restTemplate = new RestTemplate();
    final HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    final HttpEntity<MultiValueMap> entity =
        new HttpEntity<>(authInfo.getQueryParamMap(authInfo), headers);
    final AccessTokenResponse tokenResponse =
        restTemplate.postForObject(
            keycloakDeployment.getTokenUrl(), entity, AccessTokenResponse.class);

    final RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext;
    final Collection<? extends GrantedAuthority> authorities;
    final KeycloakAuthenticationToken token;
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
    } catch (final VerificationException e) {
      throw new BadCredentialsException("Unable to validate token", e);
    } catch (final Exception e) {
      throw new AuthenticationServiceException("Error authenticating with Keycloak server", e);
    }

    return token;
  }

  @SneakyThrows
  @Override
  public void entryKeycloakAccountForRedis(
      final SimpleKeycloakAccount account, final AuthInfo authInfo) {
    final String redisKey = account.getPrincipal().toString();
    final RefreshableKeycloakSecurityContext context = account.getKeycloakSecurityContext();
    final var redisData =
        new AuthorityInfo(
            authInfo.getPrincipal(),
            authInfo.getCredentials(),
            context.getTokenString(),
            context.getRefreshToken(),
            context.getIdTokenString());
    redisTemplate.delete(redisKey);

    final var json = JsonUtils.encode(redisData);
    final var encryptString =
        CipherUtils.encrypt(json, encryptionKey, encryptionKey, AlgorithmCode.AES_CBC_PKCS5PADDING);

    redisTemplate.opsForValue().set(redisKey, encryptString);
  }

  @SneakyThrows
  @Override
  public Authentication redisAuthorization(
      final KeycloakDeployment keycloakDeployment, final String tokenString) {

    final AccessToken accessToken = TokenVerifier.create(tokenString, AccessToken.class).getToken();

    final String redisKey = accessToken.getSubject();

    final String redisResult = redisTemplate.opsForValue().get(redisKey);
    if (StringUtils.isEmpty(redisResult)) return null;

    final String decryptString =
        CipherUtils.decrypt(
            redisResult, encryptionKey, encryptionKey, AlgorithmCode.AES_CBC_PKCS5PADDING);

    final AuthorityInfo redisData =
        JsonUtils.decodeToObject(decryptString, new TypeReference<AuthorityInfo>() {});

    final RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext;
    final Collection<? extends GrantedAuthority> authorities;
    final KeycloakAuthenticationToken token;
    try {
      refreshableKeycloakSecurityContext =
          createKeycloakSecurityContext(
              keycloakDeployment,
              redisData.getTokenString(),
              redisData.getIdTokenString(),
              redisData.getRefreshToken());
      authorities =
          createGrantedAuthorities(refreshableKeycloakSecurityContext, grantedAuthoritiesMapper);
      token =
          new KeycloakAuthenticationToken(
              createAccount(keycloakDeployment, refreshableKeycloakSecurityContext),
              false,
              authorities);
    } catch (final VerificationException e) {
      throw new BadCredentialsException("Unable to validate token", e);
    } catch (final Exception e) {
      throw new AuthenticationServiceException("Error authenticating with Keycloak server", e);
    }
    return token;
  }
}

package com.example.springbootrediskeycloakdemo.context.auth.service.impl;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.model.AuthorityInfo;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
import com.example.springbootrediskeycloakdemo.enums.AlgorithmCode;
import com.example.springbootrediskeycloakdemo.support.CipherUtils;
import com.example.springbootrediskeycloakdemo.support.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.TokenVerifier;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AccessGrantServiceImpl implements AccessGrantService {

  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;
  @Autowired private StringRedisTemplate redisTemplate;

  private final String encryptionKey = "1234567890123456";

  @Override
  public AccessTokenResponse login(
      final KeycloakDeployment keycloakDeployment, final AuthInfo authInfo) {

    final RestTemplate restTemplate = new RestTemplate();
    final HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    final HttpEntity<MultiValueMap> entity =
        new HttpEntity<>(authInfo.getQueryParamMap(authInfo), headers);
    return restTemplate.postForObject(
        keycloakDeployment.getTokenUrl(), entity, AccessTokenResponse.class);
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
  public AuthorityInfo redisAuthorization(
      final KeycloakDeployment keycloakDeployment, final String tokenString) {

    final AccessToken accessToken = TokenVerifier.create(tokenString, AccessToken.class).getToken();

    final String redisKey = accessToken.getSubject();

    final String redisResult = redisTemplate.opsForValue().get(redisKey);
    if (StringUtils.isEmpty(redisResult)) return null;

    final String decryptString =
        CipherUtils.decrypt(
            redisResult, encryptionKey, encryptionKey, AlgorithmCode.AES_CBC_PKCS5PADDING);

    return JsonUtils.decodeToObject(decryptString, new TypeReference<>() {});
  }
}

package com.example.springbootrediskeycloakdemo.settings.security.auth.filter;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthorityInfo;
import com.example.springbootrediskeycloakdemo.context.auth.model.UserCredential;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
import com.example.springbootrediskeycloakdemo.context.auth.service.KeycloakContextService;
import com.example.springbootrediskeycloakdemo.settings.security.auth.extractor.TokenExtractor;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/** Token認証を行うフィルター */
public class AuthenticationProcessingFilter extends KeycloakAuthenticationProcessingFilter {

  @Autowired private AccessGrantService accessGrantService;
  @Autowired private KeycloakContextService keycloakContextService;

  /**
   * Creates a new Keycloak authentication processing filter with given {@link
   * AuthenticationManager} and the {@link
   * KeycloakAuthenticationProcessingFilter#DEFAULT_REQUEST_MATCHER default request matcher}.
   *
   * @param authenticationManager the {@link AuthenticationManager} to authenticate requests (cannot
   *     be null)
   * @see KeycloakAuthenticationProcessingFilter#DEFAULT_REQUEST_MATCHER
   */
  public AuthenticationProcessingFilter(
      final AuthenticationManager authenticationManager, final AdapterDeploymentContext context) {

    super(authenticationManager);

    this.context = context;
  }

  private static final Logger log =
      LoggerFactory.getLogger(KeycloakAuthenticationProcessingFilter.class);
  private final AdapterDeploymentContext context;

  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request, final HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    // 本来ならここに認可処理を書くべき
    final KeycloakDeployment deployment =
        keycloakContextService.resolveDeployment(request, response, "", false);
    if (Objects.isNull(deployment)) return null;

    final AuthorityInfo authorityInfo =
        accessGrantService.redisAuthorization(deployment, TokenExtractor.extract(request));

    UserCredential credential = UserCredential.convertToAuthorityInfo(authorityInfo);
    return keycloakContextService.convertUserCredential(deployment, credential);
  }

  @Override
  protected void successfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult)
      throws IOException, ServletException {}

  @Override
  protected void unsuccessfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException failed)
      throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
  }
}

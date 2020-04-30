package com.example.springbootrediskeycloakdemo.settings.security.auth.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/** Token認証を行うフィルター */
public class AuthenticationProcessingFilter extends KeycloakAuthenticationProcessingFilter {

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

    return null;
  }

  @Override
  protected void successfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult)
      throws IOException, ServletException {

    //    if (authResult instanceof KeycloakAuthenticationToken
    //        && ((KeycloakAuthenticationToken) authResult).isInteractive()) {
    //      super.successfulAuthentication(request, response, chain, authResult);
    //      return;
    //    }
    //
    //    if (log.isDebugEnabled()) {
    //      log.debug(
    //          "Authentication success using bearer token/basic authentication. Updating
    // SecurityContextHolder to contain: {}",
    //          authResult);
    //    }
    //
    //    SecurityContext context = SecurityContextHolder.createEmptyContext();
    //    context.setAuthentication(authResult);
    //    SecurityContextHolder.setContext(context);
    //
    //    try {
    //      // Fire event
    //      if (this.eventPublisher != null) {
    //        eventPublisher.publishEvent(
    //            new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
    //      }
    //      chain.doFilter(request, response);
    //    } finally {
    //      SecurityContextHolder.clearContext();
    //    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException failed)
      throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
  }
}

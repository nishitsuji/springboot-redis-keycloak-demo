package com.example.springbootrediskeycloakdemo.settings.security;

import com.example.springbootrediskeycloakdemo.settings.security.auth.filter.AuthenticationProcessingFilter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@RequiredArgsConstructor
public class WebSecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

  @Bean
  public KeycloakConfigResolver KeycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.httpBasic()
        .disable()
        .cors()
        .disable()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .permitAll();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 認証失敗時のアクション.
   *
   * @return エントリーポイント
   */
  protected AuthenticationEntryPoint authenticationEntryPoint() {

    return new AuthenticationEntryPoint() {

      @Override
      public void commence(
          final HttpServletRequest request,
          final HttpServletResponse response,
          final AuthenticationException authException)
          throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      }
    };
  }

  @Bean
  public KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter()
      throws Exception {
    final KeycloakAuthenticationProcessingFilter filter =
        new AuthenticationProcessingFilter(authenticationManagerBean(), adapterDeploymentContext());
    filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
    return filter;
  }
}

package com.example.springbootrediskeycloakdemo.context.auth.model;

import lombok.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Value
public class AuthorityInfo extends UsernamePasswordAuthenticationToken {

  /**
   * This constructor can be safely used by any code that wishes to create a <code>
   * UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()} will return
   * <code>false</code>.
   *
   * @param principal
   * @param credentials
   */
  public AuthorityInfo(Object principal, Object credentials) {
    super(principal, credentials);
    this.tokenString = null;
    this.refreshToken = null;
    this.idTokenString = null;
  }

  public AuthorityInfo(
      Object principal,
      Object credentials,
      final String tokenString,
      final String refreshToken,
      final String idTokenString) {
    super(principal, credentials);
    this.tokenString = tokenString;
    this.refreshToken = refreshToken;
    this.idTokenString = idTokenString;
  }

  private String tokenString;
  private String refreshToken;
  private String idTokenString;
}

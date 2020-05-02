package com.example.springbootrediskeycloakdemo.context.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@JsonInclude
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class AuthorityInfo {

  @JsonCreator
  public AuthorityInfo(
      @JsonProperty("principal") final Object principal,
      @JsonProperty("credentials") final Object credentials,
      @JsonProperty("tokenString") final String tokenString,
      @JsonProperty("refreshToken") final String refreshToken,
      @JsonProperty("idTokenString") final String idTokenString) {
    this.principal = principal;
    this.credentials = credentials;
    this.tokenString = tokenString;
    this.refreshToken = refreshToken;
    this.idTokenString = idTokenString;
  }

  private final Object principal;
  private Object credentials;
  private String tokenString;
  private String refreshToken;
  private String idTokenString;
}

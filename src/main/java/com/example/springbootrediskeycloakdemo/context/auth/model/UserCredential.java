package com.example.springbootrediskeycloakdemo.context.auth.model;

import lombok.Builder;
import lombok.Value;
import org.keycloak.representations.AccessTokenResponse;

@Value
@Builder
public class UserCredential {
  private String tokenString;
  private String refreshToken;
  private String idTokenString;

  public static UserCredential convertToTokenResponse(AccessTokenResponse tokenResponse) {
    return UserCredential.builder()
        .tokenString(tokenResponse.getToken())
        .idTokenString(tokenResponse.getIdToken())
        .refreshToken(tokenResponse.getRefreshToken())
        .build();
  }

  public static UserCredential convertToAuthorityInfo(AuthorityInfo authorityInfo) {
    return UserCredential.builder()
        .tokenString(authorityInfo.getTokenString())
        .idTokenString(authorityInfo.getIdTokenString())
        .refreshToken(authorityInfo.getRefreshTokenString())
        .build();
  }


}

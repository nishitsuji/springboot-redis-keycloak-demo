package com.example.springbootrediskeycloakdemo.context.auth.model;

import com.example.springbootrediskeycloakdemo.settings.security.auth.SecurityConstants;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/** アクセス情報 */
@EqualsAndHashCode(callSuper = false)
@Value
public class AuthInfo extends UsernamePasswordAuthenticationToken {

  /** 企業ID */
  private String clientId;

  public AuthInfo(final Object principal, final Object credentials, final String clientId) {
    super(principal, credentials);
    this.clientId = clientId;
  }

  @SneakyThrows
  // TODO:: リファクタリングも考える場所
  public MultiValueMap getQueryParamMap(AuthInfo info) {

    Map<String, Object> param = new HashMap<>();
    param.put(SecurityConstants.GRANT_TYPE, "password");
    param.put(SecurityConstants.CLIENT_ID, getClientId());
    param.put(SecurityConstants.LOGIN_ID, getPrincipal());
    param.put(SecurityConstants.PASSWORD, getCredentials());

    MultiValueMap content = new LinkedMultiValueMap<String, String>();
    content.setAll(param);
    return content;
  }
}

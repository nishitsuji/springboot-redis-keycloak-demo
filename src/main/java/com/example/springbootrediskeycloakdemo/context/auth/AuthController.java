package com.example.springbootrediskeycloakdemo.context.auth;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.model.UserCredential;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
import com.example.springbootrediskeycloakdemo.context.auth.service.KeycloakContextService;
import com.example.springbootrediskeycloakdemo.settings.security.auth.extractor.AuthInfoExtractor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/keycloak")
@RequiredArgsConstructor
public class AuthController {

  private final String ANONYMOUS = "anonymousUser";

  @Autowired private AccessGrantService accessGrantService;
  @Autowired private KeycloakContextService keycloakContextService;

  @SneakyThrows
  @RequestMapping(path = "/auth")
  @PostMapping
  @GetMapping // 検証用のため、GETも許可している
  public Map<String, String> auth(
      final HttpServletRequest request, final HttpServletResponse response) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (!HttpMethod.POST.matches(request.getMethod())) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    if (Objects.isNull(auth)) {
      throw new AuthenticationServiceException("Authentication is null");
    }

    if (ObjectUtils.notEqual(ANONYMOUS, auth.getPrincipal())) {
      final SimpleKeycloakAccount account = simpleKeycloakAccount(auth);
      // 認可済み
      return new HashMap<String, String>() {
        {
          put("token", account.getKeycloakSecurityContext().getTokenString());
        }
      };
    }

    final KeycloakDeployment deployment =
        keycloakContextService.resolveDeployment(request, response, "", false);

    final Optional<AuthInfo> authInfoOpt = Optional.ofNullable(AuthInfoExtractor.extract(request));
    if (!authInfoOpt.isPresent()) return null;

    final AuthInfo authInfo = authInfoOpt.get();

    final AccessTokenResponse tokenResponse = accessGrantService.login(deployment, authInfo);
    UserCredential credential = UserCredential.convertToTokenResponse(tokenResponse);
    final Authentication authentication =
        keycloakContextService.convertUserCredential(deployment, credential);
    final SimpleKeycloakAccount account = simpleKeycloakAccount(authentication);
    accessGrantService.entryKeycloakAccountForRedis(account, authInfo);

    // JWT Tokenを返却する
    return new HashMap<String, String>() {
      {
        put("token", account.getKeycloakSecurityContext().getTokenString());
      }
    };
  }

  private SimpleKeycloakAccount simpleKeycloakAccount(final Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return (SimpleKeycloakAccount) authentication.getDetails();
  }
}

package com.example.springbootrediskeycloakdemo.context.auth;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import com.example.springbootrediskeycloakdemo.context.auth.service.AccessGrantService;
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
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/keycloak/client")
@RequiredArgsConstructor
public class ClientController {

  private final String ANONYMOUS = "anonymousUser";

  private final AccessGrantService accessGrantService;
  private final AdapterDeploymentContext context;

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
      throw new AuthenticationServiceException(
          "Authentication is null");
    }

    if (ObjectUtils.notEqual(ANONYMOUS, auth.getPrincipal())) {
      return new HashMap<String, String>() {
        {
          put("token", "認証済みのtoken");
        }
      };
    }

    final HttpFacade facade = new SimpleHttpFacade(request, response);
    final KeycloakDeployment deployment = context.resolveDeployment(facade);
    if (Objects.isNull(deployment)) return null;

    final Optional<AuthInfo> authInfoOpt = Optional.ofNullable(AuthInfoExtractor.extract(request));
    if (!authInfoOpt.isPresent()) return null;

    final AuthInfo authInfo = authInfoOpt.get();

    Authentication authentication = accessGrantService.login(deployment, authInfo);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    SimpleKeycloakAccount account = (SimpleKeycloakAccount) authentication.getDetails();

    // JWT Tokenを返却する
    return new HashMap<String, String>() {
      {
        put("token", account.getKeycloakSecurityContext().getTokenString());
      }
    };
  }
}
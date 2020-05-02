package com.example.springbootrediskeycloakdemo.settings.security.auth.filter;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * 匿名認証を行うフィルター
 * @deprecated
 */
public class AnonymousAuthenticationProcessingFilter extends AnonymousAuthenticationFilter {

  /**
   * Creates a filter with a principal named "anonymousUser" and the single authority
   * "ROLE_ANONYMOUS".
   *
   * @param key the key to identify tokens created by this filter
   */
  public AnonymousAuthenticationProcessingFilter(String key) {
    super(key);
  }

  /**
   * @param key         key the key to identify tokens created by this filter
   * @param principal   the principal which will be used to represent anonymous users
   * @param authorities the authority list for anonymous users
   */
  public AnonymousAuthenticationProcessingFilter(String key, Object principal,
      List<GrantedAuthority> authorities) {
    super(key, principal, authorities);
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {


    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      SecurityContextHolder.getContext().setAuthentication(
          createAuthentication((HttpServletRequest) req));

      if (logger.isDebugEnabled()) {
        logger.debug("Populated SecurityContextHolder with anonymous token: '"
            + SecurityContextHolder.getContext().getAuthentication() + "'");
      }
    }
    else {
      if (logger.isDebugEnabled()) {
        logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
            + SecurityContextHolder.getContext().getAuthentication() + "'");
      }
    }

    chain.doFilter(req, res);
  }
}

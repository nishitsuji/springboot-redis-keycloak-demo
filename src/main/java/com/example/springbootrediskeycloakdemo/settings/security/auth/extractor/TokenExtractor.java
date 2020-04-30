package com.example.springbootrediskeycloakdemo.settings.security.auth.extractor;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class TokenExtractor {

  /** 対象スキーマ. */
  private static final String TOKEN_PREFIX = "Bearer";

  /**
   * トークン値の抽出を行う.
   *
   * @param request HTTPリクエスト
   * @return 値が抽出できた場合：抽出された値、値が抽出できなかった場合：空文字
   */
  public static String extract(final HttpServletRequest request) {

    final String value = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isEmpty(value)) {
      return "";
    }

    if (!value.startsWith(TOKEN_PREFIX)) {

      return "";
    }

    final String splitValue = value.substring(TOKEN_PREFIX.length());

    return splitValue.trim();
  }
}

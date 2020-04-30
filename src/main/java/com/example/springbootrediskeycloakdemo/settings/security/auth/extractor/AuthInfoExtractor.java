package com.example.springbootrediskeycloakdemo.settings.security.auth.extractor;

import com.example.springbootrediskeycloakdemo.context.auth.model.AuthInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

public class AuthInfoExtractor {

  public static AuthInfo extract(final HttpServletRequest request)
      throws InvocationTargetException, IllegalAccessException {

    RequestParameter context = RequestParameter.copy(request.getParameterMap());
    if (Objects.isNull(context)) return null;
    return new AuthInfo(context.userId, context.password, context.clientId);
  }

  public static AuthInfo extract(final ServletRequest request)
      throws InvocationTargetException, IllegalAccessException {

    RequestParameter context = RequestParameter.copy(request.getParameterMap());
    if (Objects.isNull(context)) return null;
    return new AuthInfo(context.userId, context.password, context.clientId);
  }

  @Data
  public static class RequestParameter {

    private String clientId;

    private String userId;

    private String password;

    public static RequestParameter copy(Object obj)
        throws InvocationTargetException, IllegalAccessException {
      if (Objects.isNull(obj)) return null;

      RequestParameter content = new RequestParameter();
      BeanUtils.copyProperties(content, obj);
      return content;
    }
  }
}

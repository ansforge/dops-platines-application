/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** The Class CustomBasicAuthenticationEntryPoint. */
@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.security.web.authentication.www.
   * BasicAuthenticationEntryPoint#commence(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * org.springframework.security.core.AuthenticationException)
   */
  @Override
  public void commence(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException authException)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.addHeader("Access-Control-Allow-Origin", "*");
    String message;
    if (authException.getCause() != null) {
      message = authException.getCause().getMessage();
    } else {
      message = authException.getMessage();
    }
    final byte[] body =
        new ObjectMapper().writeValueAsBytes(Collections.singletonMap("error", message));
    response.getOutputStream().write(body);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.security.web.authentication.www.
   * BasicAuthenticationEntryPoint#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() {
    setRealmName("MY_TEST_REALM");
    super.afterPropertiesSet();
  }
}

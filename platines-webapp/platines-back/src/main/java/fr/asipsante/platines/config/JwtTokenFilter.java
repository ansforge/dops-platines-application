/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import fr.asipsante.platines.service.authentication.JwtTokenProvider;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author apierre
 */
public class JwtTokenFilter extends GenericFilterBean {

  /** Logger. */
  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);

  /** jwt token provider. */
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * Constructeur.
   *
   * @param jwtTokenProvider Json web token provider
   */
  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
      throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;
    final String token = jwtTokenProvider.resolveToken(request);
    try {
      if (token != null) {
        final String newToken = jwtTokenProvider.validateToken(token);

        final Authentication auth = jwtTokenProvider.getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(auth);

        response.addHeader("access-control-expose-headers", "Authorization");
        response.addHeader("Authorization", newToken);
      }
      filterChain.doFilter(request, response);
    } catch (final Exception e) {
      LOG.info("Token {} invalide", token);
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
    }
  }
}

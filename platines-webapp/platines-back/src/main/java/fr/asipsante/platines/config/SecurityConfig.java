/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import fr.asipsante.platines.service.authentication.CustomBasicAuthenticationEntryPoint;
import fr.asipsante.platines.service.authentication.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author aboittiaux
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /** The Constant FORM_BASED_LOGIN_ENTRY_POINT. */
  public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/insecure/**";

  /** The Constant TOKEN_BASED_AUTH_ENTRY_POINT. */
  public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/secure/**";

  /** token provider. */
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(getBasicAuthEntryPoint())
        .and()
        .authorizeRequests()
        .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
        .permitAll()
        .antMatchers(org.springframework.http.HttpMethod.GET, "/techdata/public/**")
        .permitAll()
        .antMatchers(FORM_BASED_LOGIN_ENTRY_POINT)
        .permitAll()
        .antMatchers("/session/**")
        .permitAll()
        .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT)
        .authenticated()
        .and()
        .apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
  }

  /**
   * Gets the basic auth entry point.
   *
   * @return the basic auth entry point
   */
  @Bean
  public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint() {
    return new CustomBasicAuthenticationEntryPoint();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.security.config.annotation.web
   * .configuration.WebSecurityConfigurerAdapter#configure(org. springframework
   * .security. config.annotation.web.builders.WebSecurity)
   */
  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/");
  }
}

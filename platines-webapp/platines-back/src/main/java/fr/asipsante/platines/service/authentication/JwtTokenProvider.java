/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.authentication;

import fr.asipsante.platines.entity.Profile;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.entity.UserFamily;
import fr.asipsante.platines.security.CustomUser;
import fr.asipsante.platines.service.PreferencesService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Provider utilisé pour la création de la validation des tokens.
 *
 * @author aboittiaux
 */
@Component
public class JwtTokenProvider {

  /** LOGGER. */
  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

  /**
   * Random pour représentant la clé pour signer le token. Ce random est généré à chaque déploiement
   * de l'application.
   */
  public static final String SECRET = RandomStringUtils.randomAlphanumeric(20);

  /** Durée de validitée du token. */
  private static final long VALIDITY_IN_MILLISECONDS = Duration.ofMinutes(20).toMillis();

  /** multiplicateur de minute à milliseconde. */
  private static final long MINUTE_TO_MILLISECONDS = Duration.ofMinutes(1).toMillis();

  /** Propriété représentant la durée de validité du token. */
  private static final String TOKEN_DURATION = "token_duration";

  /** Taille de la String Bearer renvoyée avec le token du côté frontend. */
  private static final int SIZE_BEARER = 7;

  /** Service des preferences. */
  @Autowired private PreferencesService preferencesService;

  /**
   * Méthode de création du token.
   *
   * @param user user
   * @param roles roles
   * @param duration duration
   * @return token String
   */
  public String createToken(User user, List<Profile> roles, Long duration) {

    final Claims claims = Jwts.claims().setSubject(user.getMail());

    final List<SimpleGrantedAuthority> auth =
        roles.stream()
            .map(profile -> new SimpleGrantedAuthority(profile.getLabel()))
            .collect(Collectors.toList());
    claims.put("auth", auth);

    final List<Long> themes =
        user.getFamilies().stream().map(UserFamily::getTheme).collect(Collectors.toList()).stream()
            .map(Theme::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    claims.put("theme", themes);

    claims.put("profil", user.getProfile().getLabel());

    final Date now = new Date();
    Date validity;
    if (duration != null) {
      validity = new Date(now.getTime() + duration);
    } else {
      final String tokenDuration = preferencesService.getPreferences().getProperty(TOKEN_DURATION);
      if (tokenDuration != null) {
        validity =
            new Date(now.getTime() + (Long.parseLong(tokenDuration) * MINUTE_TO_MILLISECONDS));
      } else {
        validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);
      }
    }

    return Jwts.builder() //
        .setClaims(claims) //
        .setIssuer(user.getForename() + " " + user.getName())
        .setIssuedAt(now) //
        .setExpiration(validity) //
        .signWith(SignatureAlgorithm.HS256, SECRET) //
        .compact();
  }

  /**
   * Méthode permettant de récupérer l'Authentication à partir du token.
   *
   * @param token token
   * @return Authentication
   */
  public Authentication getAuthentication(String token) {

    if (token.startsWith("Bearer ")) {
      token = token.substring(SIZE_BEARER);
    }

    final UserDetails userDetails =
        CustomUser.withMyUsername(getUsername(token)) //
            .profil(getProfile(token))
            .families(getFamilies(token)) //
            .password("") //
            .roles(getProfile(token)) //
            .accountExpired(false) //
            .accountLocked(false) //
            .credentialsExpired(false) //
            .disabled(false) //
            .build();

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  /**
   * Récupération du nom de l'utilisateur dans le token.
   *
   * @param token le token
   * @return le nom de l'utilisateur
   */
  public String getUsername(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(SIZE_BEARER);
    }
    return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Récupération des thèmes de l'utilisateur dans le token.
   *
   * @param token le token
   * @return les thèmes de l'utilisateur
   */
  @SuppressWarnings("unchecked")
  public List<Long> getFamilies(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(SIZE_BEARER);
    }
    return (List<Long>)
        Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().get("theme");
  }

  /**
   * Récupération des roles de l'utilisateur dans le token.
   *
   * @param token le token
   * @return les roles de l'utilisateur
   */
  @SuppressWarnings("unchecked")
  public List<SimpleGrantedAuthority> getRoles(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(SIZE_BEARER);
    }
    return (List<SimpleGrantedAuthority>)
        Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().get("auth");
  }

  /**
   * Récupération du profil de l'utilisateur dans le token.
   *
   * @param token le token
   * @return le profil de l'utilisateur
   */
  public String getProfile(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(SIZE_BEARER);
    }
    return Jwts.parser()
        .setSigningKey(SECRET)
        .parseClaimsJws(token)
        .getBody()
        .get("profil")
        .toString();
  }

  /**
   * Méthode de résolution du token.
   *
   * @param req HttpServletRequest
   * @return token String
   */
  public String resolveToken(HttpServletRequest req) {
    final String bearerToken = req.getHeader("Authorization");
    String token = null;
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      token = bearerToken.substring(SIZE_BEARER);
    }
    return token;
  }

  /**
   * Méthode de validation du token.
   *
   * @param token token
   * @return newToken
   */
  public String validateToken(String token) {
    final Date now = new Date();
    final String tokenDuration = preferencesService.getPreferences().getProperty(TOKEN_DURATION);
    final Date validity;
    if (tokenDuration != null) {
      validity = new Date(now.getTime() + (Long.parseLong(tokenDuration) * MINUTE_TO_MILLISECONDS));
    } else {
      validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);
    }

    final Claims claims =
        Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .setIssuedAt(now)
            .setExpiration(validity);
    return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, SECRET).compact();
  }
}

/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.authentication;

import fr.asipsante.platines.dao.IUserDao;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.entity.UserFamily;
import fr.asipsante.platines.security.CustomUser;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author apierre
 */
@Service
public class MyUserDetails implements UserDetailsService {

  /** user dao. */
  @Autowired
  @Qualifier("userRefactoringDao")
  private IUserDao userRepository;

  /** constructeur. */
  public MyUserDetails() {
    super();
  }

  /**
   * récupère les détails d'un utilisateur par son nom.
   *
   * @param username, le nom de l'utilisateur
   * @return les détails de l'utilisateur
   */
  @Transactional
  @Override
  public UserDetails loadUserByUsername(String username) {
    final User user = userRepository.getUserByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    return CustomUser.withMyUsername(username)
        .profil(user.getProfile().getLabel())
        .families(
            user.getFamilies().stream()
                .map(UserFamily::getTheme)
                .collect(Collectors.toList())
                .stream()
                .map(Theme::getId)
                .collect(Collectors.toList()))
        .password(user.getPassword())
        .roles(user.getProfile().getLabel())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}

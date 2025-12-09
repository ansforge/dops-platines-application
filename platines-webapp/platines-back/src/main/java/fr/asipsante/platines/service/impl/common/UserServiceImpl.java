/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IApplicationDao;
import fr.asipsante.platines.dao.IGenericDao;
import fr.asipsante.platines.dao.IThemeDao;
import fr.asipsante.platines.dao.IUserDao;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.UserProfile;
import fr.asipsante.platines.entity.Application;
import fr.asipsante.platines.entity.Profile;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.entity.UserFamily;
import fr.asipsante.platines.exception.DatabaseException;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.mail.entity.Mail;
import fr.asipsante.platines.service.MailService;
import fr.asipsante.platines.service.UserService;
import fr.asipsante.platines.service.authentication.JwtTokenProvider;
import fr.asipsante.platines.service.mapper.UserDtoMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * User service implementation.
 *
 * @author apierre
 */
@Service(value = "userServiceRefactoring")
public class UserServiceImpl implements UserService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  /** SUBJECT_CREATE_PASSWORD. */
  private static final String SUBJECT_CREATE_PASSWORD = "Platines: Création de mot de passe";

  /** EXPIRATION_TIME_CREATE. */
  private static final long EXPIRATION_TIME_CREATE = 14_400_000;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("userDtoMapper")
  private UserDtoMapper userDtoMapper;

  /** IMailService. */
  @Autowired private MailService mailService;

  /** JwtTokenProvider. */
  @Autowired private JwtTokenProvider jwtTokenProvider;

  /** IApplicationDao. */
  private IApplicationDao iApplicationDao;

  /** IUserDao. */
  private IUserDao userDao;

  /** IThemeDao. */
  private IThemeDao themeDao;

  @PersistenceContext private EntityManager em;

  /** Constructeur par défaut. */
  public UserServiceImpl() {}

  /**
   * Constructeur.
   *
   * @param genericDao, the generic dao
   * @param applicationDao, the application dao
   * @param familyDao, the family dao
   */
  @Autowired
  public UserServiceImpl(
      @Qualifier("userRefactoringDao") IGenericDao<User, Long> genericDao,
      @Qualifier("applicationDao") IGenericDao<Application, Long> applicationDao,
      IGenericDao<Theme, Long> familyDao) {
    this.userDao = (IUserDao) genericDao;
    this.iApplicationDao = (IApplicationDao) applicationDao;
    this.themeDao = (IThemeDao) familyDao;
  }

  @Transactional
  @Override
  public List<UserDto> getAllUsers() {
    final List<UserDto> usersDto = new ArrayList<>();
    final List<User> users = userDao.getAll();
    for (final User user : users) {
      usersDto.add(userDtoMapper.convertToUserDto(user));
    }
    return usersDto;
  }

  @Transactional
  @Override
  public List<UserDto> safeFetchAllUnassignedUsers() {
    final List<UserDto> usersDto = new ArrayList<>();
    final List<User> users = userDao.safeFetchAllUnassignedUsers();
    for (final User user : users) {
      usersDto.add(userDtoMapper.convertToUserDto(user));
    }
    return usersDto;
  }

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Transactional
  @Override
  public void createUser(UserDto userDto) {
    User userDb = null;
    try {
      userDb = userDao.getUserByEmail(userDto.getMail());
    } catch (final DatabaseException e) {
      LOGGER.info("L'utilisateur n'éxiste pas dans la base de donnée");
    }
    if (userDb == null) {
      final User user = userDtoMapper.convertToUser(userDto);

      final List<Profile> profiles = new ArrayList<>();
      profiles.add(user.getProfile());

      final String token = jwtTokenProvider.createToken(user, profiles, EXPIRATION_TIME_CREATE);
      final Mail mail = new Mail();
      mail.setMailTo(user.getMail());
      mail.setMailSubject(SUBJECT_CREATE_PASSWORD);
      mail.setTemplate("/velocity/mail/create_user.vm");
      final String url =
          System.getenv("EXTERNAL_PROTOCOL")
              + "://"
              + System.getenv("API_DOMAIN")
              + contextPath
              + "#/reset;id="
              + token;
      final Map<String, Object> model = new HashMap<>();
      model.put("url", url);
      model.put("firstName", HtmlUtils.htmlEscape(user.getForename()));
      mail.setVariables(model);

      mailService.sendEmail(mail);
      userDao.save(user);

      for (final UserFamily userFamily : user.getFamilies()) {
        em.persist(userFamily);
      }

    } else {
      throw new ServiceException("L'utilisateur existe déjà");
    }
  }

  @Transactional
  @Override
  public void updateUser(UserDto userDto) {
    final User user = userDao.getById(userDto.getId());

    for (final UserFamily userFamily : user.getFamilies()) {
      em.remove(userFamily);
    }

    user.setForename(userDto.getForename());
    user.setName(userDto.getName());
    user.setMail(userDto.getMail());
    user.setFirm(userDto.getFirm());

    final Profile profile = new Profile();
    profile.setId(userDto.getProfile().getId());

    user.getFamilies().clear();
    for (final ThemeDto familyDto : userDto.getFamilies()) {
      final Theme family = themeDao.getById(familyDto.getId());
      user.addFamily(family);
    }

    for (final UserFamily userFamily : user.getFamilies()) {
      em.persist(userFamily);
    }

    user.setProfile(profile);
    userDao.update(user);
  }

  @Transactional
  @Override
  public void updateUserFamilies(UserDto userDto) {
    final User user = userDao.safeFetchById(userDto.getId());

    for (final UserFamily userFamily : user.getFamilies()) {
      em.remove(userFamily);
    }

    user.getFamilies().clear();
    for (final ThemeDto familyDto : userDto.getFamilies()) {
      final Theme family = themeDao.safeFetchById(familyDto.getId());
      user.addFamily(family);
    }

    for (final UserFamily userFamily : user.getFamilies()) {
      em.persist(userFamily);
    }

    userDao.safeUpdate(user);
  }

  @Transactional
  @Override
  public void deleteUser(Long userId) {
    final User user = userDao.getById(userId);
    final List<Application> applications = iApplicationDao.getApplicationsByUser(userId);
    for (final Application application : applications) {
      iApplicationDao.delete(application);
    }
    for (final UserFamily userFamily : user.getFamilies()) {
      em.remove(userFamily);
    }
    userDao.delete(user);
  }

  @Transactional
  @Override
  public UserDto getUserByToken(String token) {
    final User user = userDao.getUserByEmail(jwtTokenProvider.getUsername(token));
    return userDtoMapper.convertToUserDto(user);
  }

  @Transactional
  @Override
  public UserDto getUserById(Long idUser) {
    return userDtoMapper.convertToUserDto(userDao.getById(idUser));
  }

  @Override
  @Transactional
  public void updateProfile(UserProfile user) {
    final User userupdate = userDao.getById(user.getUser().getId());
    if (userupdate != null) {
      userupdate.setFirm(user.getUser().getFirm());
      userupdate.setForename(user.getUser().getForename());
      userupdate.setName(user.getUser().getName());
    }
    if (user.getIdentification().getNewPassword() != null) {
      final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      if (passwordEncoder.matches(
          user.getIdentification().getLastPassword(), userupdate.getPassword())) {
        userupdate.setPassword(user.getIdentification().getNewPassword());
      } else {
        throw new ServiceException("Erreur de mot de passe");
      }
    }
  }

  @Override
  @Transactional
  public UserDto getUserByMail(String mail) {
    return userDtoMapper.convertToUserDto(userDao.getUserByEmail(mail));
  }
}

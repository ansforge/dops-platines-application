/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.security;

import fr.asipsante.platines.entity.Application;
import fr.asipsante.platines.entity.Certificate;
import fr.asipsante.platines.entity.ChainOfTrust;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.ProjectResult;
import fr.asipsante.platines.entity.ProjectResultProperty;
import fr.asipsante.platines.entity.Resource;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.TestCertificate;
import fr.asipsante.platines.entity.TestSession;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.entity.UserFamily;
import fr.asipsante.platines.entity.Version;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Custom Spring Security Expressions
 *
 * @author cnader
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {
  /**
   * Dérogation aux règles de nommage conventionnelles des loggers pour que cette classe obéisse à
   * <code>-Dlogging.level.org.springframework.security=DEBUG</code>
   */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(
          "org.springframework.security.custom.platines"
              + CustomMethodSecurityExpressionRoot.class.getSimpleName());

  private static final String ADMINISTRATOR = "admin";
  private static final String MANAGER = "manager";
  private static final String USER = "user";

  private Object filterObject;
  private Object returnObject;

  /**
   * Instantiates a new Custom method security expression root.
   *
   * @param authentication the authentication
   */
  public CustomMethodSecurityExpressionRoot(Authentication authentication) {
    super(authentication);
  }

  /**
   * Custom Expression For User Access
   *
   * @param object the object
   * @return the boolean
   */
  public boolean hasAccess(Object object) {
    LOGGER.debug("Evaluating 'hasAccess' on {}", object);
    if (ADMINISTRATOR.equals(((CustomUser) this.getPrincipal()).getProfil())) {
      LOGGER.debug("Administrator {} gains automatic access.", this.getPrincipal());
      return true;
    } else if (MANAGER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
      LOGGER.debug("Access automatically refused to Manager {}.", this.getPrincipal());
      return false;
    }
    return isAllowed(object);
  }

  /**
   * Custom Expression For Manager Access
   *
   * @param object the object
   * @return the boolean
   */
  public boolean isManaged(Object object) {
    if (ADMINISTRATOR.equals(((CustomUser) this.getPrincipal()).getProfil())) {
      return true;
    } else if (USER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
      return false;
    }
    return isAllowed(object);
  }

  private boolean isAllowed(Object object) {
    List<Long> objectFamilies = new ArrayList<>();

    LOGGER.debug("Checking access rules for User {} on object {}.", this.getPrincipal(), object);

    final List<Long> userFamilies = ((CustomUser) this.getPrincipal()).getFamilies();
    final List<String> userList = new ArrayList<>(userFamilies.size());
    // do not replace this with "enhanced" for-loop otherwise it causes a class cast exception (int
    // to Long)
    for (int i = 0; i < userFamilies.size(); i++) {
      userList.add(String.valueOf(userFamilies.get(i)));
    }

    if (object instanceof User) {
      objectFamilies =
          ((User) object)
              .getFamilies().stream()
                  .filter(Objects::nonNull)
                  .map(UserFamily::getTheme)
                  .collect(Collectors.toList())
                  .stream()
                  .map(Theme::getId)
                  .collect(Collectors.toList());

    } else if (object instanceof Application) {
      objectFamilies =
          ((Application) object)
              .getUser().getFamilies().stream()
                  .filter(Objects::nonNull)
                  .map(UserFamily::getTheme)
                  .collect(Collectors.toList())
                  .stream()
                  .map(Theme::getId)
                  .collect(Collectors.toList());

    } else if (object instanceof ChainOfTrust) {
      objectFamilies =
          ((ChainOfTrust) object)
              .getUser().getFamilies().stream()
                  .filter(Objects::nonNull)
                  .map(UserFamily::getTheme)
                  .collect(Collectors.toList())
                  .stream()
                  .map(Theme::getId)
                  .collect(Collectors.toList());

    } else if (object instanceof Certificate) {
      objectFamilies =
          ((Certificate) object)
              .getChainOfTrust().getUser().getFamilies().stream()
                  .filter(Objects::nonNull)
                  .map(UserFamily::getTheme)
                  .collect(Collectors.toList())
                  .stream()
                  .map(Theme::getId)
                  .collect(Collectors.toList());

    } else if (object instanceof Theme) {
      if (USER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
        if (((Theme) object).getVisibility()) {
          objectFamilies.add(((Theme) object).getId());
          LOGGER.debug("User {} gets to see visible Theme {}", this.getPrincipal(), object);
        }
      } else {
        objectFamilies.add(((Theme) object).getId());
        LOGGER.debug(
            "Admin/Manager {} automatically gets to see Theme {}", this.getPrincipal(), object);
      }

    } else if (object instanceof TestSession) {
      if (!ADMINISTRATOR.equals(
          ((TestSession) object).getApplication().getUser().getProfile().getLabel())) {
        if (USER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
          if (((TestSession) object).getVersion().getVisibility()
              && ((TestSession) object).getVersion().getService().getTheme().getVisibility()) {
            objectFamilies.add(((TestSession) object).getVersion().getService().getTheme().getId());
          }
        } else {
          objectFamilies.add(((TestSession) object).getVersion().getService().getTheme().getId());
        }
      }

    } else if (object instanceof Version) {
      if (USER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
        if (((Version) object).getVisibility()
            && ((Version) object).getService().getTheme().getVisibility()) {
          objectFamilies.add(((Version) object).getService().getTheme().getId());
        }
      } else {
        objectFamilies.add(((Version) object).getService().getTheme().getId());
      }

    } else if (object instanceof SimulatedService) {
      objectFamilies.add(((SimulatedService) object).getTheme().getId());

    } else if (object instanceof Project) {
      if (USER.equals(((CustomUser) this.getPrincipal()).getProfil())) {
        if (((Project) object).getVisibility()) {
          objectFamilies =
              ((Project) object)
                  .getVersions().stream()
                      .map(Version::getService)
                      .collect(Collectors.toList())
                      .stream()
                      .map(SimulatedService::getTheme)
                      .collect(Collectors.toList())
                      .stream()
                      .map(Theme::getId)
                      .collect(Collectors.toList());
        }
      } else {
        objectFamilies =
            ((Project) object)
                .getVersions().stream()
                    .map(Version::getService)
                    .collect(Collectors.toList())
                    .stream()
                    .map(SimulatedService::getTheme)
                    .collect(Collectors.toList())
                    .stream()
                    .map(Theme::getId)
                    .collect(Collectors.toList());
      }

    } else if (object instanceof ProjectResult) {
      if (!ADMINISTRATOR.equals(
          ((ProjectResult) object)
              .getTestSession()
              .getApplication()
              .getUser()
              .getProfile()
              .getLabel())) {
        objectFamilies.add(
            ((ProjectResult) object).getTestSession().getVersion().getService().getTheme().getId());
      }

    } else if (object instanceof ProjectResultProperty) {
      if (!ADMINISTRATOR.equals(
          ((ProjectResultProperty) object)
              .getProjectResult()
              .getTestSession()
              .getApplication()
              .getUser()
              .getProfile()
              .getLabel())) {
        objectFamilies.add(
            ((ProjectResultProperty) object)
                .getProjectResult()
                .getTestSession()
                .getVersion()
                .getService()
                .getTheme()
                .getId());
      }

    } else if (object instanceof Resource) {
      objectFamilies.add(((Resource) object).getTheme().getId());

    } else if (object instanceof TestCertificate) {
      return ((TestCertificate) object).isDownloadable()
          || MANAGER.equals(((CustomUser) this.getPrincipal()).getProfil());

    } else {
      return false;
    }
    final List<String> objectList = new ArrayList<>(objectFamilies.size());
    for (final Long id : objectFamilies) {
      objectList.add(String.valueOf(id));
    }
    LOGGER.debug("Matching object Themes {} with user Themes {}.", objectList, userList);
    objectList.retainAll(userList);
    return !objectList.isEmpty();
  }

  /**
   * Is not managed boolean.
   *
   * @param object the object
   * @return the boolean
   */
  public boolean isNotManaged(Object object) {

    if (object instanceof User) {
      return ((User) object)
              .getFamilies().stream()
                  .filter(Objects::nonNull)
                  .map(UserFamily::getTheme)
                  .collect(Collectors.toList())
                  .stream()
                  .map(Theme::getId)
                  .count()
          == 0;
    } else {
      return false;
    }
  }

  /**
   * Gets filter object.
   *
   * @return the filter object
   */
  @Override
  public Object getFilterObject() {
    return this.filterObject;
  }

  /**
   * Gets return object.
   *
   * @return the return object
   */
  @Override
  public Object getReturnObject() {
    return this.returnObject;
  }

  /**
   * Gets this.
   *
   * @return the this
   */
  @Override
  public Object getThis() {
    return this;
  }

  /**
   * Sets filter object.
   *
   * @param obj the obj
   */
  @Override
  public void setFilterObject(Object obj) {
    this.filterObject = obj;
  }

  /**
   * Sets return object.
   *
   * @param obj the obj
   */
  @Override
  public void setReturnObject(Object obj) {
    this.returnObject = obj;
  }
}

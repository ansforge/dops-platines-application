/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import fr.asipsante.platines.security.CustomMethodSecurityExpressionHandler;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author apierre
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclSecurityConfig extends GlobalMethodSecurityConfiguration {

  /** datasource. */
  @Autowired DataSource dataSource;

  @Autowired CacheManager cacheManager;

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    final CustomMethodSecurityExpressionHandler expressionHandler =
        new CustomMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService()));
    expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
    return expressionHandler;
  }

  /**
   * acl authorization strategy.
   *
   * @return acl authorization strategy
   */
  @Bean
  public AclAuthorizationStrategy aclAuthorizationStrategy() {
    return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_admin"));
  }

  /**
   * Permission granting strategy.
   *
   * @return Permission granting strategy
   */
  @Bean
  public PermissionGrantingStrategy permissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
  }

  /**
   * lookup strategy.
   *
   * @return lookup strategy
   */
  @Bean
  public LookupStrategy lookupStrategy() {
    return new BasicLookupStrategy(
        dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
  }

  /**
   * acl service.
   *
   * @return jdbc mutable acl service
   */
  @Bean
  public JdbcMutableAclService aclService() {
    final JdbcMutableAclService service =
        new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    service.setClassIdentityQuery("SELECT @@IDENTITY");
    service.setSidIdentityQuery("SELECT @@IDENTITY");
    return service;
  }

  /**
   * acl cache.
   *
   * @return acl cache
   */
  @Bean
  public AclCache aclCache() {
    return new SpringCacheBasedAclCache(
        cacheManager.getCache("aclCache"),
        permissionGrantingStrategy(),
        aclAuthorizationStrategy());
  }

  /**
   * acl cache factory bean.
   *
   * @return acl cache factory bean
   */
  @Bean
  public EhCacheFactoryBean aclEhCacheFactoryBean() {
    final EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
    ehCacheFactoryBean.setCacheManager(Objects.requireNonNull(aclCacheManager().getObject()));
    ehCacheFactoryBean.setCacheName("aclCache");
    return ehCacheFactoryBean;
  }

  /**
   * acl cache manager.
   *
   * @return cache manager factory
   */
  @Bean
  public EhCacheManagerFactoryBean aclCacheManager() {
    EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    cacheManagerFactoryBean.setShared(true);
    return cacheManagerFactoryBean;
  }
}

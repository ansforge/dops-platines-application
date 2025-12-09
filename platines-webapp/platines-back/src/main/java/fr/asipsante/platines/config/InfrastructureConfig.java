/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author apierre
 */
@Configuration
@EnableTransactionManagement
public class InfrastructureConfig {

  /** MODEL_PACKAGE : package entity. */
  private static final String MODEL_PACKAGE = "fr.asipsante.platines.entity";

  /** boolean string pour les paramètres de configuration de base de donnes. */
  private static final String FALSE = "false";

  /** environment. */
  @Autowired Environment env;

  /** datasource. */
  @Autowired private DataSource dataSource;

  /**
   * transaction manager.
   *
   * @param emf, entity manager factory
   * @return transaction manager
   */
  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }

  /**
   * Entity manager factory.
   *
   * @return entity manager
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan(MODEL_PACKAGE);

    final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(getHibernateProperties());

    return em;
  }

  /**
   * liquibase.
   *
   * @return liquibase
   */
  @Bean
  public SpringLiquibase liquibase() {
    final SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.xml");

    return liquibase;
  }

  /**
   * hibernate properties.
   *
   * @return properties
   */
  private Properties getHibernateProperties() {
    final Properties properties = new Properties();
    properties.setProperty(
        "hibernate.hbm2ddl.auto",
        env.getProperty("api.database.hibernate.hbm2ddl.auto.api", "none"));

    final String dialect =
        env.getProperty(
            "api.database.hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    properties.setProperty("hibernate.dialect", dialect);

    properties.setProperty(
        "hibernate.generate_statistics",
        env.getProperty("api.database.hibernate.generate_statistics", FALSE));
    properties.setProperty(
        "hibernate.show_sql", env.getProperty("api.database.hibernate.show_sql", FALSE));
    properties.setProperty(
        "hibernate.format_sql", env.getProperty("api.database.hibernate.format_sql", FALSE));
    properties.setProperty(
        "hibernate.use_sql_comments",
        env.getProperty("api.database.hibernate.use_sql_comments", FALSE));
    return properties;
  }
}

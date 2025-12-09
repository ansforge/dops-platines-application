/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author aboittiaux
 */
@Configuration
@ComponentScan(basePackages = {"fr.asipsante.platines.service"})
@EnableAspectJAutoProxy
public class ServiceConfig {

  /** SMTP default port. */
  private static final int SMTP_DEFAULT_PORT = 587;

  /**
   * Mail sender.
   *
   * @return java mail sender
   */
  //    @Bean
  //    public JavaMailSender getMailSender() {
  //        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
  //        if (System.getenv("SMTP_HOST") != null) {
  //            mailSender.setHost(System.getenv("SMTP_HOST"));
  //            mailSender.setPort(Integer.parseInt(System.getenv("SMTP_PORT")));
  //        } else {
  //            mailSender.setHost("smtp.gmail.com");
  //            mailSender.setPort(SMTP_DEFAULT_PORT);
  //        }
  //
  //        final Properties javaMailProperties = new Properties();
  //        javaMailProperties.put("mail.transport.protocol", "smtp");
  //        mailSender.setJavaMailProperties(javaMailProperties);
  //        return mailSender;
  //    }

  /**
   * model mapper.
   *
   * @return the model mapper
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  /**
   * velocity.
   *
   * @return velocity engine
   */
  @Bean
  public VelocityEngine getVelocity() {
    final Properties props = new Properties();
    props.put("resource.loader", "class");
    props.put(
        "class.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    return new VelocityEngine(props);
  }

  /**
   * multipart resolver.
   *
   * @return multipart resolver
   */
  @Bean
  public MultipartResolver multipartResolver() {
    return new CommonsMultipartResolver();
  }
}

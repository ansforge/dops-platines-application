/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** The Class AsynchronousConfiguration. */
@Configuration
@Slf4j
public class AsynchronousConfiguration {

  /**
   * Work executor.
   *
   * @return the task executor
   */
  @Bean(name = "taskExecutor")
  public TaskExecutor workExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setThreadNamePrefix("Async-");
    threadPoolTaskExecutor.setCorePoolSize(3);
    threadPoolTaskExecutor.setMaxPoolSize(50);
    threadPoolTaskExecutor.setQueueCapacity(600);
    threadPoolTaskExecutor.afterPropertiesSet();
    log.info("ThreadPoolTaskExecutor set");
    return threadPoolTaskExecutor;
  }
}

/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author aboittiaux
 */
@Configuration
@ComponentScan(basePackages = {"fr.asipsante.platines.dao"})
public class RepositoryConfig {}

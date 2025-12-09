/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

/**
 * Service pour publier des données techniques sur platines. Créé pour afficher la version
 * applicative.
 *
 * @author ericdegenetais
 */
public interface PlatinesTechDataService {
  String getWebappVersion();
}

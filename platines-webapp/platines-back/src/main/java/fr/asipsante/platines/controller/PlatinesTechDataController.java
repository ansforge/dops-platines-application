/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.service.PlatinesTechDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour servir les données techniques sur l'application. Créé pour afficher la version de
 * l'applicatif.
 *
 * @author edegenetais
 */
@RequestMapping("/techdata")
@RestController
public class PlatinesTechDataController {

  @Autowired private PlatinesTechDataService techDataService;

  @GetMapping(value = "public/version", produces = "application/json")
  public String getWebappVersion() {
    /** Une chaîne correctement quotée est un Json valide mais ce @#! webmvc ne le sait pas */
    return '"' + techDataService.getWebappVersion() + '"';
  }
}

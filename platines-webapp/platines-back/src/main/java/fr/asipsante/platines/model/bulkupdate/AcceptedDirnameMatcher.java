/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author edegenetais
 */
class AcceptedDirnameMatcher implements Predicate<String> {
  private static final String ACCEPTED_TERMINATION = ".XML";

  private List<String> acceptedDirnames;

  public AcceptedDirnameMatcher(List<String> projectNames) {
    this.acceptedDirnames =
        projectNames.stream()
            .map(name -> name.toUpperCase())
            .filter(
                name ->
                    name.endsWith(
                        ACCEPTED_TERMINATION)) /* en cours de validation, la liste pouirrait contenir des fichiers qui ne sont pas des projets,
                                               d'où ce filtre*/
            .map(name -> name.substring(0, name.lastIndexOf(ACCEPTED_TERMINATION)))
            .collect(Collectors.toList());
  }

  @Override
  public boolean test(String t) {
    final String sanitizedDirnameString = t.toUpperCase().split("/")[0];
    return !acceptedDirnames.contains(sanitizedDirnameString);
  }
}

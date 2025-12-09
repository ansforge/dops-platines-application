/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.testutils;

import java.util.List;
import java.util.Map;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
import org.slf4j.LoggerFactory;

/**
 * Par défaut, dbunit analyse le schéma pour trouver les PK, mais comme nous avons quelques tables
 * sans ID, il est nécessaire de définir les PK avec ce filtre.
 *
 * @author edegenetais
 */
public class PrimaryKeyFilter implements IColumnFilter {

  /** Les tables qui font exception à la règle. */
  private static final Map<String, List<String>> PRIMARY_KEY_MAPPING =
      Map.ofEntries(
          Map.entry("VERSION_PROJECT", List.of("PROJECT_ID", "VERSION_ID")),
          Map.entry("USER_FAMILLE", List.of("USER_ID", "FAMILY_ID")));

  public PrimaryKeyFilter() {
    LoggerFactory.getLogger(PrimaryKeyFilter.class).info("{} instanciée.", getClass().getName());
  }

  @Override
  public boolean accept(String tableName, Column column) {
    LoggerFactory.getLogger(PrimaryKeyFilter.class)
        .info("{} utilisée: {}.{}", getClass().getName(), tableName, column.getColumnName());
    if (PRIMARY_KEY_MAPPING.containsKey(tableName)) {
      return PRIMARY_KEY_MAPPING.get(tableName).contains(column.getColumnName());
    } else {
      /** Par défaut les tables ont une unique de PK qui s'appelle ID. */
      return "ID".equals(column.getColumnName());
    }
  }
}

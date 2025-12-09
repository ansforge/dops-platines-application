/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.RelatedFiles;

/**
 * Interface provides methods required to link {@link RelatedFiles} entity to a data source.
 *
 * @author apierre
 */
public interface IRelatedFiles extends IGenericDao<RelatedFiles, Long> {

  /**
   * Gets a document by its id.
   *
   * @param id, the document id
   * @return the document
   */
  RelatedFiles getDocumentById(Long id);
}

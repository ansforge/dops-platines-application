/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IRelatedFiles;
import fr.asipsante.platines.entity.RelatedFiles;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "relatedFilesDao")
public class RelatedFilesDaoImpl extends GenericDaoImpl<RelatedFiles, Long>
    implements IRelatedFiles {

  @Override
  public RelatedFiles getDocumentById(Long id) {
    return entityManager.find(RelatedFiles.class, id);
  }
}

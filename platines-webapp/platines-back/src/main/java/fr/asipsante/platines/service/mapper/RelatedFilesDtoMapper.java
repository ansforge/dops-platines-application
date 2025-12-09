/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.entity.RelatedFiles;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "relatedFilesDtoMapper")
public class RelatedFilesDtoMapper extends GenericDtoMapper<RelatedFiles, RelatedFilesDto> {

  /**
   * Converts a {@link RelatedFiles} into a {@link RelatedFilesDto}.
   *
   * @param relatedFile, the {@link RelatedFiles} to convert
   * @return a {@link RelatedFilesDto}
   */
  public RelatedFilesDto convertToRelatedFileDto(RelatedFiles relatedFile) {
    final RelatedFilesDto relatedFilesDto = new RelatedFilesDto();
    relatedFilesDto.setId(relatedFile.getId());
    relatedFilesDto.setFileName(relatedFile.getFileName());
    relatedFilesDto.setFileType(relatedFile.getFileType());
    if (relatedFile.getFile() != null) {
      relatedFilesDto.setFile(relatedFile.getFile());
    }
    relatedFilesDto.setDateUpload(relatedFile.getDateUpload());
    return relatedFilesDto;
  }
}

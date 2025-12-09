/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

import fr.asipsante.platines.testutils.ResourceTestHelper;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author edegenetais
 */
public class BulkProjectUpdateArchiveTest {
  /**
   * Les fichiers ci-dessous doivent être identiques à ceux contenus dans le zip, sinon le test
   * échoue (dans ce cas reconstruire le zip)
   */
  private static final String NOM_ENTREE_1 =
      "C_Lecture_EG_Offre_complete_EJ_sans_date_sans_restrictionOI_profil1.xml";

  private static final String NOM_ENTREE_2 = "Projet_TOM-WS02_v0.6.xml";
  private static final String NOM_ENTREE_3 =
      "RechercheMCO_REC_2_1_3_Contenu_des_offres_de_soins_IDF.xml";

  private ResourceTestHelper resourceTestHelper = new ResourceTestHelper(this);

  @ParameterizedTest
  @ValueSource(strings = {NOM_ENTREE_1, NOM_ENTREE_2, NOM_ENTREE_3})
  public void canGetAnyFileEntry(final String entryName) throws IOException {
    Assertions
        .assertTimeoutPreemptively( // Cette assertion fonctionne même dans le cas de thread sans
            // opérations bloquantes, contrairement à @Timeout
            Duration.ofSeconds(1),
            () -> {
              BulkProjectUpdateArchive testee =
                  new BulkProjectUpdateArchive(
                      resourceTestHelper.getLocalResourceContent("test_upload_ROR.zip"));
              byte[] actualEntryContent = testee.getFileEntryData(entryName);
              byte[] expectedContent = resourceTestHelper.getLocalResourceContent(entryName);
              Assertions.assertArrayEquals(
                  expectedContent, actualEntryContent, "Entrée non conforme");
            });
  }
}

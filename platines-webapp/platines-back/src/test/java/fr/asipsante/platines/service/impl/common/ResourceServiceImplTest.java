/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.dto.ResourceDto;
import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.service.ResourceService;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author edegenetais
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class
})
@ContextConfiguration(locations = {"/app-config.xml"})
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(
    value = "/fr/asipsante/platines/service/donnees-theme.xml",
    type = DatabaseOperation.REFRESH)
public class ResourceServiceImplTest {

  /** Object under test. */
  @Autowired private ResourceService resourceService;

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testThemeAssociatedResource() throws Exception {
    MultipartFile f = new TestMultipartFile();
    final long idTheme = 11L;
    Long rscId = Long.decode(resourceService.saveFile(f, idTheme, FileType.RESOURCE.getValue()));

    List<ResourceDto> rscList = resourceService.getResourcesByAssociation(idTheme);
    Assertions.assertEquals(1, rscList.size(), "Only one expected resource for Theme " + idTheme);
    ResourceDto rscS = rscList.get(0);
    Assertions.assertEquals(rscId, rscS.getId(), "Unexpected id mismatch");
    Assertions.assertEquals(f.getOriginalFilename(), rscS.getFileName(), "Unexpected filename");
  }

  private class TestMultipartFile implements MultipartFile {

    public TestMultipartFile() {}

    private static final String CONTENT =
        """
                                                      {"name": "bogus"}
                                                      """;

    @Override
    public String getName() {
      return "testPayload.json";
    }

    @Override
    public String getOriginalFilename() {
      return "/tmp/testPayload.json";
    }

    @Override
    public String getContentType() {
      return ContentType.APPLICATION_JSON.toString();
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public long getSize() {
      return internalGetBytes().length;
    }

    private byte[] internalGetBytes() {
      try {
        return CONTENT.getBytes("UTF-8");
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("Uh !? No UTF-8 ? WTF is that JVM ?", ex);
      }
    }

    @Override
    public byte[] getBytes() throws IOException {
      return internalGetBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(internalGetBytes());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
      try (FileOutputStream os = new FileOutputStream(dest)) {
        os.write(internalGetBytes());
      }
    }
  }
}

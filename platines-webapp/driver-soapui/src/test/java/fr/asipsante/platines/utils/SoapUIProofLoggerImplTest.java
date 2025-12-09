/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author edegenetais
 */
public class SoapUIProofLoggerImplTest {
  private static final NullPointerException NULL_POINTER_EXCEPTION;

  static {
    try {
      NULL_POINTER_EXCEPTION =
          new ObjectMapper()
              .readValue(
                  SoapUIProofLoggerImplTest.class.getResourceAsStream("/exception.json"),
                  NullPointerException.class);
    } catch (IOException ex) {
      throw new RuntimeException("Échech de désérialisation de l'exception test.", ex);
    }
  }

  private static final String NPE_STACKTRACE =
      """
java.lang.NullPointerException: The NPE
	at fr.asipsante.platines.utils.ExceptionThrower.buildException(ExceptionThrower.java:34)
	at fr.asipsante.platines.utils.ExceptionThrower.main(ExceptionThrower.java:30)
""";
  private static final String INFO_PREFIX = "INFO: ";

  private static PrintStream realOut;
  ByteArrayOutputStream testOs;
  SoapUIProofLoggerImpl testee;

  @BeforeClass
  public static void setStdoutUp() {
    realOut = System.out;
  }

  @AfterClass
  public static void restoreStdout() {
    System.setOut(realOut);
  }

  @BeforeEach
  public void setSinkAsStdout() throws UnsupportedEncodingException {
    testOs = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOs, true, "UTF-8"));
  }

  @BeforeEach
  public void setTesteeUpo() {
    testee = new SoapUIProofLoggerImpl(SoapUIProofLoggerImplTest.class);
  }

  @Test
  public void simpleInfoMessage() throws UnsupportedEncodingException {
    final String message = "This is the message";
    testee.info(message);
    Assertions.assertEquals(INFO_PREFIX + message + "\n", testOs.toString("UTF-8"));
  }

  @Test
  public void accentedInfoMessage() throws UnsupportedEncodingException {
    final String message = "Hé, le logger, comment gères-tu les accents?";
    testee.info(message);
    Assertions.assertEquals(INFO_PREFIX + message + "\n", testOs.toString("UTF-8"));
  }

  @Test
  public void errorWithException() throws UnsupportedEncodingException {
    final String message = "This is the message";
    testee.error(message, NULL_POINTER_EXCEPTION);
    Assertions.assertEquals(
        "ERROR: " + message + "\n" + NPE_STACKTRACE + "\n", testOs.toString("UTF-8"));
  }
}

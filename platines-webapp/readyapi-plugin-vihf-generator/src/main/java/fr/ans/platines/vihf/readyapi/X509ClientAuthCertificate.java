/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf.readyapi;

import com.eviware.soapui.settings.SSLSettings;
import com.smartbear.ready.core.ApplicationEnvironment;
import com.smartbear.ready.core.logging.ReadyApiLogger;
import fr.ans.platines.vihf.exception.GenericVihfException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import org.slf4j.Logger;

public class X509ClientAuthCertificate {

  /** Logger definition. */
  @ReadyApiLogger Logger logger;

  String sslKeystore;

  String sslKeystorePassword;

  public X509ClientAuthCertificate() {
    this.sslKeystore = ApplicationEnvironment.getSettings().getString(SSLSettings.KEYSTORE, "");
    this.sslKeystorePassword =
        ApplicationEnvironment.getSettings().getString(SSLSettings.KEYSTORE_PASSWORD, "");
  }

  public X509ClientAuthCertificate(String sslKeystore, String sslKeystorePassword) {
    super();
    this.sslKeystore = sslKeystore;
    this.sslKeystorePassword = sslKeystorePassword;
  }

  public String getDN() {
    KeyStore p12;
    String dn = "";
    try {
      p12 = KeyStore.getInstance("pkcs12");
      p12.load(new FileInputStream(sslKeystore), sslKeystorePassword.toCharArray());
      Enumeration<String> e = p12.aliases();
      while (e.hasMoreElements()) {
        String alias = e.nextElement();
        X509Certificate c = (X509Certificate) p12.getCertificate(alias);
        Principal subject = c.getSubjectX500Principal();
        dn = subject.toString();
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      logger.error("Can't generate DN", e);
      throw new GenericVihfException("Can't generate DN", e);
    }
    return dn;
  }
}

/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.soapui;

import fr.asipsante.platines.exception.ResourceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

/**
 * This consumer accumulates jar URLs from their manifest urls. Only URLs of jar manifests
 * (obviously, jar URLs) are taken into account. The case of jar packaged in a jar/war is mabnaged,
 * as weell as jars from the filesystem (as occures when developing).
 *
 * @author edegenetais
 */
public class JarManifestURLAccumulator implements Consumer<URL>, Iterable<URL> {
  private Set<URL> result = new HashSet<>();

  /**
   * Get the set of all jar URLs found.
   *
   * @return
   */
  public Set<URL> getResult() {
    return result;
  }

  @Override
  public void accept(URL manifestUrl) {
    try {
      if (manifestUrl.getPath().endsWith("META-INF/MANIFEST.MF")) {
        final String manifestUrlString = manifestUrl.toExternalForm();
        String jarPart =
            manifestUrlString.substring(0, manifestUrl.toExternalForm().lastIndexOf('!'));
        if (jarPart.contains("!")) {
          // The jar is itself pâckaged in a jar as ressource (war/runnable jar case) so its URL is
          // still a jar URL
          result.add(new URL(jarPart));
        } else {
          // The jar isn't packaged, so we nedd to strip the jar protocol to build a URL from the
          // jar source URL part
          result.add(new URL(jarPart.substring("jar:".length())));
        }
      } else {
        LoggerFactory.getLogger(JarManifestURLAccumulator.class).debug("Ingored non-manifest URL");
      }
    } catch (MalformedURLException ex) {
      throw new ResourceException(ex, "Failes to locate jar for manifest URL " + manifestUrl);
    }
  }

  /**
   * @return an iterator on accumulated jar URLs. Each jar should only occur once.
   */
  @Override
  public Iterator<URL> iterator() {
    return result.iterator();
  }
}

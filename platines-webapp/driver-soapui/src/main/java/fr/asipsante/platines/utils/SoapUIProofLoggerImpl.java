/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Because SoapUI / eadyAPI wreaks havoc in hte log configuration when started inside a process.
 *
 * @author edegenetais
 */
public class SoapUIProofLoggerImpl implements Logger {

  public SoapUIProofLoggerImpl(final Class<?> aClass) {
    name = aClass.getName();
  }

  private static final String PLACEHOLDER = "\\{\\}";

  @Override
  public String getName() {
    return name;
  }

  private String name;

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(String string) {}

  @Override
  public void trace(String string, Object o) {}

  @Override
  public void trace(String string, Object o, Object o1) {}

  @Override
  public void trace(String string, Object... os) {}

  @Override
  public void trace(String string, Throwable thrwbl) {}

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return false;
  }

  @Override
  public void trace(Marker marker, String string) {}

  @Override
  public void trace(Marker marker, String string, Object o) {}

  @Override
  public void trace(Marker marker, String string, Object o, Object o1) {}

  @Override
  public void trace(Marker marker, String string, Object... os) {}

  @Override
  public void trace(Marker marker, String string, Throwable thrwbl) {}

  @Override
  public boolean isDebugEnabled() {
    return Boolean.parseBoolean(System.getProperty("driver.debug.enabled"));
  }

  @Override
  public void debug(String message) {
    if (isDebugEnabled()) {
      output("DEBUG", message);
    }
  }

  private void output(final String level, String message) {
    System.out.println(level + ": " + message);
  }

  @Override
  public void debug(String string, Object o) {
    if (isDebugEnabled()) {
      if (o instanceof Throwable) {
        debug(string, (Throwable) o);
      } else {
        debug(singleParmMessage(string, o));
      }
    }
  }

  private String singleParmMessage(String string, Object o) {
    return string.replaceFirst(PLACEHOLDER, o == null ? "" : o.toString());
  }

  @Override
  public void debug(String string, Object o, Object o1) {
    if (isDebugEnabled()) {
      if (o1 instanceof Throwable) {
        String message = singleParmMessage(string, o);
        debug(message, (Throwable) o1);
      } else {
        String message =
            singleParmMessage(string.replaceFirst(PLACEHOLDER, o == null ? "" : o.toString()), o1);
        debug(message);
      }
    }
  }

  @Override
  public void debug(String string, Object... os) {
    if (isDebugEnabled()) {
      final Object[] parmsMinusTheLast = new Object[os.length - 1];
      System.arraycopy(os, 0, parmsMinusTheLast, 0, os.length - 1);
      String message = multiParmMessage(string, parmsMinusTheLast);
      debug(message, os[os.length - 1]);
    }
  }

  private String multiParmMessage(String string, Object[] os) {
    String message = string;
    for (int i = 0; i < os.length; i++) {
      message = singleParmMessage(message, os[i]);
    }
    return message;
  }

  @Override
  public void debug(String string, Throwable thrwbl) {
    if (isDebugEnabled()) {
      try {
        debug(buildThrowableMessage(thrwbl, string));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("No reasonnable platform should lack UTF-8 support.", ex);
      }
    }
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isDebugEnabled();
  }

  @Override
  public void debug(Marker marker, String string) {
    debug(string);
  }

  @Override
  public void debug(Marker marker, String string, Object o) {
    debug(string, o);
  }

  @Override
  public void debug(Marker marker, String string, Object o, Object o1) {
    debug(string, o, o1);
  }

  @Override
  public void debug(Marker marker, String string, Object... os) {
    debug(string, os);
  }

  @Override
  public void debug(Marker marker, String string, Throwable thrwbl) {
    debug(string, thrwbl);
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public void info(String string) {
    if (isInfoEnabled()) {
      output("INFO", string);
    }
  }

  @Override
  public void info(String string, Object o) {
    if (isInfoEnabled()) {
      if (o instanceof Throwable) {
        info(string, (Throwable) o);
      }
    }
  }

  @Override
  public void info(String string, Object o, Object o1) {
    if (isInfoEnabled()) {
      if (o1 instanceof Throwable) {
        String message = singleParmMessage(string, o);
        info(message, (Throwable) o);
      } else {
        String message = singleParmMessage(singleParmMessage(string, o), o1);
        info(message);
      }
    }
  }

  @Override
  public void info(String string, Object... os) {
    if (isInfoEnabled()) {
      final Object[] parmsMinusTheLast = new Object[os.length - 1];
      System.arraycopy(os, 0, parmsMinusTheLast, 0, os.length - 1);
      String message = multiParmMessage(string, parmsMinusTheLast);
      info(message, os[os.length - 1]);
    }
  }

  @Override
  public void info(String string, Throwable thrwbl) {
    if (isInfoEnabled()) {
      try {
        info(buildThrowableMessage(thrwbl, string));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("No reasonnable platform should lack UTF-8 support.", ex);
      }
    }
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isInfoEnabled();
  }

  @Override
  public void info(Marker marker, String string) {
    info(string);
  }

  @Override
  public void info(Marker marker, String string, Object o) {
    info(string, o);
  }

  @Override
  public void info(Marker marker, String string, Object o, Object o1) {
    info(string, o, o1);
  }

  @Override
  public void info(Marker marker, String string, Object... os) {
    info(string, os);
  }

  @Override
  public void info(Marker marker, String string, Throwable thrwbl) {
    info(string, thrwbl);
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void warn(String string) {
    if (isWarnEnabled()) {
      output("WARN", string);
    }
  }

  @Override
  public void warn(String string, Object o) {
    if (isWarnEnabled()) {
      if (o instanceof Throwable) {
        warn(string, (Throwable) o);
      } else {
        warn(singleParmMessage(string, o));
      }
    }
  }

  @Override
  public void warn(String string, Object... os) {
    if (isWarnEnabled()) {
      final Object[] parmsMinusTheLast = new Object[os.length - 1];
      System.arraycopy(os, 0, parmsMinusTheLast, 0, os.length - 1);
      String message = multiParmMessage(string, parmsMinusTheLast);
      warn(message, os[os.length - 1]);
    }
  }

  @Override
  public void warn(String string, Object o, Object o1) {
    if (isWarnEnabled()) {
      if (o1 instanceof Throwable) {
        String message = singleParmMessage(string, o);
        warn(message, (Throwable) o);
      } else {
        String message = singleParmMessage(singleParmMessage(string, o), o1);
        warn(message);
      }
    }
  }

  @Override
  public void warn(String string, Throwable thrwbl) {
    if (isWarnEnabled()) {
      try {
        warn(buildThrowableMessage(thrwbl, string));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("No reasonnable platform should lack UTF-8 support.", ex);
      }
    }
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isWarnEnabled();
  }

  @Override
  public void warn(Marker marker, String string) {
    warn(string);
  }

  @Override
  public void warn(Marker marker, String string, Object o) {
    warn(string, o);
  }

  @Override
  public void warn(Marker marker, String string, Object o, Object o1) {
    warn(string, o, o1);
  }

  @Override
  public void warn(Marker marker, String string, Object... os) {
    warn(string, os);
  }

  @Override
  public void warn(Marker marker, String string, Throwable thrwbl) {
    warn(string, thrwbl);
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public void error(String string) {
    if (isErrorEnabled()) {
      output("ERROR", string);
    }
  }

  @Override
  public void error(String string, Object o) {
    if (isErrorEnabled()) {
      if (o instanceof Throwable) {
        error(string, (Throwable) o);
      } else {
        String message = singleParmMessage(string, o);
        error(message);
      }
    }
  }

  @Override
  public void error(String string, Object o, Object o1) {
    if (isErrorEnabled()) {
      if (o1 instanceof Throwable) {
        String message = singleParmMessage(string, o);
        error(message, (Throwable) o1);
      } else {
        String message = singleParmMessage(singleParmMessage(string, o), o1);
        error(message);
      }
    }
  }

  @Override
  public void error(String string, Object... os) {
    if (isErrorEnabled()) {
      final Object[] parmsMinusTheLast = new Object[os.length - 1];
      System.arraycopy(os, 0, parmsMinusTheLast, 0, os.length - 1);
      String message = multiParmMessage(string, parmsMinusTheLast);
      error(message, os[os.length - 1]);
    }
  }

  @Override
  public void error(String string, Throwable thrwbl) {
    if (isErrorEnabled()) {
      try {
        error(buildThrowableMessage(thrwbl, string));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("No reasonnable platform should lack UTF-8 support.", ex);
      }
    }
  }

  private String buildThrowableMessage(Throwable thrwbl, String string)
      throws UnsupportedEncodingException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(os, true);
    thrwbl.printStackTrace(pw);
    final String message = string + "\n" + os.toString("UTF-8");
    return message;
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isErrorEnabled();
  }

  @Override
  public void error(Marker marker, String string) {
    error(string);
  }

  @Override
  public void error(Marker marker, String string, Object o) {
    error(string, o);
  }

  @Override
  public void error(Marker marker, String string, Object o, Object o1) {
    error(string, o, o1);
  }

  @Override
  public void error(Marker marker, String string, Object... os) {
    error(string, os);
  }

  @Override
  public void error(Marker marker, String string, Throwable thrwbl) {
    error(string, thrwbl);
  }
}

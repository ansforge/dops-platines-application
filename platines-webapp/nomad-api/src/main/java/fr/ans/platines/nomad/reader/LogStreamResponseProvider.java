/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.nomad.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.nomad.model.LogStream;

@Provider
@Consumes(MediaType.TEXT_PLAIN)
public class LogStreamResponseProvider implements MessageBodyReader<Object> {

  private static final Logger log = Logger.getLogger(LogStreamResponseProvider.class.getName());

  @Override
  public boolean isReadable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return genericType.equals(LogStream.class);
  }

  @Override
  public Object readFrom(
      Class<Object> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream)
      throws IOException, WebApplicationException {
    LogStream logStream = new LogStream();
    if (entityStream != null) {
      try {
        JsonReader reader = Json.createReader(entityStream);
        JsonObject o = reader.readObject();
        logStream.setData(o.getString("Data"));
        logStream.setOffset(o.getInt("Offset"));
        logStream.setFile("File");
        logStream.setFileEvent("FileEvent");
      } catch (Exception e) {
        log.log(java.util.logging.Level.SEVERE, "Error while reading log stream: ", e);
      }
    }
    return logStream;
  }
}

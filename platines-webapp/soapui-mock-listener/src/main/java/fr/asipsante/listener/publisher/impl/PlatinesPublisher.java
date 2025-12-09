/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.listener.publisher.impl;

import com.google.gson.Gson;
import fr.asipsante.listener.entity.ServerOperation;
import fr.asipsante.listener.publisher.MockResultsPublisher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatinesPublisher implements MockResultsPublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlatinesPublisher.class);

  private static final String URL_PLATINES = System.getenv("PLATINES_PUBLISHER");

  @Override
  public void publish(ServerOperation serverOperation) {
    String url = URL_PLATINES + "/saveServerOperation";
    Gson gson = new Gson();
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody(
        "operation",
        gson.toJson(serverOperation),
        ContentType.create("application/json", Consts.UTF_8));
    HttpEntity multipart = builder.build();
    publish(url, multipart);
  }

  private String publish(String url, HttpEntity httpEntity) {
    HttpClient httpClient = HttpClientBuilder.create().build();

    HttpPost post = new HttpPost(url);
    HttpResponse response;
    String responseServer = "";
    try {
      post.setEntity(httpEntity);

      response = httpClient.execute(post);
      BufferedReader br =
          new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
      while ((responseServer = br.readLine()) != null) {
        responseServer += responseServer;
      }
    } catch (IOException e) {
      LOGGER.error("Erreur lors de l'envoi des informations platines", e);
    }

    return responseServer;
  }
}

/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.publisher.impl;

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

import com.google.gson.Gson;

import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.SessionLog;
import fr.asipsante.platines.publisher.IPublisher;

public class PlatinesPublisher implements IPublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatinesPublisher.class);

	private static final String URL_PLATINES = System.getenv("PLATINES_PUBLISHER");

	@Override
	public void publishProject(DriverTestResult resultat) {
		String url = URL_PLATINES + "/saveResultRProjet/";
		Gson gson = new Gson();
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("driverProjectResult", gson.toJson(resultat),
				ContentType.create("application/json", Consts.UTF_8));
		HttpEntity multipart = builder.build();
		publish(url, multipart);
	}

	@Override
	public void publishSession(DriverSessionResult driverSessionResult) {
		String url = URL_PLATINES + "/updateSession/";
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Gson gson = new Gson();
		builder.addTextBody("result", gson.toJson(driverSessionResult),
				ContentType.create("application/json", Consts.UTF_8));
		HttpEntity multipart = builder.build();
		publish(url, multipart);

	}

	@Override
	public void publishLogSession(SessionLog sessionLog) {
	    
		String url = URL_PLATINES + "/addLogSession/";
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("uuid", sessionLog.getUuidSession());
		builder.addBinaryBody("file", sessionLog.getSessionLog(), ContentType.APPLICATION_OCTET_STREAM,
				sessionLog.getSessionLog().getName());
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
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			while ((responseServer = br.readLine()) != null) {
				responseServer += responseServer;
			}
		} catch (IOException e) {
			LOGGER.error("Erreur lors de l'envoi des informations platines", e);
		}

		return responseServer;
	}
}

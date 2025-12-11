/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.asipsante.platines.executor.IExecutor;
import fr.asipsante.platines.executor.impl.SoapuiExecutor;
import fr.asipsante.platines.executor.status.ExitStatus;
import fr.asipsante.platines.model.DriverContext;
import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.SessionLog;
import fr.asipsante.platines.model.TestStatus;
import fr.asipsante.platines.publisher.IPublisher;
import fr.asipsante.platines.publisher.impl.PlatinesPublisher;
import fr.asipsante.platines.utils.Unzip;
import fr.asipsante.platines.utils.ZipDirectory;

public class TestDriver {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestDriver.class);

	private static final String SESSION_DIRECTORY = System.getenv("JAVA_APP_DIR") + File.separator
			+ System.getenv("SESSION_DIRECTORY");

	private static final File ARCHIVE_FILE = Paths.get(SESSION_DIRECTORY + ".zip").toFile();

	public static void main(String[] args) {
	    LOGGER.info("Démarrage de la session");
	    DriverSessionResult driverSessionResult = new DriverSessionResult();
        
	    List<IPublisher> publishers = new ArrayList<>();
        publishers.add(new PlatinesPublisher());
        for (IPublisher iPublisher : publishers) {
            iPublisher.publishSession(driverSessionResult);
        }
		Unzip unzip = new Unzip();
		try {
			unzip.unzip(ARCHIVE_FILE, ARCHIVE_FILE.getParent());
		} catch (IOException e1) {
			LOGGER.error("Problème lors du dézippage du zip de session : ", e1);
			driverSessionResult.setStatus(TestStatus.ERROR.toString());
			for (IPublisher iPublisher : publishers) {
	            iPublisher.publishSession(driverSessionResult);
	        }
			System.exit(ExitStatus.ZIP_ERROR.getValue());
		}
		LOGGER.info("Session dézipée");
		driverSessionResult.setDateExecution(new Date());
		driverSessionResult.setStatus(TestStatus.PENDING.toString());
		driverSessionResult.setUuidSession(System.getenv("SESSION_DIRECTORY"));
		List<String> projectsStatus = new ArrayList<>();
		
		for (IPublisher iPublisher : publishers) {
			iPublisher.publishSession(driverSessionResult);
		}
		IExecutor iExecutor = null;
		for (int i = 0; i < args.length; i++) {
			Path path = Paths.get(args[i]);
			File projectFolder = new File(path.toUri());
			DriverContext context = new DriverContext();
			context.setProjectDirectory(projectFolder.getParentFile());
			context.setProperties(getProperties(projectFolder.getParentFile()));
			iExecutor = new SoapuiExecutor(context, publishers);
			iExecutor.init();
			DriverTestResult driverTestResult = iExecutor.execute(projectFolder);
			projectsStatus.add(driverTestResult.getStatut());

		}
		driverSessionResult.setStatus(TestStatus.FINISHED.toString());
		driverSessionResult.setTimeTaken(new Date().getTime() - driverSessionResult.getDateExecution().getTime());

		SessionLog sessionLog = new SessionLog();
		for (IPublisher iPublisher : publishers) {
			iPublisher.publishSession(driverSessionResult);
		}
		if (System.getenv("EXPORT_LOG") != null) {
			ZipDirectory directory = new ZipDirectory();
			String zipPath = System.getenv("SESSION_DIRECTORY") + File.separator + "log_"
					+ System.getenv("SESSION_DIRECTORY") + ".zip";
			directory.zipFiles(
					System.getenv("SESSION_DIRECTORY") + File.separator + ".readyapi" + File.separator + "logs",
					zipPath);
			sessionLog.setSessionLog(Paths.get(zipPath).toFile());
			sessionLog.setUuidSession(System.getenv("SESSION_DIRECTORY"));
			for (IPublisher iPublisher : publishers) {
				iPublisher.publishLogSession(sessionLog);
			}

		}

		System.exit(0);
	}

	private static Properties getProperties(File directoryProjet) {
		Properties properties = new Properties();
		for (File file : directoryProjet.listFiles()) {
			if (file.getName().equals("project.properties")) {
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);
					properties.load(in);
				} catch (IOException e) {
					LOGGER.error("Erreur lors de la récupération des properties du projet", e);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						LOGGER.error("Erreur lors de la fermeture du flux lors de la lecture des properties du projet",
								e);
					}
				}
			}
		}

		return properties;
	}

}

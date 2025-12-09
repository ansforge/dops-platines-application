/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines;

import fr.asipsante.platines.executor.Executor;
import fr.asipsante.platines.executor.SoapuiExecutor;
import fr.asipsante.platines.executor.status.ExitStatus;
import fr.asipsante.platines.model.DriverContext;
import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.SessionLog;
import fr.asipsante.platines.model.TestStatus;
import fr.asipsante.platines.publisher.TestsResultsPublisher;
import fr.asipsante.platines.publisher.impl.PlatinesPublisher;
import fr.asipsante.platines.utils.SoapUIProofLoggerImpl;
import fr.asipsante.platines.utils.Unzip;
import fr.asipsante.platines.utils.ZipDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

public class TestDriver {

  private static final String ACCESS_KEY_OPTION = "accessKey";

  private static final String TESTS_OPTIONS = "tests";

  private static final Logger LOGGER = new SoapUIProofLoggerImpl(TestDriver.class);

  private static final String JAVA_APP_DIR =
      System.getProperty("java.app.dir", System.getenv("JAVA_APP_DIR"));

  private static final String SESSION_DIRECTORY =
      System.getProperty("session.directory", System.getenv("SESSION_DIRECTORY"));

  private static final File ARCHIVE_FILE =
      Paths.get(JAVA_APP_DIR + File.separator + SESSION_DIRECTORY + ".zip").toFile();

  public static void main(String[] args) {
    CommandLine cmd = parseArgs(args);
    // Map pour stocker les options supplémentaires aux fichiers des tests
    Map<String, String> contextOptions = new HashMap<>();
    if (cmd.hasOption("k")) {
      contextOptions.put(ACCESS_KEY_OPTION, cmd.getOptionValue(ACCESS_KEY_OPTION));
      LOGGER.info("ReadyApi access key : {}", cmd.getOptionValue(ACCESS_KEY_OPTION));
    }
    LOGGER.info("Démarrage de la session");
    DriverSessionResult driverSessionResult = new DriverSessionResult();

    List<TestsResultsPublisher> publishers = new ArrayList<>();
    publishers.add(new PlatinesPublisher());
    Unzip unzip = new Unzip();
    try {
      unzip.unzip(ARCHIVE_FILE, ARCHIVE_FILE.getParent());
    } catch (IOException e1) {
      LOGGER.error("Problème lors du dézippage du zip de session : ", e1);
      driverSessionResult.setStatus(TestStatus.ERROR.toString());
      for (TestsResultsPublisher iPublisher : publishers) {
        iPublisher.publishSession(driverSessionResult);
      }
      System.exit(ExitStatus.ZIP_ERROR.getValue());
    }
    LOGGER.info("Session dézipée");
    String archivePath = ARCHIVE_FILE.getAbsolutePath();
    String sessionBase = archivePath.substring(0, archivePath.lastIndexOf('.'));
    LOGGER.debug("SoapUI home : {}", sessionBase);
    System.setProperty("soapui.home", sessionBase);
    driverSessionResult.setDateExecution(new Date());
    driverSessionResult.setStatus(TestStatus.PENDING.toString());
    driverSessionResult.setUuidSession(SESSION_DIRECTORY);
    List<String> projectsStatus = new ArrayList<>();

    for (TestsResultsPublisher iPublisher : publishers) {
      iPublisher.publishSession(driverSessionResult);
    }

    LOGGER.info("Statut session en attente de démarrage publié.");

    Executor executor = null;
    String[] tests = cmd.getOptionValues(TESTS_OPTIONS);
    for (int i = 0; i < tests.length; i++) {
      Path path = Paths.get(tests[i]);
      File projectFolder = new File(path.toUri());
      LOGGER.info("Lancement test {}", projectFolder.getPath());
      DriverContext context = new DriverContext();
      context.setOptions(contextOptions);
      context.setProjectDirectory(projectFolder.getParentFile());
      executor = new SoapuiExecutor(context, publishers);
      executor.init();
      DriverTestResult driverTestResult = executor.execute(projectFolder);
      projectsStatus.add(driverTestResult.getStatut());
    }
    driverSessionResult.setStatus(TestStatus.FINISHED.toString());
    driverSessionResult.setTimeTaken(
        new Date().getTime() - driverSessionResult.getDateExecution().getTime());

    LOGGER.info("Tests terminés en {} ms", driverSessionResult.getTimeTaken());

    SessionLog sessionLog = new SessionLog();
    for (TestsResultsPublisher iPublisher : publishers) {
      iPublisher.publishSession(driverSessionResult);
    }
    LOGGER.info("Résultats publiées");
    if (System.getenv("EXPORT_LOG") != null) {
      ZipDirectory directory = new ZipDirectory();
      String zipPath = SESSION_DIRECTORY + File.separator + "log_" + SESSION_DIRECTORY + ".zip";
      directory.zipFiles(
          SESSION_DIRECTORY + File.separator + ".readyapi" + File.separator + "logs", zipPath);
      sessionLog.setSessionLog(Paths.get(zipPath).toFile());
      sessionLog.setUuidSession(SESSION_DIRECTORY);
      for (TestsResultsPublisher iPublisher : publishers) {
        iPublisher.publishLogSession(sessionLog);
      }
      LOGGER.info("Logs publiés.");
    }

    System.exit(0);
  }

  private static CommandLine parseArgs(String[] args) {
    // Options possibles en plus des tests, une seule pour le moment : clef de licence ReadyAPI
    Options options = new Options();
    Option tests =
        Option.builder("t")
            .longOpt(TESTS_OPTIONS)
            .argName(TESTS_OPTIONS)
            .hasArgs()
            .required(true)
            .desc("List of Test files to execute")
            .build();
    Option accessKey =
        Option.builder("k")
            .longOpt(ACCESS_KEY_OPTION)
            .argName(ACCESS_KEY_OPTION)
            .hasArg()
            .required(false)
            .desc("Access Key for ReadyAPI")
            .build();
    options.addOption(accessKey);
    options.addOption(tests);

    CommandLine cmd = null;
    CommandLineParser parser = new DefaultParser();
    HelpFormatter helper = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);

    } catch (ParseException e) {
      LOGGER.info(e.getMessage());
      helper.printHelp("Usage:", options);
      System.exit(0);
    }
    LOGGER.debug("Options correctes");
    return cmd;
  }
}

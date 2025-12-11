/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.executor.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlProjectPro;
import com.eviware.soapui.impl.wsdl.testcase.WsdlProjectRunner;
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestSuiteRunner;
import com.eviware.soapui.plugins.PluginManager;
import com.eviware.soapui.settings.ProxySettings;
import com.eviware.soapui.settings.SSLSettings;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.soapui.support.types.StringToStringsMap;
import com.smartbear.ready.ui.toolbar.ReadyApiToolbarComponentRegistry;

import fr.asipsante.platines.executor.IExecutor;
import fr.asipsante.platines.executor.status.ExitStatus;
import fr.asipsante.platines.model.DriverContext;
import fr.asipsante.platines.model.DriverTestCaseResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.DriverTestStepResult;
import fr.asipsante.platines.model.DriverTestSuiteResult;
import fr.asipsante.platines.model.TestStatus;
import fr.asipsante.platines.publisher.IPublisher;

public class SoapuiExecutor implements IExecutor {
	/**
	 * Le loggeur LOG4J.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SoapuiExecutor.class);

	private static final String PROXY_HOST = System.getenv("PROXY_HOST");

	private static final String PROXY_PORT = System.getenv("PROXY_PORT");

	private static final String SESSION_DIRECTORY = System.getenv("SESSION_DIRECTORY");

	private static final String KEYSTORE_PASSWORD = System.getenv("KEYSTORE_PASSWORD");

	/**
	 * Context du driver.
	 */
	private DriverContext context;

	/**
	 * Liste des publishers sur lesquels seront envoyés les résultats de l'exécution
	 * de chaque test.
	 */
	private List<IPublisher> iPublishers;
	/**
	 * Properties du projet<
	 */
	private Properties properties;

	/**
	 * Résultat du test.
	 */
	private DriverTestResult driverTestResult;

	/**
	 * Constructeur par défaut.
	 */
	public SoapuiExecutor() {
	}

	/**
	 * 
	 * @param projectDirectory
	 */
	public SoapuiExecutor(DriverContext context, List<IPublisher> iPublishers) {
		this.context = context;
		this.iPublishers = iPublishers;
	}

	/**
	 * Méthode d'initialisation des settings soapui. En paramètre est passé le path
	 * du répertoire du projet qui sera exécuté par soapui.
	 * 
	 * @param projectDirectory
	 */
	@Override
	public void init() {
		if (SESSION_DIRECTORY == null) {
			LOGGER.error("Répertoire de session introuvable");
			System.exit(ExitStatus.SESSION_DIRECTORY_ERROR.getValue());
		} else {
			Path soapuiHome = Paths.get(SESSION_DIRECTORY);
			File directorySoapuiHome = soapuiHome.toFile();
			System.setProperty("user.home", directorySoapuiHome.getAbsolutePath());
			Settings settings = SoapUI.getSettings();
			settings.setString("soapui.home",
			        System.getenv("JAVA_APP_DIR") + File.separator + "lib");
			settings.setString("nosFolder", soapuiHome + File.separator + "nomenclatures");
			settings.setString("Script Library", System.getenv("SESSION_DIRECTORY") + File.separator
					+ "projets" + File.separator + context.getProjectDirectory().getName());
			//password avant keystore, car des qu'il a le keystore il essaie de l'ouvrir
			if (KEYSTORE_PASSWORD != null) {
			    settings.setString(SSLSettings.KEYSTORE_PASSWORD, System.getenv("KEYSTORE_PASSWORD"));
			}

			File keystoreDirectory = new File(
					context.getProjectDirectory().getAbsolutePath() + File.separator + "keystore");

			if (keystoreDirectory.exists() && keystoreDirectory.isDirectory()) {
			    settings.setString(SSLSettings.KEYSTORE,
						keystoreDirectory.listFiles()[0].getAbsolutePath());
			}

			if (PROXY_HOST != null && PROXY_PORT != null) {
			    settings.setString(ProxySettings.HOST, PROXY_HOST);
			    settings.setString(ProxySettings.PORT, PROXY_PORT);
			    settings.setString(ProxySettings.AUTO_PROXY, "false");
			    settings.setString(ProxySettings.ENABLE_PROXY, "true");

				try {
					SoapUI.saveSettings();
					SoapUI.updateProxyFromSettings();
				} catch (Exception e) {
					LOGGER.error("Sauvegarde des Settings SoapUI" + e);
				}
			}

			PluginManager manager = new PluginManager(SoapUI.getFactoryRegistry(), SoapUI.getActionRegistry(),
					SoapUI.getListenerRegistry(), new ReadyApiToolbarComponentRegistry());

			manager.loadPlugins();
		}

	}

	/**
	 * Méthode d'exécution des tests. En paramètre est passé le fichier projet à
	 * exécuter.
	 * 
	 * @param project
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public DriverTestResult execute(File project) {
		driverTestResult = new DriverTestResult();
		driverTestResult.setStatut(TestStatus.PENDING.toString());
		driverTestResult.setProjectId(context.getProjectDirectory().getName());
		driverTestResult.setDateExecution(new Date());
		publish(driverTestResult);
		WsdlProjectPro wsdlProject = new WsdlProjectPro(project.getAbsolutePath());
		properties = context.getProperties();

		if (!context.getProperties().isEmpty()) {
			wsdlProject = getPropertiesOfProject(wsdlProject);
		}

		WsdlProjectRunner runner = wsdlProject.run(new StringToObjectMap(), false);
		List<TestSuiteRunner> liSuiteRunners = runner.getResults();
		//tri pour apparaitre dans l'ordre d'execution
		liSuiteRunners = liSuiteRunners.stream().sorted(Comparator.comparing(TestSuiteRunner::getStartTime))
				.collect(Collectors.toList());
		driverTestResult.setStatut(setTestStatus(runner.getStatus().toString()).toString());
		driverTestResult.setExecutionTime(runner.getTimeTaken());
		driverTestResult.setName(runner.getProject().getName());
		driverTestResult.setDescription(runner.getProject().getDescription());
		setTestResult(liSuiteRunners);
		publish(driverTestResult);

		return driverTestResult;
	}

	@Override
	public void publish(DriverTestResult driverTestResult) {
		for (IPublisher publisher : iPublishers) {
			publisher.publishProject(driverTestResult);
		}
	}

	private WsdlProjectPro getPropertiesOfProject(WsdlProjectPro wsdlProject) {
		for (Entry<Object, Object> prop : context.getProperties().entrySet()) {
			wsdlProject.setPropertyValue(prop.getKey().toString(), prop.getValue().toString());
		}

		return wsdlProject;
	}

	private void setTestResult(List<TestSuiteRunner> liSuiteRunners) {
		driverTestResult.setErrors(new ArrayList<>());
		for (TestSuiteRunner testSuiteRunner : liSuiteRunners) {
			setTestSuiteResult(testSuiteRunner);
		}
	}

	private DriverTestSuiteResult setTestSuiteResult(TestSuiteRunner testSuiteRunner) {
		DriverTestSuiteResult driverTestSuiteResult = new DriverTestSuiteResult();
		driverTestSuiteResult.setContent(testSuiteRunner.getReason());
		driverTestSuiteResult.setDateExecution(new Date(testSuiteRunner.getStartTime()));
		driverTestSuiteResult.setExecutionTime(testSuiteRunner.getTimeTaken());
		driverTestSuiteResult.setName(testSuiteRunner.getTestSuite().getName());
		driverTestSuiteResult.setStatut(setTestStatus(testSuiteRunner.getStatus().toString()).toString());
		driverTestSuiteResult.setDescription(testSuiteRunner.getTestSuite().getDescription());
		driverTestResult.getSuiteResults().add(driverTestSuiteResult);

		List<TestCaseRunner> testCaseRunners = testSuiteRunner.getResults();
		testCaseRunners = testCaseRunners.stream()
				.sorted(Comparator.comparing(TestCaseRunner::getStartTime)).collect(Collectors.toList());
		for (TestCaseRunner testCaseRunner : testCaseRunners) {
			setTestCaseResult(testCaseRunner, driverTestSuiteResult);
		}
		return driverTestSuiteResult;
	}

	private DriverTestCaseResult setTestCaseResult(TestCaseRunner testCaseRunner,
			DriverTestSuiteResult driverTestSuiteResult) {
		DriverTestCaseResult driverTestCaseResult = new DriverTestCaseResult();
		driverTestCaseResult.setContent(driverTestSuiteResult.getContent());
		driverTestCaseResult.setDateExecution(new Date(testCaseRunner.getStartTime()));
		driverTestCaseResult.setExecutionTime(testCaseRunner.getTimeTaken());
		driverTestCaseResult.setStatut(setTestStatus(testCaseRunner.getStatus().toString()).toString());
		driverTestCaseResult.setName(testCaseRunner.getTestCase().getName());
		driverTestCaseResult.setDescription(testCaseRunner.getTestCase().getDescription());
		if (testCaseRunner.getTestCase().getProperty("criticite") != null) {
			driverTestCaseResult.setCriticality(testCaseRunner.getTestCase().getProperty("criticite").getValue());
		}
		driverTestSuiteResult.getCaseResults().add(driverTestCaseResult);
		List<TestStepResult> stepResults = testCaseRunner.getResults();
		stepResults = stepResults.stream().sorted(Comparator.comparing(TestStepResult::getTimeStamp))
				.collect(Collectors.toList());
		for (TestStepResult stepResult : stepResults) {
			setTestStepResult(stepResult, driverTestCaseResult);
		}

		return driverTestCaseResult;
	}

	private DriverTestStepResult setTestStepResult(TestStepResult stepResult,
			DriverTestCaseResult driverTestCaseResult) {
		DriverTestStepResult driverTestStepResult = new DriverTestStepResult();
		if (stepResult.getTestStep().getClass() == WsdlTestRequestStep.class
				|| stepResult.getTestStep().getClass() == RestTestRequestStep.class) {
			driverTestStepResult.setErrors(getListeMessage(stepResult.getMessages()));
			driverTestStepResult.setExecutionTime(stepResult.getTimeTaken());
			driverTestStepResult.setName(stepResult.getTestStep().getName());
			driverTestStepResult.setStatut(setTestStatus(stepResult.getStatus().toString()).toString());
			driverTestStepResult.setDescription(stepResult.getTestStep().getDescription());
			driverTestStepResult.setDateExecution(new Date(stepResult.getTimeStamp()));

			if(((MessageExchange)stepResult).getRawRequestData() != null) {
			    final String request = new String(((MessageExchange)stepResult).getRawRequestData());
	            driverTestStepResult.setRequest(request);
			}
			if(((MessageExchange)stepResult).getResponseHeaders() != null) {
			    final StringToStringsMap strMap = ((MessageExchange)stepResult).getResponseHeaders();
	            String headers = "";
	            for (String name : strMap.getKeys()) {
	                for (String value : strMap.get(name)) {
	                    headers = headers + name + " : " + value + "\n";
	                }
	            }
	            if(((MessageExchange)stepResult).getResponseContent() != null) {
	                final String response = headers + "\n" +((MessageExchange)stepResult).getResponseContent();
	                driverTestStepResult.setResponse(response);
	            }
	            
			}
			
            
            
			driverTestCaseResult.getStepResults().add(driverTestStepResult);
			
		}
		return driverTestStepResult;
	}

	private TestStatus setTestStatus(String status) {

		TestStatus testStatus = null;

		switch (status) {
		case "CANCELED":
			testStatus = TestStatus.ERROR;
			break;
		case "UNKNOWN":
			testStatus = TestStatus.ERROR;
			break;
		case "PENDING":
			testStatus = TestStatus.ERROR;
			break;
		case "RUNNING":
			testStatus = TestStatus.ERROR;
			break;
		case "WARNING":
			testStatus = TestStatus.ERROR;
			break;
		case "FINISHED":
			testStatus = TestStatus.SUCCESS;
			break;
		case "OK":
			testStatus = TestStatus.SUCCESS;
			break;
		case "FAILED":
			testStatus = TestStatus.FAILURE;
			break;
		default:
			testStatus = TestStatus.ERROR;
			break;
		}

		return testStatus;

	}

	private List<String> getListeMessage(String[] msgs) {
		final List<String> listMsg = new ArrayList<>();
		for (String msg : msgs) {
			listMsg.add(msg);
		}
		return listMsg;
	}

	/**
	 * Get iPublishers.
	 * 
	 * @return
	 */
	public List<IPublisher> getiPublishers() {
		return iPublishers;
	}

	/**
	 * Set iPublishers.
	 * 
	 * @param iPublishers
	 */
	public void setiPublishers(List<IPublisher> iPublishers) {
		this.iPublishers = iPublishers;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the driverTestResult
	 */
	public DriverTestResult getDriverTestResult() {
		return driverTestResult;
	}

	/**
	 * @param driverTestResult
	 *            the driverTestResult to set
	 */
	public void setDriverTestResult(DriverTestResult driverTestResult) {
		this.driverTestResult = driverTestResult;
	}

}

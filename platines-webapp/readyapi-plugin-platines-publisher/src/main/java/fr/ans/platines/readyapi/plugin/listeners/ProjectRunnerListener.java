/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.readyapi.plugin.listeners;

import com.eviware.soapui.model.support.ProjectRunListenerAdapter;
import com.eviware.soapui.model.testsuite.ProjectRunContext;
import com.eviware.soapui.model.testsuite.ProjectRunner;
import com.eviware.soapui.plugins.ListenerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ListenerConfiguration
public class ProjectRunnerListener extends ProjectRunListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(ProjectRunnerListener.class);

  @Override
  public void beforeRun(ProjectRunner projectRunner, ProjectRunContext runContext) {
    // Fusionner les properties du fichier avec celles du projet
    // Voir quelle sont les properties à fusionner ???
    projectRunner.getProject().getProperties();
    projectRunner.getRunContext().getProperties();
  }

  @Override
  public void afterRun(ProjectRunner projectRunner, ProjectRunContext runContext) {
    // Publier les logs de la session ???
    // Attention, on doit publier projet par projet au lieu de toute la session : à voir.
  }
}

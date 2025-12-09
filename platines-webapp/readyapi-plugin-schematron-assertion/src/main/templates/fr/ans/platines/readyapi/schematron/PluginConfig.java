/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.readyapi.schematron;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(
    groupId = "${project.groupId}",
    name = "${project.name}",
    version = "${project.version}",
    autoDetect = true,
    description = "${project.description}",
    infoUrl = "${project.url}")
public class PluginConfig extends PluginAdapter {}

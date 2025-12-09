/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf.soapui.plugin;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(
    groupId = "${project.groupId}",
    name = "${project.name}",
    version = "${project.version}",
    autoDetect = true,
    description = "${project.description}",
    infoUrl = "${project.url}",
    category = "CI/CD - API Test Automation")
public class PluginConfig extends PluginAdapter {}

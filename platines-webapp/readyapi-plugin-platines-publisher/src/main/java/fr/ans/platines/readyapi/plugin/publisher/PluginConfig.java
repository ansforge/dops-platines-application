/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.readyapi.plugin.publisher;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(
    groupId = "fr.ans.ready.plugins",
    name = "Ready! API Publisher Plugin",
    version = "1.0",
    autoDetect = true,
    description = "A Ready! API Plugin to publish Tests Results to Platines",
    infoUrl =
        "http://rhodecode.proxy-dev-forge.asip.hst.fluxus.net/ApplicationsANS/Transverse/platines/application/platines-webapp")
public class PluginConfig extends PluginAdapter {}

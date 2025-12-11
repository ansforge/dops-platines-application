package fr.plugin.soapui;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "fr.plugin.soapui.plugins", name = "saml2-plugin SoapUI RequestFilter", version = "1.0",
        autoDetect = true, description = "A Request Intercepting, VIHF Token Generating, SoapUI Plugin",
        infoUrl = "" )
public class PluginConfig extends PluginAdapter {
}

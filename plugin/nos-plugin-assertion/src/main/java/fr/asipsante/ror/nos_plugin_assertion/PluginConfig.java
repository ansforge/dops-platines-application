/*
 * Construction de la pop voulu pour transformer les fichiers de nomenclatures au format xml. 
 */
package fr.asipsante.ror.nos_plugin_assertion;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "fr.asipsante.ror.plugins", name = "Schematron Assertion Plugin", version = "1.5",
        autoDetect = true, description = "Plugin permettant de contrôler une réponse xml contre un fichier schematron",
        infoUrl = "" )
public class PluginConfig extends PluginAdapter {
}

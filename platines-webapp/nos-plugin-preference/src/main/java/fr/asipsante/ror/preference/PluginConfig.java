/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.ror.preference;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(
    groupId = "fr.asipsante.ror.plugins",
    name = "Nos Preference Plugin",
    version = "1.0",
    autoDetect = true,
    description =
        "Plugin préférence indiquant le path du dossier contenant les fichiers"
            + "schematron et de nomenclatures",
    infoUrl = "")
public class PluginConfig extends PluginAdapter {}

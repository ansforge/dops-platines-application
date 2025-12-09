/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.ror.preference;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.actions.Prefs;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.plugins.auto.PluginPrefs;
import com.eviware.soapui.support.components.DirectoryFormComponent;
import com.eviware.soapui.support.components.SimpleForm;
import com.eviware.soapui.support.types.StringToStringMap;

@PluginPrefs
public class PrefConverter implements Prefs {

  private static final String FOLDER_NOMENCLATURE = "nosFolder";
  private static final int COMPONENT_SPACE = 5;
  private SimpleForm myPrefForm;
  private DirectoryFormComponent directory = new DirectoryFormComponent("");

  @Override
  public SimpleForm getForm() {
    if (myPrefForm == null) {
      myPrefForm = new SimpleForm();
      myPrefForm.addSpace(5);
      if (!SoapUI.getSettings().getString(FOLDER_NOMENCLATURE, "").isEmpty()) {
        directory.setValue(SoapUI.getSettings().getString(FOLDER_NOMENCLATURE, ""));
      }
      myPrefForm.append("Dossier", directory);
      myPrefForm.addSpace(COMPONENT_SPACE);
    }
    return myPrefForm;
  }

  @Override
  public void setFormValues(Settings settings) {
    getForm().setValues(getValues(settings));
  }

  @Override
  public void getFormValues(Settings settings) {
    StringToStringMap values = new StringToStringMap();
    myPrefForm.getValues(values);
    storeValues(values, settings);
  }

  @Override
  public void storeValues(StringToStringMap values, Settings settings) {
    String target = values.get("Dossier");
    settings.setString(FOLDER_NOMENCLATURE, target);
  }

  @Override
  public StringToStringMap getValues(Settings settings) {
    StringToStringMap values = new StringToStringMap();
    values.put(FOLDER_NOMENCLATURE, settings.getString(FOLDER_NOMENCLATURE, ""));
    return values;
  }

  @Override
  public String getTitle() {
    return "Localisation des fichiers Nos/Schematron";
  }
}

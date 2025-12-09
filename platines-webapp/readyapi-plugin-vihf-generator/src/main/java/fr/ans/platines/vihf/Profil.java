/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf;

public class Profil {

  private String name = "Profil_VIHF";

  private String value;

  private String code;

  private String displayName;

  public Profil() {}

  public Profil(String name, String value) {
    this.name = name;
    this.value = value;
    this.fetchCodeAndDisplayName(value);
  }

  public Profil(String value) {
    this.value = value;
    this.fetchCodeAndDisplayName(value);
  }

  private void fetchCodeAndDisplayName(String value) {

    if ("0".equals(value)) {
      code = "CU23";
      displayName = "Information du public";
    } else if ("1".equals(value)) {
      code = "CU22";
      displayName = "Régulation de soins non programmés";
    } else if ("2".equals(value)) {
      code = "CU20";
      displayName = "Orientation sanitaire et médico-sociale";
    } else if ("3".equals(value)) {
      code = "CU19";
      displayName = "Orientation médico-sociale";
    }
  }

  public boolean isValid() {
    return value.matches("^[0-3]{1}$");
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}

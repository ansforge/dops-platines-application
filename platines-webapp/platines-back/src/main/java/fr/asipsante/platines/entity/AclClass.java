/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AclClass.
 *
 * @author apierre
 */
@Entity
@Table(name = "acl_class")
public class AclClass implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7662652756483058123L;

  /** the id. */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  /** the class object. */
  @Column(name = "class")
  private String classe;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the class
   */
  public String getClasse() {
    return classe;
  }

  /**
   * @param classe the class to set
   */
  public void setClasse(String classe) {
    this.classe = classe;
  }
}

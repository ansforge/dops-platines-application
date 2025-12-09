/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.GlobalListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Association corresponding to the table "association" in the database. La table Association
 * c'est la table mère de Famille, Service et Version. Elle sert à identifier le 'rank' (ici
 * association_type) d'une entité.
 *
 * @author cnader
 */
@Entity
@Table(name = "association")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "association_type", discriminatorType = DiscriminatorType.INTEGER)
@EntityListeners(GlobalListener.class)
public class Association extends AbstractEntity implements Serializable {

  public enum RANK {
    FAMILY,
    SERVICE,
    VERSION
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4537450176523406071L;

  /** Association type. */
  @Column(name = "association_type", insertable = false, updatable = false)
  private int associationType;

  public RANK getAssociationType() {
    if (associationType == 1) {
      return RANK.FAMILY;
    } else if (associationType == 2) {
      return RANK.SERVICE;
    } else {
      return RANK.VERSION;
    }
  }

  public void setAssociationType(int associationType) {
    this.associationType = associationType;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}

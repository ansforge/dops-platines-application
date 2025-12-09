/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Class UserFamilyId.
 *
 * @author cnader
 */
@Embeddable
public class UserFamilyId implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4761172824877345712L;

  /** The user id. */
  @Column(name = "User_ID")
  private Long userId;

  /** family id. */
  @Column(name = "FAMILY_ID")
  private Long familyId;

  /** Constructors. */
  public UserFamilyId() {}

  public UserFamilyId(Long userId, Long familyId) {
    this.userId = userId;
    this.familyId = familyId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public void setFamilyId(Long familyId) {
    this.familyId = familyId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((familyId == null) ? 0 : familyId.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final UserFamilyId other = (UserFamilyId) obj;
    if (familyId == null) {
      if (other.familyId != null) return false;
    } else if (!familyId.equals(other.familyId)) return false;
    if (userId == null) {
      if (other.userId != null) return false;
    } else if (!userId.equals(other.userId)) return false;
    return true;
  }
}

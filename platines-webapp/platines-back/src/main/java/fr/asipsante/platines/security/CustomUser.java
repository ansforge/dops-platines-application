/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class CustomUser extends User {
  /** */
  private static final long serialVersionUID = -5768487551209111104L;

  private final String username;
  private final Set<GrantedAuthority> authorities;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;
  private List<Long> families;
  private String profil;

  public CustomUser(
      String profil,
      List<Long> families,
      String username,
      String password,
      boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired,
      boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(
        username,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities);

    if ("".equals(username)) {
      throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
    }

    this.username = username;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    this.families = families;
    this.profil = profil;
  }

  private static SortedSet<GrantedAuthority> sortAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
    // Ensure array iteration order is predictable (as per
    // UserDetails.getAuthorities() contract and SEC-717)
    final SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());

    for (final GrantedAuthority grantedAuthority : authorities) {
      Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
      sortedAuthorities.add(grantedAuthority);
    }

    return sortedAuthorities;
  }

  private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Override
    public int compare(GrantedAuthority g1, GrantedAuthority g2) {
      // Neither should ever be null as each entry is checked before adding it to
      // the set.
      // If the authority is null, it is a custom authority and should precede
      // others.
      if (g2.getAuthority() == null) {
        return -1;
      }

      if (g1.getAuthority() == null) {
        return 1;
      }

      return g1.getAuthority().compareTo(g2.getAuthority());
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append(": ");
    sb.append("Username: ").append(this.username).append("; ");
    sb.append("Password: [PROTECTED]; ");
    sb.append("Enabled: ").append(this.enabled).append("; ");
    sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
    sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
    sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
    sb.append("Families: ").append(this.families).append("; ");
    sb.append("Profil: ").append(this.profil).append("; ");

    if (!authorities.isEmpty()) {
      sb.append("Granted Authorities: ");

      boolean first = true;
      for (final GrantedAuthority auth : authorities) {
        if (!first) {
          sb.append(",");
        }
        first = false;

        sb.append(auth);
      }
    } else {
      sb.append("Not granted any authorities");
    }

    return sb.toString();
  }

  public String getProfil() {
    return profil;
  }

  public void setProfil(String profil) {
    this.profil = profil;
  }

  public List<Long> getFamilies() {
    return families;
  }

  public void setFamilies(List<Long> families) {
    this.families = families;
  }

  /**
   * Creates a UserBuilder with a specified user name
   *
   * @param username the username to use
   * @return the UserBuilder
   */
  public static MyUserBuilder withMyUsername(String username) {
    return myBuilder().username(username);
  }

  /**
   * Creates a UserBuilder
   *
   * @return the UserBuilder
   */
  public static MyUserBuilder myBuilder() {
    return new MyUserBuilder();
  }

  /**
   * Builds the user to be added. At minimum the username, password, and authorities should
   * provided. The remaining attributes have reasonable defaults.
   */
  public static class MyUserBuilder {
    private String profil;
    private List<Long> families;
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private boolean disabled;
    private UnaryOperator<String> passwordEncoder = myPassword -> myPassword;

    /** Creates a new instance */
    private MyUserBuilder() {}

    /**
     * Populates the profil. This attribute is required.
     *
     * @param profil the profil. Cannot be null.
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder profil(String profil) {
      this.profil = profil;
      return this;
    }

    /**
     * Populates the families. This attribute is required.
     *
     * @param id the families. Cannot be null.
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder families(List<Long> id) {
      this.families = id;
      return this;
    }

    /**
     * Populates the username. This attribute is required.
     *
     * @param username the username. Cannot be null.
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder username(String username) {
      Assert.notNull(username, "username cannot be null");
      this.username = username;
      return this;
    }

    /**
     * Populates the password. This attribute is required.
     *
     * @param password the password. Cannot be null.
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder password(String password) {
      Assert.notNull(password, "password cannot be null");
      this.password = password;
      return this;
    }

    /**
     * Encodes the current password (if non-null) and any future passwords supplied to {@link
     * #password(String)}.
     *
     * @param encoder the encoder to use
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder passwordEncoder(UnaryOperator<String> encoder) {
      Assert.notNull(encoder, "encoder cannot be null");
      this.passwordEncoder = encoder;
      return this;
    }

    /**
     * Populates the roles. This method is a shortcut for calling {@link #authorities(String...)},
     * but automatically prefixes each entry with "ROLE_". This means the following: <code>
     *     builder.roles("USER","ADMIN");
     * </code> is equivalent to <code>
     *     builder.authorities("ROLE_USER","ROLE_ADMIN");
     * </code>
     *
     * <p>This attribute is required, but can also be populated with {@link
     * #authorities(String...)}.
     *
     * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot be null, contain null
     *     values or start with "ROLE_"
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder roles(String... roles) {
      final List<GrantedAuthority> myAuthorities = new ArrayList<>(roles.length);
      for (final String role : roles) {
        Assert.isTrue(
            !role.startsWith("ROLE_"),
            role + " cannot start with ROLE_ (it is automatically added)");
        myAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
      }
      return authorities(myAuthorities);
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user. Cannot be null, or contain null values
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     * @see #roles(String...)
     */
    public MyUserBuilder authorities(GrantedAuthority... authorities) {
      return authorities(Arrays.asList(authorities));
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user. Cannot be null, or contain null values
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     * @see #roles(String...)
     */
    public MyUserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
      this.authorities = new ArrayList<>(authorities);
      return this;
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user (i.e. ROLE_USER, ROLE_ADMIN, etc). Cannot be
     *     null, or contain null values
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     * @see #roles(String...)
     */
    public MyUserBuilder authorities(String... authorities) {
      return authorities(AuthorityUtils.createAuthorityList(authorities));
    }

    /**
     * Defines if the account is expired or not. Default is false.
     *
     * @param accountExpired true if the account is expired, false otherwise
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder accountExpired(boolean accountExpired) {
      this.accountExpired = accountExpired;
      return this;
    }

    /**
     * Defines if the account is locked or not. Default is false.
     *
     * @param accountLocked true if the account is locked, false otherwise
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder accountLocked(boolean accountLocked) {
      this.accountLocked = accountLocked;
      return this;
    }

    /**
     * Defines if the credentials are expired or not. Default is false.
     *
     * @param credentialsExpired true if the credentials are expired, false otherwise
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder credentialsExpired(boolean credentialsExpired) {
      this.credentialsExpired = credentialsExpired;
      return this;
    }

    /**
     * Defines if the account is disabled or not. Default is false.
     *
     * @param disabled true if the account is disabled, false otherwise
     * @return the {@link MyUserBuilder} for method chaining (i.e. to populate additional attributes
     *     for this user)
     */
    public MyUserBuilder disabled(boolean disabled) {
      this.disabled = disabled;
      return this;
    }

    public UserDetails build() {
      final String encodedPassword = this.passwordEncoder.apply(password);
      return new CustomUser(
          profil,
          families,
          username,
          encodedPassword,
          !disabled,
          !accountExpired,
          !credentialsExpired,
          !accountLocked,
          authorities);
    }
  }
}

/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.User;
import java.util.List;

/**
 * Interface provides methods required to link {@link User} entity to a data source.
 *
 * @author apierre
 */
public interface IUserDao extends IGenericDao<User, Long> {

  /**
   * Gets a user by its mail and password.
   *
   * @param email, user mail
   * @param password, user password
   * @return the user
   */
  User getUserByEmailAndPassword(String email, String password);

  /**
   * Gets a user by its mail.
   *
   * @param email, user mail
   * @return the user
   */
  User getUserByEmail(String email);

  /**
   * Gets a user by its id, for anyone who asks.
   *
   * @param id, user id
   * @return the user
   */
  User safeFetchById(Long id);

  /**
   * The method updates the user received as parameter from the data source. The user has to exist
   * in the data source if you want the operation to success.
   *
   * @param user user to update in the database.
   */
  void safeUpdate(User user);

  /**
   * Fetches all unassigned users, for anyone who asks.
   *
   * @return list of all users
   */
  List<User> safeFetchAllUnassignedUsers();
}

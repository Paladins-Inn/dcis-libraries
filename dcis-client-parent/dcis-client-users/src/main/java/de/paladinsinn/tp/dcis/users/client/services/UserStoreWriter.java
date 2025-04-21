package de.paladinsinn.tp.dcis.users.client.services;


import de.paladinsinn.tp.dcis.users.client.model.user.User;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author klenkes74
 * @since 20.04.25
 */
public interface UserStoreWriter {
  /**
   * @param user The user data to write.
   * @return The user as written in the local store.
   */
  User write(@NotNull final User user);
  
  /**
   * @param id The ID of the user to be deleted.
   */
  void delete(@NotNull final UUID id);
}

package de.paladinsinn.tp.dcis.users.client.services;


import de.paladinsinn.tp.dcis.users.client.model.User;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * @author klenkes74
 * @since 20.04.25
 */
public interface UserStoreReader {
  /**
   * @param id The ID of the user to be read.
   * @return The user from the user store.
   */
  Optional<User> findById(@NotNull final UUID id);
  
  /**
   * @param namespace The namespace of the user to be loaded.
   * @param name The name of the user to be loaded.
   * @return The user from the user store.
   */
  Optional<User> findByUsername(@NotNull final String namespace, @NotNull final String name);
  
  /**
   * @param issuer The IDP of the user.
   * @param subject The subject at the IDP of the user.
   * @return The user from the user store.
   */
  Optional<User> findByLogin(@NotNull final String issuer, @NotNull final String subject);
}

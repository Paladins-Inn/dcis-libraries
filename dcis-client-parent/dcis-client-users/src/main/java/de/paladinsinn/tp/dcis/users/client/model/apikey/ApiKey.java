/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.users.client.model.apikey;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsBannedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDeletedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDetainedException;
import de.paladinsinn.tp.dcis.users.client.model.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The user of the DCIS system.
 */
@JsonDeserialize(as = ApiKeyImpl.class)
public interface ApiKey extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
  User getUser();
  
  /**
   * @return The expiration time of this API key.
   */
  OffsetDateTime getExpiration();
  
  /**
   * @return if the API key is expired.
   */
  default boolean isExpired() {
    return getExpiration().isAfter(OffsetDateTime.now());
  }
  
  /**
   * @return true if the user is banned from the system.
   */
  default boolean isBanned() {
    return getUser().isBanned();
  }
  
  /**
   * @return true if the user is detained.
   */
  default boolean isDetained() {
    return getUser().isDetained();
  }
  
  /**
   * @return true if the user is deleted.
   */
  default boolean isDeleted() {
    return getUser().isDeleted();
  }
  
  /**
   * @return true if the user is inactive for any reason.
   */
  default boolean isInactive() {
    return (isDeleted() || isBanned() || isDetained()) || isExpired();
  }
  
  
  /**
   * Checks if the user is inactive.

   * @throws UserIsBannedException if the user is banned.
   * @throws UserIsDeletedException if the user is deleted.
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkInactive() throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException, ApiKeyIsExpiredException {
    checkExpired();
    checkBanned();
    checkDeleted();
    checkDetained();
  }
  
  default void checkExpired() throws ApiKeyIsExpiredException {
    if (isExpired()) {
      throw new ApiKeyIsExpiredException(getUser(), getId(), getExpiration());
    }
  }
  
  /**
   * Checks if the user is banned.
   *
   * @throws UserIsBannedException if the user is banned
   */
  default void checkBanned() throws UserIsBannedException {
    if (isBanned()) {
      throw new UserIsBannedException(getUser());
    }
  }
  
  /**
   * Checks if the user is deleted.
   *
   * @throws UserIsDeletedException if the user is deleted.
   */
  default void checkDeleted() throws UserIsDeletedException {
    if (isDeleted() && !isBanned()) {
      throw new UserIsDeletedException(getUser());
    }
  }
  
  /**
   * Checks if the user is detained.
   *
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkDetained() throws UserIsDetainedException {
    if (isDetained()) {
      throw new UserIsDetainedException(getUser());
    }
  }
}
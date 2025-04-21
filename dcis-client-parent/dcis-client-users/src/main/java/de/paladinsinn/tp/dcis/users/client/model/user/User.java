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

package de.paladinsinn.tp.dcis.users.client.model.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsBannedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDeletedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDetainedException;
import de.paladinsinn.tp.dcis.users.client.model.user.state.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The user of the DCIS system.
 */
@JsonDeserialize(as = UserImpl.class)
public interface User extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
  /**
   * @return The IDP issuer of this user.
   */
  String getIssuer();
  
  /**
   * @return The IDP subject of this user.
   */
  String getSubject();
  
  
  /**
   * @return The period the user has been detained for.
   */
  Duration getDetainmentDuration();
  
  /**
   * @return The end of the user detainment.
   */
  OffsetDateTime getDetainedTill();
  
  /**
   * Detains the user for a number of days.
   *
   * @param days The number of days the user is detained within the system.
   * @return the user
   */
  User detain(@Min(1) @Max(1095) long days);
  
  /**
   * Release the user from detainment.
   *
   * @return the user
   */
  User release();
  
  /**
   * Ban the user from the system.
   *
   * @return the user
   */
  User ban();
  
  /**
   * Unban the user from the system.
   *
   * @return the user
   */
  User unban();
  
  /**
   * delete the user.
   *
   * @return  the user
   */
  User delete();
  
  /**
   * undelete the user
   *
   * @return the user
   */
  User undelete();
  
  /**
   * @return true if the user is banned from the system.
   */
  boolean isBanned();
  
  /**
   * @return true if the user is detained.
   */
  default boolean isDetained() {
    return getDetainedTill() != null;
  }
  
  /**
   * @return true if the user is deleted.
   */
  default boolean isDeleted() {
    return getDeleted() != null && !isBanned();
  }
  
  /**
   * @return true if the user is inactive for any reason.
   */
  default boolean isInactive() {
    return (isDeleted() || isBanned() || isDetained());
  }
  
  /**
   * Creates the user state from the user data itself.
   *
   * @param bus the event bus to be used during state changes.
   * @return the current user state.
   */
  default UserState getState(EventBus bus) {
    if (isDeleted()) {
      return DeletedUser.builder().user(this).bus(bus).build();
    }
    
    if (isBanned()) {
      return BannedUser.builder().user(this).bus(bus).build();
    }
    
    if (isDetained()) {
      return DetainedUser.builder().user(this).bus(bus).build();
    }
    
    return ActiveUser.builder().user(this).bus(bus).build();
  }
  
  /**
   * Checks if the user is inactive.

   * @throws UserIsBannedException if the user is banned.
   * @throws UserIsDeletedException if the user is deleted.
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkInactive() throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException {
    checkBanned();
    checkDeleted();
    checkDetained();
  }
  
  /**
   * Checks if the user is banned.
   *
   * @throws UserIsBannedException if the user is banned
   */
  default void checkBanned() throws UserIsBannedException {
    if (isBanned()) {
      throw new UserIsBannedException(this);
    }
  }
  
  /**
   * Checks if the user is deleted.
   *
   * @throws UserIsDeletedException if the user is deleted.
   */
  default void checkDeleted() throws UserIsDeletedException {
    if (isDeleted() && !isBanned()) {
      throw new UserIsDeletedException(this);
    }
  }
  
  /**
   * Checks if the user is detained.
   *
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkDetained() throws UserIsDetainedException {
    if (isDetained()) {
      throw new UserIsDetainedException(this);
    }
  }
}
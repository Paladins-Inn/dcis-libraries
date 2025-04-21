/*
 * Copyright (c) 2025.  Kaiserpfalz EDV-Service, Roland T. Lichti
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
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.paladinsinn.tp.dcis.users.client.model.apikey;


import de.paladinsinn.tp.dcis.users.client.authentication.UserIsInactiveException;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The given API key has expired.
 *
 * @author klenkes74
 * @since 21.04.25
 */
@Getter
@ToString(callSuper = true)
public class ApiKeyIsExpiredException extends UserIsInactiveException {
  private final UUID id;
  private final OffsetDateTime expiration;
  
  public ApiKeyIsExpiredException(@Nullable final User user, final UUID id, final OffsetDateTime expiresAt) {
    super(user, "API key " + id + " is expired since " + expiresAt);
    
    this.id = id;
    this.expiration = expiresAt;
  }
}

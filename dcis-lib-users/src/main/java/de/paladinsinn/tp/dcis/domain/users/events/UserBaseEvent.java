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
package de.paladinsinn.tp.dcis.domain.users.events;



import de.paladinsinn.tp.dcis.commons.events.DcisBaseEvent;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-11-05
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class UserBaseEvent extends DcisBaseEvent {
  @Builder.Default
  final OffsetDateTime timestamp = OffsetDateTime.now();
  
  @ToString.Include
  final private String system;
  
  @ToString.Include
  final private User user;
  
  @Override
  public  Object[] getI18nData() {
    return new Object[] {
        timestamp,
        system,
        user.getId(),
        user.getNameSpace(),
        user.getName(),
        user.getCreated(),
        user.getModified(),
        user.getDeleted(),
        user.getDetainedTill(),
        user.getDetainmentDuration()
    };
  }
}

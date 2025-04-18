/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.domain.users.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

import java.io.Serializable;
import java.util.UUID;

@JsonDeserialize(as = UserLogEntryImpl.class)
public interface UserLogEntry extends HasId<UUID>, HasTimestamps, Serializable {
    User getUser();

    String getSystem();
    String getText();
    
    String getComment();
    
    default Object[] getI18nData() {
        return new Object[] {
            getCreated(),
            getSystem(),
            getUser().getId(),
            getUser().getNameSpace(),
            getUser().getName(),
            getUser().getCreated(),
            getUser().getModified(),
            getUser().getDeleted(),
            getUser().getDetainedTill(),
            getUser().getDetainmentDuration()
        };
    }
}

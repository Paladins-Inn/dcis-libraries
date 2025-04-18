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

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserLogEntryImpl implements UserLogEntry {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime deleted;
    
    private User user;

    private String system;
    private String text;
    private String comment;
}

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

package de.paladinsinn.tp.dcis.domain.users.persistence;

import de.kaiserpfalzedv.commons.jpa.AbstractRevisionedJPAEntity;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

import java.time.*;
import java.util.UUID;

/**
 * The player
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "USERS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@XSlf4j
public class UserJPA extends AbstractRevisionedJPAEntity<UUID> implements User {
    @Nullable
    @Column(name = "DETAINED_DURATION")
    private Duration detainmentDuration;
    
    @Nullable
    @Column(name = "DETAINED_TILL")
    private OffsetDateTime detainedTill;
    
    @NotNull
    @Column(name = "BANNED", nullable = false)
    @Builder.Default
    private boolean banned = false;
    
    /** The namespace this player is registered for. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", nullable = false, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the player. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", nullable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;
    
    @Override
    public UserJPA detain(@Min(1) @Max(1095) long days) {
        log.entry(days);
        
        detainmentDuration = Duration.ofDays(days);
        
        detainedTill = LocalDate.now()
            .atStartOfDay(ZoneId.of("UTC"))
            .plusDays(1+ days) // today end of day (1) + days
            .toOffsetDateTime();
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA release() {
        log.entry();
        
        detainmentDuration = null;
        detainedTill = null;
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA ban() {
        log.entry();
        
        this.banned = true;
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA unban() {
        log.entry();
        
        this.banned = false;
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA delete() {
        log.entry();
        
        this.deleted = OffsetDateTime.now(Clock.systemUTC());
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA undelete() {
        log.entry();
        
        this.deleted = null;
        
        return log.exit(this);
    }
}

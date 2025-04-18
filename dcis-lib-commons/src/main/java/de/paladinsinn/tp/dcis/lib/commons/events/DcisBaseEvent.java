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

package de.paladinsinn.tp.dcis.lib.commons.events;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public abstract class DcisBaseEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private final UUID id = UUID.randomUUID();
    
    abstract public String getI18nKey();
    abstract public Object[] getI18nData();
}

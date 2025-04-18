/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.paladinsinn.tp.dcis.commons.events;


import lombok.Getter;

import java.util.UUID;


/**
 * @author klenkes74
 * @since 24.11.24
 */
@Getter
public class TestEvent extends DcisBaseEvent {
  private final String i18nKey = "event.test";
  
  public TestEvent(final UUID id) {
    super(id);
  }
  
  public Object[] getI18nData() {
    return new Object[] {};
  }
}

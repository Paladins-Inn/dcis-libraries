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

package de.paladinsinn.tp.dcis.commons.events;


import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.XSlf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


/**
 * An event bus that won't send any events.
 * It's a trap!
 *
 * @author klenkes74
 * @since 12.04.25
 */
@Service
@Scope("singleton")
@Order(1010)
@XSlf4j
public class FakeEventBus extends EventBus {
  @Override
  public void post(final @NotNull Object event) {
    log.entry(event);
    log.exit();
  }
}

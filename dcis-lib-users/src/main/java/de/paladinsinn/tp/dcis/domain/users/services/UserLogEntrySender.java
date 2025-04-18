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
package de.paladinsinn.tp.dcis.domain.users.services;

import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLogoutEvent;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;


/**
 *  This service reports all user login event to the AMQP queue for user logins.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.1.0
 * @since 2024-11-05
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserLogEntrySender {
  private static final String sinkName = "user-events";
  
  @Value("${spring.application.name:unknown}")
  private String application;

  /** The messaging infrastructure. */
  private final StreamBridge streamBridge;

  
  @PostConstruct
  public void init() {
    log.entry(streamBridge);

    log.exit();
  }

  
  @PreDestroy
  public void shutdown() {
    log.entry(streamBridge);
    
    log.exit();
  }

  
  @SuppressWarnings("unused") // It is used by the event bus
  @Counted
  @Timed
  public void send(final UserLoginEvent event) {
    log.entry(streamBridge, event);

    streamBridge.send(sinkName, event.toBuilder().system(application).build());
  
    log.info("User login has been reported to central logger. user={}, application={}", event.getUser(), application);

    log.exit();
  }
  
  @SuppressWarnings("unused") // It is used by the event bus
  @Counted
  @Timed
  public void send(final UserLogoutEvent event) {
    log.entry(streamBridge, event);
    
    streamBridge.send(sinkName, event.toBuilder().system(application).build());
      
    log.info("User logout has been reported to central logger. user={}, application={}", event.getUser(), application);
    
    log.exit();
  }
}

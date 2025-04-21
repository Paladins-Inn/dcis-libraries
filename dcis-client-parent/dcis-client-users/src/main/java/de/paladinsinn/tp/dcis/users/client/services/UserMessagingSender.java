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
package de.paladinsinn.tp.dcis.users.client.services;

import com.google.common.eventbus.Subscribe;
import de.paladinsinn.tp.dcis.lib.messaging.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.client.events.UserBaseEvent;
import de.paladinsinn.tp.dcis.users.client.events.UserEventsHandler;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.client.events.apikey.ApiKeyCreatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.apikey.ApiKeyRevokedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserPetitionedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserRemovedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserMessagingSender implements UserEventsHandler {
  private static final String sinkName = "users-modification";
  
  @Value("${spring.application.name:unknown}")
  private String application;

  private final LoggingEventBus bus;

  /** The messaging infrastructure. */
  private final StreamBridge streamBridge;

  
  @PostConstruct
  public void init() {
    log.entry(streamBridge, bus, application);

    bus.register(this);
    
    log.exit();
  }

  
  @PreDestroy
  public void shutdown() {
    log.entry(streamBridge, bus, application);
    
    bus.unregister(this);
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserActivatedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserCreatedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserDeletedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserRemovedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserBannedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserDetainedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserPetitionedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserReleasedEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserLoginEvent event) {
    sendEvent(event);
  }
  
  @Subscribe
  @Override
  public void event(final UserLogoutEvent event) {
    sendEvent(event);
  }
  
  @Override
  public void event(final ApiKeyCreatedEvent event) {
    sendEvent(event);
  }
  
  @Override
  public void event(final ApiKeyRevokedEvent event) {
    sendEvent(event);
  }
  
  private <T extends UserBaseEvent> void sendEvent(final T event) {
    log.entry(event.getSystem(), event.getUser().getId(), event.getI18nKey());
    
    if (! event.getSystem().equals(application)) {
      log.exit("I send only events from this system! from=" + event.getSystem() + ", system=" + application);
      return;
    }
    
    streamBridge.send(sinkName, event);
    
    log.exit("Event to " + sinkName + " sent for " + event.getSystem());
  }
}

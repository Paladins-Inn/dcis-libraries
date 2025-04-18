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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import de.paladinsinn.tp.dcis.commons.formatter.EnableKaiserpfalzCommonsSpringFormatters;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import de.paladinsinn.tp.dcis.domain.users.model.UserToImplImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserToJpaImpl;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAspectsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SuppressWarnings("LoggingSimilarMessage")
@SpringBootTest
@ActiveProfiles({"test"})
@EnableAutoConfiguration(exclude = {
    MetricsAspectsAutoConfiguration.class,
    MetricsAutoConfiguration.class,
})
@EnableTestBinder
@EnableJpaRepositories(basePackages = {"de.paladinsinn.tp.dcis.domain.users"})
@EntityScan(basePackages = {"de.paladinsinn.tp.dcis.domain.users"})
@EnableEventBus
@EnableUserManagement
@EnableKaiserpfalzCommonsSpringFormatters
@Import({
    UserToImplImpl.class,
    UserToJpaImpl.class
})
@XSlf4j
public class UserLogEntryIT {
  
  @Autowired
  private OutputDestination outputDestination;
  
  @Autowired
  private UserLoggedInStateRepository userState;
  
  @Autowired
  private UserLogEntryClient sut;
  
  @Autowired
  private EventBus bus;
  
  @Value("${spring.application.name:Mary}")
  private String application;
  
  @Autowired
  private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
  
  private static final String sinkName = "user-events";
  
  @Test
  public void shouldSendLoginWhenUserIsNotLoggedInYet() throws IOException {
    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, loginEvent);
    
    bus.post(loginEvent);

    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserLoginEvent received = getUserLoginEvent(result.getPayload());
      //noinspection LoggingSimilarMessage
      log.trace("Received via stream: event={}", received);
      
    assertEquals(loginEvent, received);
    assertEquals(loginEvent.getSystem(), received.getSystem());
    assertEquals(loginEvent.getUser().getName(), received.getUser().getName());
    assertEquals(loginEvent.getUser().getNameSpace(), received.getUser().getNameSpace());
    
    log.exit("success");
  }
  
  @Test
  public void shouldNotSendLoginWhenUserIsAlreadyLoggedIn() {
    log.entry("shouldNotSendLoginWhenUserIsAlreadyLoggedIn");
    
    User user = createDefaultUser();
    
    userState.login(user);

    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(user)
        .system(application)
        .build();
    
    bus.post(loginEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);

    assertNull(result);
  }
  
  
  private static UserImpl createDefaultUser() {
    return UserImpl.builder()
        .name("Peter")
        .nameSpace("Paul")
        .build();
  }
  
  private UserLoginEvent getUserLoginEvent(byte[] payload) throws IOException {
    ObjectMapper mapper = jackson2ObjectMapperBuilder.build();
    
    return log.exit(mapper.readValue(payload, UserLoginEvent.class));
  }
  
  
  @Test
  public void shouldSendTheLogoutEvent() throws IOException {
    UserLogoutEvent logoutEvent = UserLogoutEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    bus.post(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserLogoutEvent received = getUserLogoutEvent(result.getPayload());
      //noinspection LoggingSimilarMessage
      log.trace("Received via stream: event={}", received);
    
    assertEquals(logoutEvent, received);
    assertEquals(logoutEvent.getSystem(), received.getSystem());
    assertEquals(logoutEvent.getUser().getName(), received.getUser().getName());
    assertEquals(logoutEvent.getUser().getNameSpace(), received.getUser().getNameSpace());
    
    log.exit("success");
  }
  
  private UserLogoutEvent getUserLogoutEvent(byte[] payload) throws IOException {
    ObjectMapper mapper = jackson2ObjectMapperBuilder.build();
    
    return log.exit(mapper.readValue(payload, UserLogoutEvent.class));
  }
  
  
  @Test
  public void shouldNotReceiveEventsAfterShutdown() {
    log.entry("shouldNotReceiveEventsAfterShutdown");
    
    sut.shutdown();
    
    bus.post(UserLogoutEvent.builder().user(createDefaultUser()).build());
    
    sut.init();
  }
}

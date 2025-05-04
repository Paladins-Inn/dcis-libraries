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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.spring.security.EnableKeycloakSecurityIntegration;
import de.paladinsinn.tp.dcis.lib.messaging.events.EnableEventBus;
import de.paladinsinn.tp.dcis.lib.rest.EnableRestConfiguration;
import de.paladinsinn.tp.dcis.users.client.events.UserBaseEvent;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserImpl;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.scheduling.ScheduledTasksObservabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.server.EnableStubRunnerServer;
import org.springframework.cloud.contract.stubrunner.spring.cloud.consul.StubRunnerSpringCloudConsulAutoConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.cloud.eureka.StubRunnerSpringCloudEurekaAutoConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.cloud.loadbalancer.SpringCloudLoadBalancerAutoConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.cloud.zookeeper.StubRunnerSpringCloudZookeeperAutoConfiguration;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.Message;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SpringBootTest
@ActiveProfiles({"test"})
@EnableTestBinder
@ComponentScan(basePackages = {
    "de.paladinsinn.tp.dcis.users.client.events",
    "de.paladinsinn.tp.dcis.users.client.model"
})
@Import({
    UserMessagingSender.class
})
@EnableEventBus
@XSlf4j
public class UserMessagingSenderIT {
  private static final String sinkName = "users-modification";
  
  @Autowired
  private OutputDestination outputDestination;
  
  @Autowired
  private UserMessagingSender sut;
  
  @Value("${spring.application.name:unknown}")
  private String application;
  
  @Autowired
  private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
  

  @Test
  public void shouldSendTheLoginEvent() throws IOException {
    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, loginEvent);

    sut.event(loginEvent);

    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserLoginEvent received = getUserEvent(result.getPayload(), UserLoginEvent.class);
    
    verifyReceivedEvent(loginEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheLogoutEvent() throws IOException {
    UserLogoutEvent logoutEvent = UserLogoutEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);

    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserLogoutEvent.class);
    verifyReceivedEvent(logoutEvent, received);

    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheCreateEvent() throws IOException {
    UserCreatedEvent logoutEvent = UserCreatedEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserCreatedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheActivationEvent() throws IOException {
    UserActivatedEvent logoutEvent = UserActivatedEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserActivatedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheDeletionEvent() throws IOException {
    UserDeletedEvent logoutEvent = UserDeletedEvent.builder()
        .user(createDefaultUser().delete())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserDeletedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheRemovingEvent() throws IOException {
    UserRemovedEvent logoutEvent = UserRemovedEvent.builder()
        .user(createDefaultUser().delete())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserRemovedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheDetainedEvent() throws IOException {
    UserDetainedEvent logoutEvent = UserDetainedEvent.builder()
        .user(createDefaultUser().detain(100))
        .days(100)
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserDetainedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheBanningEvent() throws IOException {
    UserBannedEvent logoutEvent = UserBannedEvent.builder()
        .user(createDefaultUser().ban())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserBannedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheReleasingEvent() throws IOException {
    UserReleasedEvent logoutEvent = UserReleasedEvent.builder()
        .user(createDefaultUser().release())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserReleasedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  private static User createDefaultUser() {
    return UserImpl.builder()
        .id(UUID.randomUUID())
        .name("Peter")
        .nameSpace("Paul")
        .build();
  }
  
  private <T extends UserBaseEvent> T getUserEvent(byte[] payload, Class<T> clasz) throws IOException {
    ObjectMapper mapper = jackson2ObjectMapperBuilder.build();
    
    log.trace("Reading payload to event. payload={}", new String(payload));
    
    return log.exit(mapper.readValue(payload, clasz));
  }
  
  private static void verifyReceivedEvent(final UserBaseEvent expected, final UserBaseEvent actual) {
    log.trace("Received via stream: event={}", actual);
    
    assertEquals(expected, actual);
    assertEquals(expected.getSystem(), actual.getSystem());
    assertEquals(expected.getUser().getId(), actual.getUser().getId());
    assertEquals(expected.getUser().getName(), actual.getUser().getName());
    assertEquals(expected.getUser().getNameSpace(), actual.getUser().getNameSpace());
  }
}

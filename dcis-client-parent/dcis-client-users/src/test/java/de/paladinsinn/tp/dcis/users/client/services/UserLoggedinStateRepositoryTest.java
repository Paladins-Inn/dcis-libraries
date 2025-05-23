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


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.lib.messaging.events.EnableEventBus;
import de.paladinsinn.tp.dcis.lib.ui.formatter.EnableKaiserpfalzCommonsSpringFormatters;
import de.paladinsinn.tp.dcis.users.client.authentication.UserLoggedInStateRepository;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserImpl;
import de.paladinsinn.tp.dcis.users.client.model.user.UserToImplImpl;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SpringBootTest
@Import({
    UserToImplImpl.class
})
@ComponentScan(basePackages = {"de.paladinsinn.tp.dcis.users.client"})
@XSlf4j
public class UserLoggedinStateRepositoryTest {
  
  @Autowired
  private UserLoggedInStateRepository sut;
  
  private static final User USER = UserImpl.builder()
      .id(UUID.randomUUID())
      .nameSpace("Peter")
      .name("Paul")
      .created(OffsetDateTime.now(ZoneId.systemDefault()).minusHours(5L))
      .modified(OffsetDateTime.now(ZoneId.systemDefault()))
      .build();
  
  @BeforeEach
  public void setUp() {
    log.trace("Purging users before each single test.");
    sut.purgeAllUsers();
  }
  
  @Test
  public void shouldNotFindAUserWhenNoOneIsLoggedIn() {
    log.entry("shouldNotFindAUserWhenNoOneIsLoggedIn");
    
    assertFalse(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldFindAUserWhenLoggedIn() {
    log.entry("shouldFindAUserWhenLoggedIn");
    
    sut.login(USER);
    
    assertTrue(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldUpdateAUserWhenLoggingInTwice() {
    log.entry("shouldUpdateAUserWhenLoggingInTwice");
    
    sut.login(USER);
    sut.login(USER);
    
    assertTrue(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldNotFindAUserWhenLoggedOut() {
    log.entry("shouldNotFindAUserWhenLoggedOut");
    
    sut.login(USER);
    sut.logout(USER);
    
    assertFalse(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldNotFindAUserWhenInactiveTooLong() {
    log.entry("shouldNotFindAUserWhenInactiveTooLong");
    
    sut.login(USER, Instant.now().minusSeconds(UserLoggedInStateRepository.INACTIVITY_LIMIT_IN_SECONDS + 1));
    
    assertFalse(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldNotFindPurgedUsersWhenLoggedIn() {
    log.entry("shouldNotFindPurgedUsersWhenLoggedIn");
    
    sut.login(USER, Instant.now().minusSeconds(UserLoggedInStateRepository.INACTIVITY_LIMIT_IN_SECONDS + 1));
    
    sut.purgeInactiveUsers();
    
    assertFalse(sut.isLoggedIn(USER));
  }
  
  @Test
  public void shouldNotFindAnyUserAfterShutdown() {
    log.entry("shouldNotFindAnyUserAfterShutdown");
    
    sut.login(USER);
    
    sut.shutdown();
    
    assertFalse(sut.isLoggedIn(USER));
  }
  
  
  @SpringBootApplication(
      scanBasePackages = {
        "de.paladinsinn.tp.dcis.lib.messaging.events",
        "de.paladinsinn.tp.dcis.lib.ui.formatter",
        "de.paladinsinn.tp.dcis.users.client.services"
      }
  )
  @EnableTestBinder
  @EnableEventBus
  @EnableUserClient
  @EnableKaiserpfalzCommonsSpringFormatters
  public static class Application {}
}

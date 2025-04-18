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


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLogoutEvent;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    MetricsAspectsAutoConfiguration.class,
    MetricsAutoConfiguration.class,
})
@Import({
    UserToImplImpl.class,
    UserToJpaImpl.class
})
@EnableJpaRepositories(basePackages = {"de.paladinsinn.tp.dcis.domain.users"})
@EntityScan(basePackages = {"de.paladinsinn.tp.dcis.domain.users"})
@XSlf4j
public class UserLogEntrySenderTest {
  
  @Value("${spring.application.name:Mary}")
  private String application;
  
  @Autowired
  private UserLogEntrySender sut;
  
  
  @Test
  public void shouldSendTheLoginEvent() {
    log.entry("shouldSendTheLoginEvent");

    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    
    sut.send(loginEvent);
  }
  
  @Test
  public void shouldSendTheLogoutEvent() {
    log.entry("shouldSendTheLogoutEvent");
    
    UserLogoutEvent logoutEvent = UserLogoutEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();

    sut.send(logoutEvent);
  }
  
  @Test
  public void shouldCompleteShutdownOfSender() {
    log.entry("shouldCompleteShutdownOfSender");
    
    sut.shutdown();
  }
  
  private static UserImpl createDefaultUser() {
    return UserImpl.builder()
        .name("Peter")
        .nameSpace("Paul")
        .build();
  }
}

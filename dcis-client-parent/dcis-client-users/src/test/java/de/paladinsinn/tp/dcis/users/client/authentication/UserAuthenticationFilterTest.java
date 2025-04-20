/*
 * Copyright (c) 2025.  Kaiserpfalz EDV-Service, Roland T. Lichti
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
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.paladinsinn.tp.dcis.users.client.authentication;


import de.paladinsinn.tp.dcis.lib.messaging.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.client.BaseUserException;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.client.model.UserImpl;
import de.paladinsinn.tp.dcis.users.client.services.UserCantBeCreatedException;
import de.paladinsinn.tp.dcis.users.client.services.UserClient;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74
 * @since 20.04.25
 */
@XSlf4j
public class UserAuthenticationFilterTest {
  private static final UserImpl PLAYER = UserImpl.builder()
      .id(UUID.randomUUID())
      .issuer("https://test.issuer")
      .subject(UUID.randomUUID().toString())
      .nameSpace("test")
      .name("Mary")
      .build();
  
  private UserAuthenticationFilter sut;
  
  private LoggingEventBus bus;
  private UserClient client;
  private Authentication authentication;
  
  
  @BeforeEach
  public void setUp() {
    bus = Mockito.mock(LoggingEventBus.class);
    client = Mockito.mock(UserClient.class);
    authentication = Mockito.mock(Authentication.class);
    
    sut = new UserAuthenticationFilter(bus, client);
  }
  
  @AfterEach
  public void tearDown() {
  }
  
  
  @Test
  public void shouldAuthenticateWhenUserIsIsActive() throws BaseUserException {
    log.entry();
    
    
    when(client.login(any(Authentication.class)))
        .thenReturn(Optional.of(PLAYER));
    
    sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    
    verify(bus, times(1))
        .post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  @Test
  public void shouldNotAuthenticateWhenUserIsIsDeleted() throws BaseUserException {
    log.entry();
    
    UserImpl user = PLAYER.toBuilder()
        .deleted(OffsetDateTime.now().minusDays(2L))
        .build();
    
    when(client.login(any(Authentication.class)))
        .thenThrow(new UserIsDeletedException(user));
    
    try {
      sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    } catch (IllegalStateException e) {
      checkForException(e, UserIsDeletedException.class);
    }
    
    verify(bus, never()).post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  
  @Test
  public void shouldNotAuthenticateWhenUserIsIsBanned() throws BaseUserException {
    log.entry();
    
    UserImpl user = PLAYER.toBuilder()
        .deleted(OffsetDateTime.now().minusDays(2L))
        .banned(true)
        .build();
    
    when(client.login(any(Authentication.class)))
        .thenThrow(new UserIsBannedException(user));
    
    try {
      sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    } catch (IllegalStateException e) {
      checkForException(e, UserIsBannedException.class);
    }
    
    verify(bus, never()).post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  
  @Test
  public void shouldNotAuthenticateWhenUserIsIsDetained() throws BaseUserException {
    log.entry();
    
    UserImpl user = PLAYER.toBuilder()
        .detainedTill(OffsetDateTime.now().plusDays(2L))
        .detainmentDuration(Duration.of(100L, ChronoUnit.DAYS))
        .banned(true)
        .build();
    
    when(client.login(any(Authentication.class)))
        .thenThrow(new UserIsDetainedException(user));
    
    try {
      sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    } catch (IllegalStateException e) {
      checkForException(e, UserIsDetainedException.class);
    }
    
    verify(bus, never()).post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  
  @Test
  public void shouldAuthenticateWhenUserIsFreshlyCreated() throws BaseUserException {
    log.entry();
    
    when(client.login(any(Authentication.class)))
        .thenReturn(Optional.empty());
    when(client.create(any(Authentication.class)))
        .thenReturn(Optional.of(PLAYER));
//      .thenThrow(new UserCantBeCreatedException(PLAYER.getIssuer(), PLAYER.getSubject(), PLAYER.getName()));
    
    sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    
    verify(bus, times(1)).post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  
  @Test
  public void shouldAuthenticateWhenUserIsNotCreatable() throws BaseUserException {
    log.entry();
    
    when(client.login(any(Authentication.class)))
        .thenReturn(Optional.empty());
    when(client.create(any(Authentication.class)))
        .thenThrow(new UserCantBeCreatedException(PLAYER.getIssuer(), PLAYER.getSubject(), PLAYER.getName()));
    
    try {
      sut.onApplicationEvent(new AuthenticationSuccessEvent(authentication));
    } catch (IllegalStateException e) {
      checkForException(e, UserCantBeCreatedException.class);
    }
    
    verify(bus, never()).post(any(UserLoginEvent.class));
    
    log.exit();
  }
  
  
  /**
   * Checks if the cause of the exception has the correct class.
   *
   * @param cause The runtime exception which wraps the real cause.
   * @param clasz The exception class to check for.
   */
  private void checkForException(@NotNull final RuntimeException cause, Class<? extends BaseUserException> clasz) {
      if (clasz.isAssignableFrom(cause.getCause().getClass())) {
        log.debug("Correct exception thrown. class={}, reason={}",
            cause.getCause().getClass().getSimpleName(),
            cause.getCause().getMessage()
        );
        
        return;
      }
    
      throw cause;
  }
}

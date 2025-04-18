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

package de.paladinsinn.tp.dcis.domain.users.model;


import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author klenkes74
 * @since 06.04.25
 */
@XSlf4j
public class UserInactivityTest {
  private static final UUID USER_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now(Clock.systemUTC());
  private static final String NAMESPACE = "Peter";
  private static final String NAME = "Paul";
  
  private static final UserImpl DEFAULT_USER = UserImpl.builder()
      .id(USER_ID)
      .nameSpace(NAMESPACE)
      .name(NAME)
      .created(CREATED_AT)
      .build();
  
  private User sut;
  
  @BeforeEach
  public void setUp() {
    sut = DEFAULT_USER.toBuilder().build();
  }
  
  @Test
  public void shouldReturnAllValuesWhenPrompted() {
    log.entry(sut);
    
    logUser(sut);
    
    assertEquals(USER_ID, sut.getId(), "The ID is not as expected");
    assertEquals(NAMESPACE, sut.getNameSpace(), "The namespace is not as expected");
    assertEquals(NAME, sut.getName(), "The name is not as expected");
    assertEquals(CREATED_AT, sut.getCreated(), "The creation date is not as expected");
    assertFalse(sut.isInactive(), "The should not be inactive");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldBeInactiveWhenUserHasBeenBanned() {
    log.entry(sut);
    
    sut.ban();
    
    logUser(sut);
    
    assertTrue(sut.isBanned(), "The user has been banned!");
    assertFalse(sut.isDetained(), "The user has not been detained!");
    assertFalse(sut.isDeleted(), "The user has not been deleted!");
    assertTrue(sut.isInactive(), "The user has been banned!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldBeInactiveWhenUserHasBeenDetained() {
    log.entry(sut);
    
    sut.detain(20L);
    
    logUser(sut);
    
    assertTrue(sut.isDetained(), "The user has been detained!");
    assertFalse(sut.isBanned(), "The user has not been banned!");
    assertFalse(sut.isDeleted(), "The user has not been deleted!");
    assertTrue(sut.isInactive(), "The user has been detained!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldBeInactiveWhenUserHasBeenDeleted() {
    log.entry(sut);
    
    sut.delete();
    
    logUser(sut);
    
    assertFalse(sut.isBanned(), "The user has not been banned!");
    assertFalse(sut.isDetained(), "The user has not been detained!");
    assertTrue(sut.isDeleted(), "The user has been deleted!");
    assertTrue(sut.isInactive(), "The user has been deleted!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldNotBeInactiveWhenUserHasBeenReleased() {
    log.entry(sut);
    
    sut.detain(20L);
    sut.release();
    
    logUser(sut);
    
    assertFalse(sut.isBanned(), "The user has not been banned!");
    assertFalse(sut.isDetained(), "The user has been released!");
    assertFalse(sut.isDeleted(), "The user has not been deleted!");
    assertFalse(sut.isInactive(), "The user has been released!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldNotBeInactiveWhenUserHasBeenUnbanned() {
    log.entry(sut);
    
    sut.ban();
    sut.unban();
    
    logUser(sut);
    
    assertFalse(sut.isBanned(), "The user has been unbanned!");
    assertFalse(sut.isDetained(), "The user has not been detained!");
    assertFalse(sut.isDeleted(), "The user has not been deleted!");
    assertFalse(sut.isInactive(), "The user has been unbanned!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldNotBeInactiveWhenUserHasBeenUndeleted() {
    log.entry(sut);
    
    sut.delete();
    sut.undelete();
    
    logUser(sut);
    
    assertFalse(sut.isBanned(), "The user has not been banned!");
    assertFalse(sut.isDetained(), "The user has not been detained!");
    assertFalse(sut.isDeleted(), "The user has been undeleted!");
    assertFalse(sut.isInactive(), "The user has been undeleted!");
    
    log.exit(sut);
  }
  
  @Test
  public void shouldBeInactiveWhenUserHasBeenReleasedButBanned() {
    log.entry(sut);
    
    sut.ban();
    sut.detain(20L);
    
    sut.release();
    
    logUser(sut);
    
    assertTrue(sut.isBanned(), "The user has been banned!");
    assertFalse(sut.isDetained(), "The user has not been detained!");
    assertFalse(sut.isDeleted(), "The user has been undeleted!");
    assertTrue(sut.isInactive(), "The user has not been unbanned!");
    
    log.exit(sut);
  }
  
  private void logUser(final User sut) {
    log.entry(sut);
    
    log.info("SUT data. sut={}, inactive={}, deleted={}, banned={}, detained={}", sut, sut.isInactive(), sut.isDeleted(), sut.isBanned(), sut.isDetained());
    
    log.exit();
  }
}

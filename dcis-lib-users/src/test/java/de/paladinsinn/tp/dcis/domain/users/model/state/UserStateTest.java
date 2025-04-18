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
package de.paladinsinn.tp.dcis.domain.users.model.state;

import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserPetitionedEvent;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserStateTest {
  
  private User mockUser;
  private EventBus mockBus;
  private UserState sut;
  
  @BeforeEach
  void setUp() {
    mockUser = Mockito.mock(User.class);
    mockBus = Mockito.mock(EventBus.class);
    
    sut = generateUser();
    
  }
  
  private UserState generateUser() {
    Mockito.when(mockUser.isInactive()).thenReturn(false);
    Mockito.when(mockUser.isBanned()).thenReturn(false);
    Mockito.when(mockUser.isDetained()).thenReturn(false);
    Mockito.when(mockUser.isDeleted()).thenReturn(false);
    
    return new ActiveUser(mockUser, mockBus);
  }
  
  @Test
  void shouldReturnTrueWhenAskingForActivity() {
    assertTrue(sut.isActive());
  }
  
  @Test
  void shouldReturnFalseWhenAskingForInactivity() {
    assertFalse(sut.isInactive());
  }
  
  @Test
  void shouldReturnFalseWhenAskingForBeingBanned() {
    assertFalse(sut.isBanned());
  }
  
  @Test
  void shouldReturnFalseWhenAskingForBeingDetained() {
    assertFalse(sut.isDetained());
  }
  
  @Test
  void shouldReturnFalseWhenAskingForBeingDeleted() {
    assertFalse(sut.isDeleted());
  }

  @Test
  void shouldReturnSameUserWhenPetitioning() {
    UUID petition = UUID.randomUUID();
    UserState result = sut.petition(petition);
    
    assertEquals(sut, result);
    verify(mockBus, times(1))
        .post(any(UserPetitionedEvent.class));
  }
}


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
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BannedUserTest {
  
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
    Mockito.when(mockUser.isInactive()).thenReturn(true);
    Mockito.when(mockUser.isBanned()).thenReturn(true);
    Mockito.when(mockUser.isDetained()).thenReturn(false);
    Mockito.when(mockUser.isDeleted()).thenReturn(false);
    
    return new BannedUser(mockUser, mockBus);
  }
  
  @Test
  void shouldReturnBannedUserWhenUserIsActivated() {
    UserState result = sut.activate();

    assertTrue(BannedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, never()).post(any(UserActivatedEvent.class));
  }

  @Test
  void shouldReturnDetainedUserWhenUserIsDetained() {
    UserState result = sut.detain(10);

    assertTrue(BannedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, never()).post(any(UserDetainedEvent.class));
  }
  
  @Test
  void shouldReturnBannedUserWhenUserIsBanned() {
    UserState result = sut.ban();
    
    assertTrue(BannedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, never()).post(any(UserBannedEvent.class));
  }
  
  @Test
  void shouldReturnActiveUserWhenUserIsReleased() {
    UserState result = sut.release();

    assertTrue(ActiveUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, times(1)).post(any(UserReleasedEvent.class));
  }
  
  @Test
  void shouldReturnDeletedUserWhenUserIsDeleted() {
    UserState result = sut.delete();

    assertTrue(DeletedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, times(1)).post(any(UserDeletedEvent.class));
  }
  
  @Test
  void shouldReturnRemovedUserWhenUserIsRemovedWithoutDeleting() {
    UserState result = sut.remove(false);

    assertTrue(RemovedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, times(1)).post(any(UserRemovedEvent.class));
  }
  
  @Test
  void shouldReturnRemovedUserWhenUserIsRemovedWithDeleting() {
    UserState result = sut.remove(false);

    assertTrue(RemovedUser.class.isAssignableFrom(result.getClass()), "Class is of type "+ result.getClass().getCanonicalName());
    verify(mockBus, times(1)).post(any(UserRemovedEvent.class));
  }
}


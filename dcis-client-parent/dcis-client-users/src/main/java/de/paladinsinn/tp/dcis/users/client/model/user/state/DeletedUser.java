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

package de.paladinsinn.tp.dcis.users.client.model.user.state;


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserPetitionedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;


/**
 * @author klenkes74
 * @since 12.04.25
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString(of = {"user"})
public class DeletedUser implements UserState {
  @Getter
  final private User user;
  final private EventBus bus;
  
  @Override
  public UserState activate() {
    bus.post(UserActivatedEvent.builder().user(user).build());
    
    return ActiveUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState detain(final long days) {
    return this;
  }
  
  @Override
  public UserState release() {
    user.unban();
    user.release();
    
    return this;
  }
  
  @Override
  public UserState ban() {
    user.ban();
    
    return this;
  }
  
  @Override
  public UserState delete() {
    return this;
  }
  
  
  @Override
  public UserState remove(final boolean delete) {
    bus.post(UserRemovedEvent.builder().user(user).delete(delete).build());
    
    return RemovedUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState petition(final UUID petition) {
    bus.post(UserPetitionedEvent.builder().user(user).petition(petition).build());
    
    return this;
  }
}

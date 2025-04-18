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

import de.paladinsinn.tp.dcis.commons.events.FakeEventBus;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserToImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserRepository;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserToJpa;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserService {
    private final UserRepository userRepository;
    
    private final FakeEventBus fakeBus;
    private final UserToImpl toUser;
    private final UserToJpa toUserJPA;

    @Getter
    @Setter
    private Authentication authentication;

    
    @Counted
    @Timed
    public User createUser(final User player) {
        log.entry(player, authentication);

        UserJPA result = userRepository.save(toUserJPA.apply(player));
        
        return log.exit(result);
    }
    
    @Counted
    @Timed
    public void activateUser(final User user) {
        log.entry(user);
        
        Optional<User> data = loadUserByIdOrNamespaceAndName(user);
        data.ifPresent(u -> {
            u.getState(fakeBus).activate();
            userRepository.save(toUserJPA.apply(u));
        });
        
        log.exit();
    }
    
    @Counted
    @Timed
    private Optional<User> loadUserByIdOrNamespaceAndName(final User query) {
        log.entry(query);
        
        Optional<User> result;
        
        if (query.getId() != null) {
            result = retrieveUser(query.getId());
        } else {
            result = retrieveUser(query.getNameSpace(), query.getName());
        }
        
        return log.exit(result);
    }
    
    @Counted
    @Timed
    public Optional<User> retrieveUser(final UUID uid) {
        log.entry(uid, authentication);

        Optional<UserJPA> result = userRepository.findById(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result.isPresent() ? result.get() : "***none***");

        return Optional.of(log.exit(result.orElse(null)));
    }
    
    @Counted
    @Timed
    public Optional<User> retrieveUser(final String nameSpace, final String name) {
        log.entry(nameSpace, name, authentication);
        
        Optional<UserJPA> result = userRepository.findByNameSpaceAndName(nameSpace, name);
        
        log.debug("Loaded player from database. nameSpace={}, name={}, player={}", nameSpace, name, result.isPresent() ? result.get() : "***none***");
        
        return Optional.of(log.exit(result.orElse(null)));
    }
    
    @Counted
    @Timed
    public List<User> retrieveUsers() {
        log.entry();
        
        List<UserJPA> data = userRepository.findAll();
        List<User> result = data.stream().map(e -> (User) toUser.apply(e)).toList();
        
        return log.exit(result);
    }
    
    @Counted
    @Timed
    public List<User> retrieveUsers(final String namespace) {
        log.entry(namespace);
        
        List<UserJPA> data = userRepository.findByNameSpace(namespace);
        List<User> result = data.stream().map(e -> (User) toUser.apply(e)).toList();
        
        return log.exit(result);
    }
    
    @Counted
    @Timed
    public Page<User> retrieveUsers(final String nameSpace, final Pageable pageable) {
        log.entry(nameSpace, pageable, authentication);

        Page<UserJPA> data = userRepository.findByNameSpace(nameSpace, pageable);
        Page<User> result = new PageImpl<>(new LinkedList<>(data.stream().map(toUser).toList()), pageable, data.getTotalElements());

        log.debug("Loaded users for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());

        return log.exit(result);
    }
    
    @Counted
    @Timed
    public User updateUser(final UUID uid, final User user) {
        log.entry(uid, user, authentication);
        
        log.info("Updating user not implemented yet. uid={}, user={}, authentication={}", uid, user, authentication);
        
        Optional<User> data = retrieveUser(uid);
        
        return log.exit(data.orElse(toUserJPA.apply(user)));
    }
    
    @Counted
    @Timed
    public void deleteUser(final User user) {
        log.entry(user, authentication);
        
        log.error("Deleting user not implemented yet. user={}, authentication={}", user, authentication);
        
        log.exit();
    }
    
    @Counted
    @Timed
    public void removeUser(final User user) {
        log.entry(user, authentication);
        
        log.error("Removing user is not implemented yet. user={}, authentication={}", user, authentication);
        
        log.exit();
    }
    
    @Counted
    @Timed
    public void detainUser(final User user, final long ttl) {
        log.entry(user, ttl, authentication);
        
        Optional<User> data = loadUserByIdOrNamespaceAndName(user);
        data.ifPresent(u -> {
            u.getState(fakeBus).detain(ttl);
            userRepository.save(toUserJPA.apply(u));
        });
        
        log.exit(data);
    }
    
    @Counted
    @Timed
    public void banUser(final User user) {
        log.entry(user, authentication);
        
        Optional<User> data = loadUserByIdOrNamespaceAndName(user);
        data.ifPresent(u -> {
            u.getState(fakeBus).ban();
            userRepository.save(toUserJPA.apply(u));
        });
        
        log.exit(data);
    }
    
    @Counted
    @Timed
    public void releaseUser(final User user) {
        log.entry(user, authentication);
        
        Optional<User> data = loadUserByIdOrNamespaceAndName(user);
        data.ifPresent(u -> {
            u.getState(fakeBus).ban();
            userRepository.save(toUserJPA.apply(u));
        });
        
        log.exit(data);
    }
}

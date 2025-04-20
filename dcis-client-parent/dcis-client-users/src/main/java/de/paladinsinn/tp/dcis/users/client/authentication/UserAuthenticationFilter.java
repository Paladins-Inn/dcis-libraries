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

package de.paladinsinn.tp.dcis.users.client.authentication;


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.client.model.User;
import de.paladinsinn.tp.dcis.users.client.services.UserCantBeCreatedException;
import de.paladinsinn.tp.dcis.users.client.services.UserClient;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.Optional;

import static org.slf4j.ext.XLogger.Level.ERROR;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-01-06
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@XSlf4j
public class UserAuthenticationFilter implements ApplicationListener<AuthenticationSuccessEvent>, Closeable {

    private final EventBus loggingEventBus;
    private final UserClient userClient;
    
    @Value("${spring.application.system:unknown")
    private String system;
    
    @PostConstruct
    public void init() {
        log.entry();
        loggingEventBus.register(this);
        log.exit();
    }

    @PreDestroy
    @Override
    public void close() {
        log.entry();
        loggingEventBus.unregister(this);
        log.exit();
    }
    
    @Timed
    @Override
    public void onApplicationEvent(@Nonnull AuthenticationSuccessEvent event) {
        log.entry(event);

        try {
            Optional<User> user = userClient.login(event.getAuthentication());
            
            if (user.isEmpty()) {
                user = userClient.create(event.getAuthentication());
            }
            
            createEvent(user).ifPresentOrElse(
                loggingEventBus::post,
                () -> log.warn("User can't be logged in. event={}", event)
            );

        } catch (UserIsBannedException | UserIsDeletedException | UserIsDetainedException | UserCantBeCreatedException e) {
            throw log.throwing(ERROR, new IllegalStateException(e));
        }
        
        log.exit();
    }

    
    private Optional<UserLoginEvent> createEvent(final Optional<User> user)
        throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException, UserCantBeCreatedException {
        log.entry(user.orElse(null));

        if (user.isEmpty()) {
            return Optional.empty();
        }
        
        return log.exit(Optional.of(
            UserLoginEvent.builder()
                .user(user.get())
                .system(system)
            .build()
        ));
    }
}

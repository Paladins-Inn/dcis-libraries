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

package de.paladinsinn.tp.dcis.commons.ui;


import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-01-06
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@XSlf4j
public class UserLoginReportingFilter implements ApplicationListener<AuthenticationSuccessEvent>, Closeable {

    private final EventBus loggingEventBus;
    
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
        
        loggingEventBus.post(createEvent(event));
        
        log.exit();
    }
    
    private UserLoginEvent createEvent(final AuthenticationSuccessEvent event) {
        log.entry(event);
        
        DefaultOidcUser user = (DefaultOidcUser) event.getAuthentication().getPrincipal();
        
        return log.exit(
            UserLoginEvent.builder()
                .user(readUserFromOAuth2Token(user))
            .build()
        );
    }
    
    private User readUserFromOAuth2Token(final DefaultOidcUser user) {
        log.entry(user);
        
        return log.exit(
            UserImpl.builder()
                .id(UUID.fromString(user.getSubject()))
                .name(user.getName())
                .nameSpace(user.getIssuer().toString())
                .build()
        );
    }
}

package de.paladinsinn.tp.dcis.users.client.services;


import de.paladinsinn.tp.dcis.lib.messaging.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.client.model.User;
import de.paladinsinn.tp.dcis.users.client.model.UserImpl;
import de.paladinsinn.tp.dcis.users.client.rest.UserRestClient;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsBannedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDeletedException;
import de.paladinsinn.tp.dcis.users.client.authentication.UserIsDetainedException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.slf4j.ext.XLogger.Level.DEBUG;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserClient {
  // ignored because it will be implemented by either the dcis-store-users or the SCS itself.
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private final UserStoreReader reader;
  // ignored because it will be implemented by either the dcis-store-users or the SCS itself.
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private final UserStoreWriter writer;
  
  private final UserRestClient rest;

  private final LoggingEventBus bus;
  
  @Value("${spring.application.system:DCIS")
  private String system;
  
  @PostConstruct
  public void init() {
    log.entry(bus, reader, writer);
    
    bus.register(this);
    
    log.exit(bus);
  }
  
  @PreDestroy
  public void destroy() {
    log.entry(bus, reader, writer);
    
    bus.unregister(this);
    
    log.exit(bus);
  }

  
  @Timed
  @Counted
  public Optional<User> login(@NotNull final Authentication authentication) throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException {
    log.entry(authentication);
    
    DefaultOidcUser oidc = (DefaultOidcUser) authentication.getPrincipal();
    
    Optional<User> result = reader.findByLogin(oidc.getIssuer().toString(), oidc.getSubject());
    
    if (result.isPresent()) {
      result.get().checkInactive();
    }
    
    return log.exit(result);
  }
  
  @Timed
  @Counted
  public Optional<User> create(@NotNull final Authentication authentication) throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException, UserCantBeCreatedException {
    log.entry(authentication);
    
    DefaultOidcUser oidc = (DefaultOidcUser) authentication.getPrincipal();
    
    log.info("Trying to create user. issuer={}, subject={}, preferredUsername={}, system={}",
        oidc.getIssuer(), oidc.getSubject(), oidc.getPreferredUsername(), system);
    
    User result = UserImpl.builder()
        .id(UUID.randomUUID())
        .issuer(oidc.getIssuer().toString())
        .subject(oidc.getSubject())
        .nameSpace(system)
        .name(oidc.getPreferredUsername())
        .build();

    try {
      result = rest.createUser(result);
    } catch (IllegalArgumentException e) {
      throw log.throwing(DEBUG, new UserCantBeCreatedException(oidc.getIssuer().toString(), oidc.getSubject(), oidc.getPreferredUsername()));
    }
    
    result.checkInactive();
    
    log.info("Created user. user={}", result);
    
    return log.exit(Optional.of(rest.createUser(result)));
  }
}

package de.paladinsinn.tp.dcis.users.client.model;


import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserImpl;
import de.paladinsinn.tp.dcis.users.client.services.UserStoreReader;
import de.paladinsinn.tp.dcis.users.client.services.UserStoreWriter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * A small in-memory version of a user store.
 * @author klenkes74
 * @since 20.04.25
 */
@Service
@Scope("singleton")
public class InMemoryUserStore implements UserStoreReader, UserStoreWriter {
  private final HashMap<UUID, UserImpl> users = new HashMap<>();
  private final HashMap<String, UserImpl> byUsername = new HashMap<>();
  private final HashMap<String, UserImpl> byLogin = new HashMap<>();
  
  @Override
  public Optional<User> findById(final UUID id) {
    return copy(users.get(id));
  }
  
  @Override
  public Optional<User> findByUsername(final String namespace, final String name) {
    return copy(byUsername.get(namespace + ":" + name));
  }
  
  @Override
  public Optional<User> findByLogin(final String issuer, final String subject) {
    return copy(byLogin.get(issuer));
  }
  
  @Override
  public User write(final User user) {
    UserImpl stored = copy(user);
    
    users.put(user.getId(), stored);
    byUsername.put(user.getNameSpace() + "-" + user.getId(), stored);
    byLogin.put(user.getIssuer() + "-" + user.getSubject(), stored);
    
    return user;
  }
  
  @Override
  public void delete(final UUID id) {
    User user = users.get(id);
    
    if (user != null) {
      users.remove(user.getId());
      
      byUsername.remove(user.getNameSpace() + "-" + user.getId());
      byLogin.remove(user.getIssuer() + "-" + user.getSubject());
    }
  }

  
  private Optional<User> copy(UserImpl user) {
    if (user != null) {
      return Optional.of(user.toBuilder().build());
    } else {
      return Optional.empty();
    }
  }
  
  private UserImpl copy(final User user) {
    if (user instanceof UserImpl) {
      //noinspection OptionalGetWithoutIsPresent
      return (UserImpl) copy((UserImpl) user).get();
    }
    
    return UserImpl.builder()
        .id(user.getId() != null ? user.getId() : UUID.randomUUID())
        .issuer(user.getIssuer())
        .subject(user.getSubject())
        .nameSpace(user.getNameSpace())
        .name(user.getName())
        .created(user.getCreated())
        .modified(user.getModified())
        .deleted(user.getDeleted())
        .banned(user.isBanned())
        .detainedTill(user.getDetainedTill())
        .detainmentDuration(user.getDetainmentDuration())
        .build();
  }
}

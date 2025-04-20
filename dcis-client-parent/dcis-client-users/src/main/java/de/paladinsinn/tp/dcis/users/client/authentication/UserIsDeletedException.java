package de.paladinsinn.tp.dcis.users.client.authentication;

import de.paladinsinn.tp.dcis.users.client.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public class UserIsDeletedException extends UserIsInactiveException {
  public UserIsDeletedException(@NotNull User user) {
    super(user, "User is deleted since " + user.getDeleted());
  }
  
  public OffsetDateTime getDeletedAt() {
    return user.getDeleted();
  }
}

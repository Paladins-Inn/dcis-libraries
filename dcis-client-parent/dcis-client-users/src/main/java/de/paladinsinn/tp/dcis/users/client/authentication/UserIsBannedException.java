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
public class UserIsBannedException extends UserIsInactiveException {
  public UserIsBannedException(@NotNull User user) {
    super(user, "User is banned since " + user.getDeleted());
  }
  
  public OffsetDateTime getBannedAt() {
    return user.getDeleted();
  }
}

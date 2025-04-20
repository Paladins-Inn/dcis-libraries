package de.paladinsinn.tp.dcis.users.client.authentication;


import de.paladinsinn.tp.dcis.users.client.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.OffsetDateTime;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public class UserIsDetainedException extends UserIsInactiveException {
  public UserIsDetainedException(@NotNull User user) {
    super(user, "User is detained till " + user.getDetainedTill());
  }
  
  public OffsetDateTime getDetainedTill() {
    return user.getDetainedTill();
  }
  
  public Duration getDetainedFor() {
    return user.getDetainmentDuration();
  }
  
  public Long getDetainedDays() {
    return user.getDetainmentDuration().toDays();
  }
}

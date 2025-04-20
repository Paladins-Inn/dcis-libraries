package de.paladinsinn.tp.dcis.users.client.authentication;

import de.paladinsinn.tp.dcis.users.client.model.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
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
  
  
  public UserIsDeletedException(@Nullable final User user, @NotBlank final String message) {
    super(user, message);
  }
  
  public UserIsDeletedException(@Nullable final User user, @NotBlank final String message, @NotNull final Throwable cause) {
    super(user, message, cause);
  }
  
  public UserIsDeletedException(@Nullable final User user, @NotNull final Throwable cause) {
    super(user, cause);
  }
  
  public UserIsDeletedException(@Nullable final User user, @NotBlank final String message, @NotNull final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(user, message, cause, enableSuppression, writableStackTrace);
  }
  
  public OffsetDateTime getDeletedAt() {
    return user.getDeleted();
  }
}

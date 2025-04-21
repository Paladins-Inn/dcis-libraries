package de.paladinsinn.tp.dcis.users.client.authentication;


import de.paladinsinn.tp.dcis.users.client.BaseUserException;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public abstract class UserIsInactiveException extends BaseUserException {
  public UserIsInactiveException(@Nullable final User user, @NotBlank final String message) {
    super(user, message);
  }
}

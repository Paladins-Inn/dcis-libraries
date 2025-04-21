package de.paladinsinn.tp.dcis.users.client;


import de.kaiserpfalzedv.commons.api.BaseException;
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
public abstract class BaseUserException extends BaseException {
  protected final User user;
  
  public BaseUserException(@Nullable final User user, @NotBlank final String message) {
    super(message);
    this.user = user;
  }
}

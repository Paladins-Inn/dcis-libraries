package de.paladinsinn.tp.dcis.users.client;


import de.kaiserpfalzedv.commons.api.BaseException;
import de.paladinsinn.tp.dcis.users.client.model.User;
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
  
  public BaseUserException(@Nullable final User user, @NotBlank final String message, final Throwable cause) {
    super(message, cause);
    this.user = user;
  }
  
  public BaseUserException(@Nullable final User user, @NotBlank final Throwable cause) {
    super(cause);
    this.user = user;
  }
  
  public BaseUserException(@Nullable final User user, @NotBlank final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.user = user;
  }
}

package de.paladinsinn.tp.dcis.users.client.services;


import de.paladinsinn.tp.dcis.users.client.BaseUserException;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public class UserCantBeCreatedException extends BaseUserException {
  private final String issuer;
  private final String subject;
  private final String preferredUsername;
  
  public UserCantBeCreatedException(@NotBlank String issuer, @NotBlank String subject, @NotBlank String preferredUsername) {
    super(null, "User can't be created. issuer=" + issuer + ", subject=" + subject + ", preferredUsername=" + preferredUsername);
    
    this.issuer = issuer;
    this.subject = subject;
    this.preferredUsername = preferredUsername;
  }
}

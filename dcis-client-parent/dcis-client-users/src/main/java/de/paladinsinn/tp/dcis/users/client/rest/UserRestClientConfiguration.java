package de.paladinsinn.tp.dcis.users.client.rest;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author klenkes74
 * @since 20.04.25
 */
@Component
@ConfigurationProperties("dcis.users.rest")
@Builder(setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(includeFieldNames = false)
public class UserRestClientConfiguration {
  /**
   * The URL of the users service.
   */
  @NotBlank
  private String url;
  
  /**
   * The API key for the users service.
   */
  @NotBlank
  private String apiKey;
}

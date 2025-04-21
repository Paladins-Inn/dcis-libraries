package de.paladinsinn.tp.dcis.users.client.rest;


import de.paladinsinn.tp.dcis.users.client.model.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;


/**
 * @author klenkes74
 * @since 20.04.25
 */
@HttpExchange(
    url = "/users",
    contentType = MediaType.APPLICATION_JSON_VALUE,
    accept = MediaType.APPLICATION_JSON_VALUE
)
public interface UserRestClient {
  @PutExchange
  User createUser(@NotNull @Valid User user) throws IllegalStateException;
}

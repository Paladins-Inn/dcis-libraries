package de.paladinsinn.tp.dcis.users.client.rest;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

/**
 * @author klenkes74
 * @since 20.04.25
 */
@Configuration
@XSlf4j
public class UserRestClientProvider {
  @Bean
  public UserRestClient userRestClient(@NotNull @Valid final UserRestClientConfiguration properties) {
    log.entry(properties);
    
    WebClient webClient = WebClient.builder()
        .baseUrl(properties.getUrl())
        .defaultStatusHandler(
            httpStatusCode -> HttpStatus.NOT_FOUND == httpStatusCode,
            response -> Mono.empty())
        .defaultStatusHandler(
            HttpStatusCode::is5xxServerError,
            response -> Mono.error(new IllegalStateException()))
        .build();
    WebClientAdapter adapter = WebClientAdapter.create(webClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
    
    return log.exit(factory.createClient(UserRestClient.class));
  }
}

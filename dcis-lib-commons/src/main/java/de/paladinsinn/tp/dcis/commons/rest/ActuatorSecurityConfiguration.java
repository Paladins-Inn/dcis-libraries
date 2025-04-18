/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package de.paladinsinn.tp.dcis.commons.rest;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@EnableScheduling
@Order(1)
@RequiredArgsConstructor
@XSlf4j
public class ActuatorSecurityConfiguration {
    @Bean
    public SecurityFilterChain observabilitySecurity(HttpSecurity http) throws Exception {
				log.entry(http);

        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/actuator/**");
        AntPathRequestMatcher healthCheck = new AntPathRequestMatcher("/actuator/health/**");

        http
            .securityMatcher(matcher)
            .authorizeHttpRequests(r -> r
                .requestMatchers(healthCheck).anonymous()
                .requestMatchers(matcher).authenticated()
                .anyRequest().hasRole("OBSERVER")
                
            )
            .httpBasic(h -> h.realmName("Observability"))
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable /* BAD - CSRF protection shouldn't be disabled */)
            .sessionManagement(AbstractHttpConfigurer::disable)
            ;

        return log.exit(http.build());
    }

    @Bean
	public AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
			log.entry(userDetailsService, passwordEncoder);

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);

		return log.exit(providerManager);
	}

	@Value("${spring.security.user.name}")
	private String username;
	@Value("${spring.security.user.password}")
	private String password;
	@Value("${spring.security.user.roles}")
	private String roles;

	@Bean
	public UserDetailsService userDetailsService() {
		log.entry(username, roles);

		@SuppressWarnings("deprecation")
        UserDetails userDetails = User
            .withDefaultPasswordEncoder()
            .username(username)
			.password(password)
			.roles(roles.split(","))
			.build();

		return log.exit(new InMemoryUserDetailsManager(userDetails));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return log.exit(PasswordEncoderFactories.createDelegatingPasswordEncoder());
	}
}

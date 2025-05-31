/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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
package de.paladinsinn.tp.dcis.lib.rest;

import de.kaiserpfalzedv.commons.users.client.service.KpUserDetailsService;
import de.kaiserpfalzedv.commons.users.client.service.UserAuthenticationManager;
import de.kaiserpfalzedv.commons.users.client.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@EnableScheduling
@Order(1)
@RequiredArgsConstructor
@XSlf4j
@Import({
		UserAuthenticationService.class,
		UserAuthenticationManager.class,
		KpUserDetailsService.class,
})
public class ActuatorSecurityConfiguration {
    @Bean
    public SecurityFilterChain observabilitySecurity(HttpSecurity http) throws Exception {
				log.entry(http);
			
				PathPatternRequestMatcher matcher = PathPatternRequestMatcher.withDefaults().matcher("/actuator/**");
				PathPatternRequestMatcher healthCheck = PathPatternRequestMatcher.withDefaults().matcher("/actuator/health/**");

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
}

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

import de.kaiserpfalzedv.commons.spring.security.EnableKeycloakSecurityIntegration;
import de.kaiserpfalzedv.commons.spring.security.KeycloakGroupAuthorityMapper;
import de.kaiserpfalzedv.commons.spring.security.KeycloakLogoutHandler;
import de.kaiserpfalzedv.commons.users.client.reactive.ReactUserSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@EnableKeycloakSecurityIntegration
@Order(2)
@Import({
    ReactUserSecurityConfig.class,
})
@RequiredArgsConstructor
@XSlf4j
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.client.provider.sso.issuer-uri}")
    private String issuerUri;

    @Value("${server.servlet.path:/}")
    private String servletPath;

    @Bean
	  public SecurityFilterChain userSecurityFilterChain(
        HttpSecurity http, 
        KeycloakGroupAuthorityMapper authoritiesMapper,
        KeycloakLogoutHandler keycloakLogoutHandler
    ) throws Exception {
        log.entry(http, authoritiesMapper, keycloakLogoutHandler, servletPath);

        CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
        // set the name of the attribute the CsrfToken will be populated on
        csrfRequestHandler.setCsrfRequestAttributeName(null);
        
        PathPatternRequestMatcher publicMatcher = PathPatternRequestMatcher.withDefaults().matcher(servletPath + "/public/**");
        PathPatternRequestMatcher loginMatcher = PathPatternRequestMatcher.withDefaults().matcher(servletPath + "/login");
        PathPatternRequestMatcher oauth2Matcher = PathPatternRequestMatcher.withDefaults().matcher(servletPath + "/oauth2/**");
        PathPatternRequestMatcher webjarsMatcher = PathPatternRequestMatcher.withDefaults().matcher(servletPath + "/webjars/**");
        

		    http
            .authorizeHttpRequests(a -> a
                .requestMatchers(publicMatcher).anonymous()
                .requestMatchers(loginMatcher).anonymous()
                .requestMatchers(oauth2Matcher).anonymous()
                .requestMatchers(webjarsMatcher).anonymous()
                .anyRequest().authenticated()
            )
            .oauth2Login(l -> l
                .authorizationEndpoint(Customizer.withDefaults())
                .tokenEndpoint(Customizer.withDefaults())
                .userInfoEndpoint(u -> u.userAuthoritiesMapper(authoritiesMapper))
            )
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl(issuerUri + "/protocol/openid-connect/logout")
            )

            .cors(Customizer.withDefaults())

            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfRequestHandler)
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .rememberMe(Customizer.withDefaults())
            ;

        return log.exit(http.build());
	}
}

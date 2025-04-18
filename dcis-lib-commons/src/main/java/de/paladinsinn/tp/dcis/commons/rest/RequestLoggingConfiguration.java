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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-20
 */
@Configuration
@XSlf4j
public class RequestLoggingConfiguration {
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setBeforeMessagePrefix("Start: ");
        filter.setAfterMessagePrefix("End: ");

        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);

        filter.setIncludePayload(false);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);

        return log.exit(filter);
    }
}

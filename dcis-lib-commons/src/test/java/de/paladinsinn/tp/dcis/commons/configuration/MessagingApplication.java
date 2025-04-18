/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.paladinsinn.tp.dcis.commons.configuration;

import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@EnableEventBus
public class MessagingApplication extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    public static void main(String [] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }

}

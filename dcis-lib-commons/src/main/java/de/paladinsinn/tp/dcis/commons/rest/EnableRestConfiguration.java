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

import de.paladinsinn.tp.dcis.commons.ui.UserLoginReportingFilter;
import de.paladinsinn.tp.dcis.commons.ui.WebUiModelDefaultValueSetter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-09-27
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ 
        ActuatorSecurityConfiguration.class,
        WebSecurityConfiguration.class,
        RequestLoggingConfiguration.class,
        RestTemplateProvider.class,
        WebI18nConfiguration.class,
        FilterRegistrations.class,
        UserLoginReportingFilter.class,
        WebUiModelDefaultValueSetter.class
 })
public @interface EnableRestConfiguration {}

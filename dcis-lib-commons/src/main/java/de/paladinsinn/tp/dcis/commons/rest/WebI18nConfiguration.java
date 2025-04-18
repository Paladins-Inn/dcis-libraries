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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import de.kaiserpfalzedv.commons.spring.i18n.KaiserpfalzMessageSource;

@Configuration
@XSlf4j
public class WebI18nConfiguration implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        return log.exit(new KaiserpfalzMessageSource());
    }

    @Bean
    public LocaleResolver localeResolver() {
        return log.exit(new CookieLocaleResolver());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor result = new LocaleChangeInterceptor();
        result.setParamName("lang");
        return log.exit(result);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.entry(registry);

        registry.addInterceptor(localeChangeInterceptor());
    }
    
}

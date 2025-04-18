/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.commons.formatter;


import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.ext.XLogger;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


/**
 * @author klenkes74
 * @since 06.02.25
 */
@XSlf4j
public class LocalDateFormatter implements Formatter<LocalDate> {
    
    @Override
    @Nonnull
    public LocalDate parse(@Nonnull final String text, @Nonnull final Locale locale) throws ParseException {
        log.entry(text, locale);
        
        try {
            return log.exit(LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException e) {
            throw log.throwing(XLogger.Level.WARN, new ParseException(e.getMessage(), e.getErrorIndex()));
        }
    }
    
    @Override
    @Nonnull
    public String print(@Nonnull final LocalDate object, @Nonnull final Locale locale) {
        log.entry(object, locale);
        
        return log.exit(object.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}

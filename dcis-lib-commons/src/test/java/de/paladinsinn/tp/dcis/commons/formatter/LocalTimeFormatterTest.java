/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.commons.formatter;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author klenkes74
 * @since 08.02.25
 */
@Slf4j
public class LocalTimeFormatterTest {
    private final LocalTimeFormatter underTest = new LocalTimeFormatter();
    private final Locale locale = Locale.getDefault();
    
    @Test
    public void shouldConvertWhenValidLocalTimeIsGiven() throws ParseException {
        LocalTime time = LocalTime.now();
        
        assertEquals(time, underTest.parse(underTest.print(time, locale), locale));
    }
    
    @Test
    public void shouldDeliverStringWhenValidLocalTimeIsGiven() {
        LocalTime time = LocalTime.of(20, 1, 59);
        String result = underTest.print(time, locale);
        
        log.info("transformed LocalTime. input={}, result={}", time, result);
        assertEquals("20:01:59", result);
    }
    
    
    @Test
    public void shouldThrowAnExceptionIfTheStringIsInvalid() {
        assertThrows(ParseException.class, () -> underTest.parse("25:00", locale));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "00:00",
        "23:59",
        "17:40"
    })
    public void shouldAcceptISOTimeFormat(final String input) throws ParseException {
        log.info("Transformed string to OffSetDateTime, input={}, result={}", input, underTest.parse(input, locale));
    }
}

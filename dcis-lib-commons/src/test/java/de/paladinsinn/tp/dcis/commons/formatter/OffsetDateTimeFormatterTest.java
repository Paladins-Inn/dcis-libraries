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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author klenkes74
 * @since 08.02.25
 */
@Slf4j
public class OffsetDateTimeFormatterTest {
    private final OffsetDateTimeFormatter underTest = new OffsetDateTimeFormatter();
    private final Locale locale = Locale.getDefault();
    
    @Test
    public void shouldConvertWhenValidOffsetDateTimeIsGiven() throws ParseException {
        OffsetDateTime time = OffsetDateTime.now();
        
        assertEquals(time, underTest.parse(underTest.print(time, locale), locale));
    }
    
    @Test
    public void shouldDeliverStringWhenValidOffsetDateTimeIsGiven() {
        OffsetDateTime time = OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 123000000, ZoneOffset.UTC);
        String result = underTest.print(time, locale);
        
        log.info("transformed OffsetDateTime. input={}, result={}", time, result);
        assertEquals("2025-01-01T00:00:00.123Z", result);
    }
    
    
    @Test
    public void shouldThrowAnExceptionIfTheStringIsInvalid() {
        assertThrows(ParseException.class, () -> underTest.parse("01.01.2025 13:45", locale));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2025-01-01T23:25:12.142+00:00",
        "2025-01-01T23:25:12.142Z",
        "2025-01-01T23:25:12.142142142Z",
        "2025-01-01T23:25:12.142142142+01:00",
    })
    public void shouldAcceptISOTimeFormat(final String input) throws ParseException {
        log.info("Transformed string to OffSetDateTime, input={}, result={}", input, underTest.parse(input, locale));
    }
}

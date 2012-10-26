/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.commons.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Test for <code>ElapsedTimeTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/30/2006
 */
public class ElapsedTimeTest {

    private static final int TIME_IN_SECONDS = 12450;

    private ElapsedTime elapsedTime;

    @Before
    public void setUp() throws Exception {
        elapsedTime = new ElapsedTime(TIME_IN_SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        elapsedTime = null;
    }

    @Test
    public void getHours() throws Exception {
        assertEquals(3, elapsedTime.getHours());
    }

    @Test
    public void getMinutes() throws Exception {
        assertEquals(27, elapsedTime.getMinutes());
    }

    @Test
    public void getSeconds() throws Exception {
        assertEquals(30,elapsedTime.getSeconds());
    }

    @Test
    public void getFinalTime() throws Exception {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.SECOND, TIME_IN_SECONDS);

        assertEquals(cal.getTime().toString(), elapsedTime.getFinalTime().toString());
    }
}

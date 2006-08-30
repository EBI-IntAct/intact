/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Test for <code>ElapsedTimeTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/30/2006
 */
public class ElapsedTimeTest extends TestCase {
    public ElapsedTimeTest(String name) {
        super(name);
    }

    private static final int TIME_IN_SECONDS = 12450;

    private ElapsedTime elapsedTime;

    public void setUp() throws Exception {
        super.setUp();
        elapsedTime = new ElapsedTime(TIME_IN_SECONDS);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        elapsedTime = null;
    }

    public void testGetHours() throws Exception {
        assertEquals(3, elapsedTime.getHours());
    }

    public void testGetMinutes() throws Exception {
        assertEquals(27, elapsedTime.getMinutes());
    }

    public void testGetSeconds() throws Exception {
        assertEquals(30,elapsedTime.getSeconds());
    }

    public void testGetFinalTime() throws Exception {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.SECOND, TIME_IN_SECONDS);

        assertEquals(cal.getTime(), elapsedTime.getFinalTime());
    }

    public static Test suite() {
        return new TestSuite(ElapsedTimeTest.class);
    }
}

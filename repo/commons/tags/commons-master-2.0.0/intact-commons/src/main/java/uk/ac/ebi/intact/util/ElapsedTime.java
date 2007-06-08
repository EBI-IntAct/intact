/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Converts seconds in hours, minutes and seconds
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Aug-2006</pre>
 */
public class ElapsedTime
{

    private int timeInSeconds;
    private int hours;
    private int minutes;
    private int seconds;

    public ElapsedTime(int timeInSeconds)
    {
        this.timeInSeconds = timeInSeconds;

        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
    }

    public int getHours()
    {
        return hours;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    @Override
    public String toString()
    {
       return hours + " hour(s) " + minutes + " minute(s) " + seconds + " second(s)";
    }

    public Date getFinalTime()
    {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.SECOND, timeInSeconds);

        return cal.getTime();
    }
}

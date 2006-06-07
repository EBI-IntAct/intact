/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class DateToolbox {

    public static String getMonth(int monthNumber){
        String monthName = new String();
        switch (monthNumber) {
            case 1:  monthName = "JAN"; break;
            case 2:  monthName = "FEB"; break;
            case 3:  monthName = "MAR"; break;
            case 4:  monthName = "APR"; break;
            case 5:  monthName = "MAY"; break;
            case 6:  monthName = "JUN"; break;
            case 7:  monthName = "JUL"; break;
            case 8:  monthName = "AUG"; break;
            case 9:  monthName = "SEP"; break;
            case 10: monthName = "OCT"; break;
            case 11: monthName = "NOV"; break;
            case 12: monthName = "DEC"; break;
            default: monthName = "Not a month!";break;
        }

        return monthName;
    }

    public static String formatDate(Date date){
        if(date == null){
            return null;
        }
        String newDate = date.toString().substring(0,10);
        int monthNumber =  Integer.parseInt(newDate.substring(5, 7) );
        String monthName = getMonth(monthNumber);
        String year = newDate.substring(0,4);
        String day = newDate.substring(8,10);
        newDate = year + "-" + monthName + "-" + day;
        newDate = newDate.trim();
        return newDate;
    }

}

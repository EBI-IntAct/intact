/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.util;

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
        String newDate = date.toString();
//        int monthNumber =  Integer.parseInt(newDate.substring(5, 7) );
        String[] dateData = newDate.split(" ");
        String monthName = dateData[1].toUpperCase();
        System.out.println("monthName = " + monthName);
        String year = dateData[5];//newDate.substring(newDate.length(),newDate.length()-4);
        System.out.println("year = " + year);
        String day = dateData[2];//newDate.substring(8,10);
        System.out.println("day = " + day);
        newDate = year + "-" + monthName + "-" + day;
        newDate = newDate.trim();
        return newDate;
    }

    public static String formatDateResultWrapper(Date date){
        String dateString = date.toString();
        String dateData[] = dateString.split("-");
        String month = getMonth(Integer.parseInt(dateData[1]));
        String year =  dateData[1];
        String day = dateData[3];
        return year + "-" + month + "-" + day;

    }


}

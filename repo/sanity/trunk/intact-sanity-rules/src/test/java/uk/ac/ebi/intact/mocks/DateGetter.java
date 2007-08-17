/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import java.util.Date;
import java.util.Calendar;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class DateGetter {
    public static Date createdDate;
    public static Date updatedDate;
    public static final Calendar calendar = Calendar.getInstance();

    static{
        calendar.set(2006, Calendar.MARCH, 11);
        createdDate = calendar.getTime();

        calendar.set(2007, Calendar.JANUARY, 22);
        updatedDate = calendar.getTime();
    }

    /**
     * Return a Data object set to 2006, 11th of March
     * @return date
     */
    public static Date getCreatedDate() {
        return createdDate;
    }

     /**
     * Return a Data object set to 2007, 22nd of January
     * @return date
     */
    public static Date getUpdatedDate() {
        return updatedDate;
    }
}
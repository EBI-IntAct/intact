/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.alarm;

import java.io.PrintStream;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version here
 */
public class PrintStreamAlarmProcessor implements AlarmProcessor {

    private PrintStream out;

    public PrintStreamAlarmProcessor( PrintStream out ) {
        if ( out == null ) {
            throw new NullPointerException( "PrintStream must not be null." );
        }
        this.out = out;
    }

    public void processAlarm( String message ) {
        out.println( message );
    }
}
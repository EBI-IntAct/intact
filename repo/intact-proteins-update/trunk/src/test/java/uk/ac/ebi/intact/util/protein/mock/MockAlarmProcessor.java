/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.mock;

import uk.ac.ebi.intact.util.protein.alarm.AlarmProcessor;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version here
 */
public class MockAlarmProcessor implements AlarmProcessor  {

    Collection<String> messages = new ArrayList<String>( );

    public void processAlarm( String message ) {
        System.out.println( message );
        messages.add( message );
    }

    public Collection<String> getMessages() {
        return messages;
    }

    public void clearMessages() {
        messages.clear();
    }
}
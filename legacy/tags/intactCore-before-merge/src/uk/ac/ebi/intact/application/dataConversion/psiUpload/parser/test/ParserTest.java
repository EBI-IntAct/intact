/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.TestCase;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ParserTest extends TestCase {

    /**
     * Clear messages produced during the parsing process.
     */
    protected void clearParserMessages() {
        MessageHolder.getInstance().clearParserMessage();
    }


    /**
     * Display messages produced during the parsing process.
     */
    protected void displayExistingMessages() {

        Collection messages = MessageHolder.getInstance().getParserMessages();
        if ( !messages.isEmpty() ) {
            for ( Iterator iterator = messages.iterator(); iterator.hasNext(); ) {
                Message message = (Message) iterator.next();
                System.out.println( message );
            }
        }
    }
}

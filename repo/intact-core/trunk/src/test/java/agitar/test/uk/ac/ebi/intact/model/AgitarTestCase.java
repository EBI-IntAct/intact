/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package agitar.test.uk.ac.ebi.intact.model;

import junit.framework.TestCase;

/**
 * Basic implementation of the Agitar test utilities.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class AgitarTestCase extends TestCase {

    public void assertThrownBy( Class aClass, Exception ex ) {
        assertTrue( ex.getStackTrace()[0].getClassName().equals( aClass.getName() ) );
    }

    public Object getPrivateField( Object object, String fieldName ) {

        // use reflection to get this data

        throw new UnsupportedOperationException();
    }
}
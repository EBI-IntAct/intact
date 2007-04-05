/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package agitar.test.uk.ac.ebi.intact.model;

import junit.framework.TestCase;

import java.lang.reflect.Field;

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

    /**
     * Gives access to protected/private fields (by name) in the given object.
     * </p>
     * More info on how to do this with reflection here:
     * http://www.onjava.com/pub/a/onjava/2003/11/12/reflection.html?page=2
     *
     * @param object    the instance of the object on which we want to introspect fields.
     * @param fieldName the name of the field.
     *
     * @return the value contained by the field.
     */
    public Object getPrivateField( Object object, String fieldName ) {

        assertNotNull( object );
        assertNotNull( fieldName );

        // use reflection to get this data
        final Field fields[] = object.getClass().getDeclaredFields();
        for ( int i = 0; i < fields.length; ++i ) {
            if ( fieldName.equals( fields[i].getName() ) ) {
                fields[i].setAccessible( true );
                Object result = null;
                try {
                    result = fields[i].get( object );
                } catch ( IllegalAccessException e ) {
                    fail( "Failed to access private field " + object.getClass().getSimpleName() + "." + fieldName );
                }
                return result;
            }
        }

        fail( "Could not find field '" + fields + "' in Class: " + object.getClass() );
        return null;
    }
}
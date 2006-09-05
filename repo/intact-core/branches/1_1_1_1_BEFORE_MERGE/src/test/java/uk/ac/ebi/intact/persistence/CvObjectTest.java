/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.*;
import junit.framework.TestCase;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectTest extends TestCase {

       public static final String NEW_LINE = System.getProperty( "line.separator" );

       private IntactTransaction tx;

       protected void setUp() throws Exception
       {
           super.setUp();

           tx = DaoFactory.beginTransaction();
       }

       protected void tearDown() throws Exception
       {
           super.tearDown();

           tx.commit();
           tx = null;
       }


       public void testIsAssignableFrom(){
           assert(CvObject.class.isAssignableFrom(CvDatabase.class));
       }

}

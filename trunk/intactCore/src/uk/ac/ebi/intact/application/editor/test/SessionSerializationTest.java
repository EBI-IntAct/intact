/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.test;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditUser;

import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.apache.commons.lang.SerializationUtils;

/**
 * Tests the user instance which is stored in a session.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */ 
public class SessionSerializationTest extends TestCase  {

    /**
     * The handler to the Intact helper
     */
    private IntactHelper myHelper;

    public SessionSerializationTest(String name) {
        super(name);
    }

    public void setUp() throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(
                "uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "temp-web/WEB-INF/classes/config/repository.xml");
        ds.setConfig(config);
        myHelper = new IntactHelper(ds);
    }

    public void tearDown() {
        try {
            myHelper.closeStore();
        }
        catch (IntactException ie) {}
    }

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(SessionSerializationTest.class);
    }

    public void testExperiment() {
        try {
             Experiment exp = (Experiment) myHelper.getObjectByLabel(
                     Experiment.class, "gavin");
            // Create the user we want to seralize.
            EditUserI user = new EditUser(myHelper);
            byte[] bytes = SerializationUtils.serialize(user);
            System.out.println("Size: " + bytes.length);
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }

//    public static void main(String[] args) {
//        SessionSerializationTest test = null;
//        try {
//            test = new SessionSerializationTest();
//            Experiment exp = test.getExperiment();
//            System.out.println("Got gavin as an experiment");
//        }
//        catch (DataSourceException dse) {
//            dse.printStackTrace();
//        }
//        catch (IntactException ie) {
//            ie.printStackTrace();
//        }
//        finally {
//            if (test != null) {
//                test.closeStore();
//            }
//        }
//    }
}

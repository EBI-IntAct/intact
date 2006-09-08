/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;

import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: ProteinTest.java 5580 2006-08-02 08:58:30Z baranda $
 * @since <pre>13-Jul-2006</pre>
 */
public class HibernateTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ProteinTest.class);

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    private IntactTransaction tx;

    protected void setUp() throws Exception
    {
        super.setUp();

//        tx = DaoFactory.beginTransaction();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

//        tx.commit();
        tx = null;
    }

    public void testUnexpectedUpdateOnXref()
    {
        tx = DaoFactory.beginTransaction();
        ExperimentDao experimentDao = DaoFactory.getExperimentDao();
        Experiment exp = experimentDao.getByAc("EBI-476945");
        exp = experimentDao.getByAc("EBI-476945");
        System.out.println("exp.getShortLabel() = " + exp.getShortLabel());
        tx.commit();
        System.out.println("exp.getShortLabel() = " + exp.getShortLabel());
//        Collection<Interaction> interactions = exp.getInteractions();
//        for(Interaction interaction : interactions){
//            System.out.println("interaction.getShortLabel() = " + interaction.getShortLabel());
//        }
    }

}

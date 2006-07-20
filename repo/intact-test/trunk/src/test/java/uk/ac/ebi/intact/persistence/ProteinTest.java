/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Institution;
import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jul-2006</pre>
 */
public class ProteinTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ProteinTest.class);

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

    public void testCreateInstitution()
    {
        Institution institution = new Institution("EBITEST");
        institution.setFullName("Test Institution");
        institution.setAc("EBITEST-1");

        DaoFactory.getInstitutionDao().persist(institution);
    }

    public void testCreateProtein()
    {
        //Protein protein = DaoFactory.getProteinDao().getByAc("EBITEST-1");
        //Assert.assertNull(protein);
    }

    public void testGetProtein()
    {
        Protein protein = DaoFactory.getProteinDao().getByAc("EBITEST-2");
        Assert.assertNull(protein);
    }

}

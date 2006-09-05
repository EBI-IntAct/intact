/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jul-2006</pre>
 */
public class ProteinTest extends AbstractIntactTest
{

    private static final Log log = LogFactory.getLog(ProteinTest.class);

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    public void testCreateInstitution()
    {
        Institution institution = new Institution("ebi-test");
        institution.setFullName("Test Institution");

        getDaoFactory().getInstitutionDao().persist(institution);
    }

    public void testGetProtein()
    {
        Protein protein = getDaoFactory().getProteinDao().getByAc("NOTHING-2");
        Assert.assertNull(protein);
    }

}

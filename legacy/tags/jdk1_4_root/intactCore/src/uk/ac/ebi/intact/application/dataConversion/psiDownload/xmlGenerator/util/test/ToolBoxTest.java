/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.util.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.PsiDownloadTest;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.util.ToolBox;
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Xref;

/**
 * ToolBox Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/13/2005</pre>
 */
public class ToolBoxTest extends PsiDownloadTest {
    public ToolBoxTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( ToolBoxTest.class );
    }

    /////////////////////
    // Tests

    public void testGetPsiReference() throws Exception {
        Institution institution = new Institution( "ebi" );
        CvObject aliasType = new CvAliasType( institution, "aliass" );

        // test without PSi Xref
        String psiRef = ToolBox.getPsiReference( aliasType );
        assertNull( psiRef );

        // test with PSI Xref
        aliasType.addXref( new Xref( institution, psi, "MI:xxx", null, null, identity ) );

        psiRef = ToolBox.getPsiReference( aliasType );
        assertNotNull( psiRef );
        assertEquals( "MI:xxx", psiRef );
    }
}

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.HostOrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.HostOrganismParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class HostOrganismParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public HostOrganismParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( HostOrganismParserTest.class );
    }

    public void testProcess() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.HOST_ORGANISM_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        HostOrganismTag bioSource = null;
        bioSource = HostOrganismParser.process( element );

        /**
         * REMARK: we don't test here the data contained in the XML file, we have those from Newt.
         * We can't really know what will be returned for the shortlabel and the fullName so we only
         * check the taxId.
         */
        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );
    }
}

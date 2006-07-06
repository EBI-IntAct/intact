/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.CellTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.HostOrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.TissueTag;
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
public class HostOrganismParserTest extends ParserTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( HostOrganismParserTest.class );
    }

    private HostOrganismTag parse( String xmlContent ) {

        clearParserMessages();

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        HostOrganismTag hostOrganism = HostOrganismParser.process( element );
        displayExistingMessages();

        return hostOrganism;
    }

    public void testProcessWithCellTypeAndTissue() {

        HostOrganismTag bioSource = parse( MockXmlContent.HOST_ORGANISM_1 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNotNull( cellType );
        assertEquals( "MI:987", cellType.getPsiDefinition().getId() );
        assertEquals( "9876", cellType.getShortlabel() );

        TissueTag tissue = bioSource.getTissue();
        assertNotNull( tissue );
        assertEquals( "MI:123", tissue.getPsiDefinition().getId() );
        assertEquals( "1234", tissue.getShortlabel() );
    }

    public void testProcessOnlyTaxId() {

        HostOrganismTag bioSource = parse( MockXmlContent.HOST_ORGANISM_2 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNull( cellType );

        TissueTag tissue = bioSource.getTissue();
        assertNull( tissue );
    }

    public void testProcessWithTissue() {

        HostOrganismTag bioSource = parse( MockXmlContent.HOST_ORGANISM_3 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNull( cellType );

        TissueTag tissue = bioSource.getTissue();
        assertNotNull( tissue );
        assertEquals( "MI:123", tissue.getPsiDefinition().getId() );
        assertEquals( "1234", tissue.getShortlabel() );
    }

    public void testProcessWithMinimalTissue() {

        HostOrganismTag bioSource = parse( MockXmlContent.HOST_ORGANISM_3b );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNull( cellType );

        TissueTag tissue = bioSource.getTissue();
        assertNotNull( tissue );
        assertNull( tissue.getPsiDefinition() );
        assertEquals( "1234", tissue.getShortlabel() );
    }

    public void testProcessWithCellType() {

        HostOrganismTag bioSource = parse( MockXmlContent.HOST_ORGANISM_4 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNotNull( cellType );
        assertEquals( "MI:987", cellType.getPsiDefinition().getId() );
        assertEquals( "1234", cellType.getShortlabel() );

        TissueTag tissue = bioSource.getTissue();
        assertNull( tissue );
    }
}
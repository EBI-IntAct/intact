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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.CellTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.OrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.TissueTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.OrganismParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class OrganismParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public OrganismParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( OrganismParserTest.class );
    }

    private OrganismTag parse( String xmlContent ) {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        return OrganismParser.process( element );
    }

    public void testProcessWithCellTypeAndTissue() {

        OrganismTag bioSource = parse( MockXmlContent.ORGANISM_1 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNotNull( cellType );
        assertEquals( "MI:987", cellType.getPsiDefinition().getId() );

        TissueTag tissue = bioSource.getTissue();
        assertNotNull( tissue );
        assertEquals( "MI:123", tissue.getPsiDefinition().getId() );
    }

    
    public void testProcessOnlyTaxId() {

        OrganismTag bioSource = parse( MockXmlContent.ORGANISM_2 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNull( cellType );

        TissueTag tissue = bioSource.getTissue();
        assertNull( tissue );
    }


    public void testProcessWithTissue() {

        OrganismTag bioSource = parse( MockXmlContent.ORGANISM_3 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNull( cellType );

        TissueTag tissue = bioSource.getTissue();
        assertNotNull( tissue );
        assertEquals( "MI:123", tissue.getPsiDefinition().getId() );
    }


    public void testProcessWithCellType() {

        OrganismTag bioSource = parse( MockXmlContent.ORGANISM_4 );

        assertNotNull( bioSource );
        assertEquals( "4932", bioSource.getTaxId() );

        CellTypeTag cellType = bioSource.getCellType();
        assertNotNull( cellType );
        assertEquals( "MI:987", cellType.getPsiDefinition().getId() );

        TissueTag tissue = bioSource.getTissue();
        assertNull( tissue );
    }
}

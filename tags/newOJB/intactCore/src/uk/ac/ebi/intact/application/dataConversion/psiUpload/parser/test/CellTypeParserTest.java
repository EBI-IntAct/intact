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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.HostOrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.TissueTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.HostOrganismParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.CellTypeParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class CellTypeParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public CellTypeParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( CellTypeParserTest.class );
    }

    private CellTypeTag parse( String xmlContent ) {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        return CellTypeParser.process( element );
    }

    public void test() {

        CellTypeTag cellType = parse( MockXmlContent.CELL_TYPE );

        assertNotNull( cellType );

        assertNotNull( cellType );
        XrefTag definition = cellType.getPsiDefinition();
        assertNotNull( definition );
        assertEquals( "MI:987", definition.getId() );
        assertEquals( "psi-mi", definition.getDb() );
        assertEquals( "myCellType", cellType.getShortlabel() );
    }
}

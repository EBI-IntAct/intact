/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class MockDocumentBuilder {

    public static Document build( InputStream is ) {

        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( is );

        } catch ( SAXException sxe ) {
            // Error generated during parsing.
            Exception x = sxe;
            if ( sxe.getException() != null ) {
                x = sxe.getException();
            }
            x.printStackTrace();

        } catch ( ParserConfigurationException pce ) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch ( IOException ioe ) {
            // I/O error
            ioe.printStackTrace();
        }

        return document;
    }
}

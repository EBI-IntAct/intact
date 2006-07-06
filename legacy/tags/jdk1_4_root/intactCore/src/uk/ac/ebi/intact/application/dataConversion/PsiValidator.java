// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PsiValidator {

    private static class MyErrorHandler extends DefaultHandler {

        private PrintStream out;
        boolean warning = false;
        boolean error = false;
        boolean fatal = false;

        //////////////////////
        // Constructors

        public MyErrorHandler( PrintStream out ) {

            if( out == null ) {
                throw new NullPointerException( "You must give a valid PrintStream" );
            }
            this.out = out;
        }

        public MyErrorHandler() {
            this.out = System.out;
        }


        //////////////////
        // Getters

        public boolean hasWarning() {
            return warning;
        }

        public boolean hasError() {
            return error;
        }

        public boolean hasFatal() {
            return fatal;
        }

        ///////////////
        // Overriding

        public void warning( SAXParseException e ) throws SAXException {
            warning = true;
            System.out.println( "Warning: " );
            printInfo( e );
        }

        public void error( SAXParseException e ) throws SAXException {
            error = true;
            System.out.println( "Error: " );
            printInfo( e );
        }

        public void fatalError( SAXParseException e ) throws SAXException {
            fatal = true;
            System.out.println( "Fattal error: " );
            printInfo( e );
        }

        private void printInfo( SAXParseException e ) {
            out.println( "   Public ID: " + e.getPublicId() );
            out.println( "   System ID: " + e.getSystemId() );
            out.println( "   Line number: " + e.getLineNumber() );
            out.println( "   Column number: " + e.getColumnNumber() );
            out.println( "   Message: " + e.getMessage() );
        }
    }

    ///////////////////////////
    // D E M O

    public static boolean validate( File file ) {

        String parserClass = "org.apache.xerces.parsers.SAXParser";
        String validationFeature = "http://xml.org/sax/features/validation";
        String schemaFeature = "http://apache.org/xml/features/validation/schema";

        MyErrorHandler handler = new MyErrorHandler() ;

        try {

            String filename = file.getAbsolutePath();

            System.out.println( "Validating " + filename );

            XMLReader r = XMLReaderFactory.createXMLReader( parserClass );
            r.setFeature( validationFeature, true );
            r.setFeature( schemaFeature, true );

            r.setErrorHandler( handler );
            r.parse( filename );

        } catch ( SAXException e ) {
            System.out.println( e.toString() );

        } catch ( IOException e ) {
            System.out.println( e.toString() );
        }

        System.out.println( "Validation completed." );

        if( handler.hasError() == false &&
            handler.hasFatal() == false &&
            handler.hasWarning() == false ) {
            System.out.println( "The document is valid." );
            return true;
        }

        System.out.println( "The document is not valid." );
        return false;
    }
}
// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;

public class PsiValidator {

    private static final Logger log = Logger.getLogger( PsiValidator.class );

    private static class MyErrorHandler extends DefaultHandler {

        private PrintStream out;
        boolean warning = false;
        boolean error = false;
        boolean fatal = false;

        //////////////////////
        // Constructors

        public MyErrorHandler( PrintStream out ) {

            if ( out == null ) {
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
            //System.out.println( "Warning: " );
            log.warn( "SAX Warning", e );
            printInfo( e );
        }

        public void error( SAXParseException e ) throws SAXException {
            error = true;
            //System.out.println( "Error: " );
            log.error( "SAX Error", e );
            printInfo( e );
        }

        public void fatalError( SAXParseException e ) throws SAXException {
            fatal = true;
            //System.out.println( "Fattal error: " );
            log.error( "SAX Fatal error", e );
            printInfo( e );
        }

        private void printInfo( SAXParseException e ) {
            StringBuffer sb = new StringBuffer();
            sb.append( "   Public ID: " + e.getPublicId() ).append( "\n" );
            sb.append( "   System ID: " + e.getSystemId() ).append( "\n" );
            ;
            sb.append( "   Line number: " + e.getLineNumber() ).append( "\n" );
            ;
            sb.append( "   Column number: " + e.getColumnNumber() ).append( "\n" );
            ;
            sb.append( "   Message: " + e.getMessage() );

            log.debug( sb.toString() );

            out.print( sb.toString() );
        }
    }


    public static boolean validate( File file ) throws FileNotFoundException {
        String filename = file.getAbsolutePath();

        log.debug( "Validating " + filename );

        InputSource inputSource = new InputSource( new FileReader( filename ) );

        return validate( inputSource );
    }

    public static boolean validate( String xmlString ) {

        InputSource inputSource = new InputSource( new StringReader( xmlString ) );

        return validate( inputSource );
    }

    public static boolean validate( InputSource inputSource ) {

        String parserClass = SAXParser.class.getName();
        String validationFeature = "http://xml.org/sax/features/validation";
        String schemaFeature = "http://apache.org/xml/features/validation/schema";

        MyErrorHandler handler = new MyErrorHandler();

        try {

            XMLReader r = XMLReaderFactory.createXMLReader( parserClass );
            r.setFeature( validationFeature, true );
            r.setFeature( schemaFeature, true );

            r.setErrorHandler( handler );
            r.setEntityResolver(new XsdEntityResolver());

            r.parse( inputSource );

        } catch ( SAXException e ) {
            e.printStackTrace();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        log.info( "Validation completed." );

        if ( handler.hasError() == false &&
             handler.hasFatal() == false &&
             handler.hasWarning() == false ) {
            log.info( "The document is valid." );
            return true;
        }

        log.error( "The document is not valid." );
        return false;
    }
}
/*package uk.ac.ebi.intact.curationTools.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import uk.ac.ebi.intact.curationTools.proteinIdentification.jaxb.ObjectFactory;
import uk.ac.ebi.intact.curationTools.proteinIdentification.jaxb.Results;
import uk.ac.ebi.intact.curationTools.proteinIdentification.jaxb.SetOfResults;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;*/

/**
 * The writer of a protein IdentificationResult
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-May-2010</pre>
 */

//public class IdentificationResultWriter {

    /**
     * Sets up a logger for that class.
     */
   // public static final Log log = LogFactory.getLog( IdentificationResultWriter.class );

    /////////////////////////
    // Private methods

    /*private Marshaller getMarshaller( SetOfResults result ) throws IdentificationResultWritingException {

        try {
            // create a JAXBContext capable of handling classes generated into the jaxb package
            ClassLoader cl = ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance( uk.ac.ebi.intact.curationTools.proteinIdentification.jaxb.Results.class.getPackage().getName(), cl );

            // create and return Unmarshaller
            Marshaller marshaller = jc.createMarshaller();

            return marshaller;
        } catch ( Exception e ) {
            throw new IdentificationResultWritingException( "En error occured while writing Results", e );
        }
    }

    private void marshall( SetOfResults result , OutputStream os ) throws IdentificationResultWritingException {

        if ( os == null ) {
            throw new IllegalArgumentException( "You must give a non null otuput stream." );
        }

        try {
            // create a marshaller
            Marshaller m = getMarshaller( result );

            m.marshal( result, os );
        } catch ( Exception e ) {
            throw new IdentificationResultWritingException( "En error occured while writing Results", e );
        }
    }

    private void marshall( SetOfResults result, Writer writer ) throws IdentificationResultWritingException {

        if ( writer == null ) {
            throw new IllegalArgumentException( "You must give a non null writer." );
        }

        try {
            // create a marshaller
            Marshaller m = getMarshaller( result );

            m.marshal( result, writer );
        } catch ( Exception e ) {
            throw new IdentificationResultWritingException( "En error occured while writing Results", e );
        }
    }

    private void marshall( SetOfResults result, File file ) throws IdentificationResultWritingException {
        try {
            // create a marshaller
            Marshaller m = getMarshaller( result );

            m.marshal( result, new FileWriter( file ) );
        } catch ( Exception e ) {
            throw new IdentificationResultWritingException( "En error occured while writing Results", e );
        }
    }

    private String marshall( SetOfResults result ) throws IdentificationResultWritingException {

        Writer writer = new StringWriter( 4096 );

        try {
            Marshaller marshaller = getMarshaller( result );
            marshaller.marshal( result, writer );

            writer.close();
            return writer.toString();
        } catch ( Exception e ) {
            throw new IdentificationResultWritingException( "En error occured while writing Result", e );
        }
    }
}*/

/**
 *
 */
package uk.ac.ebi.intact.confidence.blastmapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;

/**
 * @author iarmean
 */
public class BlastMappingReader {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( BlastMappingReader.class );

    // //////////////////////
    // Private methods

    private Unmarshaller getUnmarshaller() throws JAXBException {

        // create a JAXBContext capable of handling classes generated into the
        // jaxb package
        JAXBContext jc = JAXBContext.newInstance( "uk.ac.ebi.intact.confidence.blastmapping.jaxb" );

        // create and return Unmarshaller

        // TODO enable/disable validation use setSchema( s ) on Marshaller
        return jc.createUnmarshaller();
    }

    private EBIApplicationResult unmarshall( URL url ) throws JAXBException, FileNotFoundException {

        if ( url == null ) {
            throw new IllegalArgumentException( "You must give a non null URL." );
        }

        // create an Unmarshaller
        Unmarshaller u = getUnmarshaller();

        // unmarshal an entrySet instance document into a tree of Java content
        // objects composed of classes from the jaxb package.
        return ( EBIApplicationResult ) u.unmarshal( url );
    }

    private EBIApplicationResult unmarshall( File file ) throws JAXBException, FileNotFoundException {

        if ( file == null ) {
            throw new IllegalArgumentException( "You must give a non null file." );
        }

        if ( !file.exists() ) {
            throw new IllegalArgumentException( "You must give an existing file. : " + file.getPath() );
        }

        if ( !file.canRead() ) {
            throw new IllegalArgumentException( "You must give a readable file." );
        }

        // create an Unmarshaller
        Unmarshaller u = getUnmarshaller();

        // unmarshal an entrySet instance document into a tree of Java content
        // objects composed of classes from the jaxb package.

       // Object o = u.unmarshal( new FileInputStream( file ) );
        if (log.isDebugEnabled()){
            log.debug("unmarshal : " + file.getPath());
        }
//        if ( EBIApplicationResult.class.isAssignableFrom( o.getClass() ) ) {
//            return ( EBIApplicationResult ) u.unmarshal( new FileInputStream( file ) );
//        }
//        if ( JAXBElement.class.isAssignableFrom( o.getClass() ) ) {
//            return ( EBIApplicationResult ) ( ( JAXBElement ) u.unmarshal( new FileInputStream( file ) ) ).getValue();
//        }

        try {
            return ( EBIApplicationResult ) u.unmarshal( new FileInputStream( file ) );
        } catch (ClassCastException e){
            if (log.isWarnEnabled()){
                log.warn("ClassCastException: file: " + file.getPath());
            }
        }
      //  return (EBIApplicationResult) ((JAXBElement)u.unmarshal(new FileInputStream(file))).getValue(); 
        //( CVMappingType ) ( ( JAXBElement ) u.unmarshal( new FileInputStream( file ) ) ).getValue();
        return null;
    }

    private EBIApplicationResult unmarshall( InputStream is ) throws JAXBException {

        if ( is == null ) {
            throw new IllegalArgumentException( "You must give a non null input stream." );
        }

        // create an Unmarshaller
        Unmarshaller u = getUnmarshaller();

        // unmarshal an entrySet instance document into a tree of Java content
        // objects composed of classes from the jaxb package.
        return ( EBIApplicationResult ) u.unmarshal( is );
    }

    private EBIApplicationResult unmarshall( String s ) throws JAXBException {

        if ( s == null ) {
            throw new IllegalArgumentException( "You must give a non null String." );
        }

        // create an Unmarshaller
        Unmarshaller u = getUnmarshaller();

        // unmarshal an entrySet instance document into a tree of Java content
        // objects composed of classes from the jaxb package.
        return ( EBIApplicationResult ) u.unmarshal( new StringReader( s ) );
    }

    // ////////////////////////
    // Public methods

    public EBIApplicationResult read( String s ) throws BlastMappingException {
        try {
            return unmarshall( s );
        } catch ( JAXBException e ) {
            throw new BlastMappingException( e );
        }
    }

    public EBIApplicationResult read( File file ) throws BlastMappingException {
        try {
            return unmarshall( file );
        } catch ( JAXBException e ) {
            throw new BlastMappingException( e );
        } catch ( FileNotFoundException e ) {
            throw new BlastMappingException( e );
        }
    }

    public EBIApplicationResult read( InputStream is ) throws BlastMappingException {
        try {
            return unmarshall( is );
        } catch ( JAXBException e ) {
            throw new BlastMappingException( e );
        }
    }

    public EBIApplicationResult read( URL url ) throws BlastMappingException {
        try {
            return unmarshall( url );
        } catch ( JAXBException e ) {
            throw new BlastMappingException( e );
        } catch ( FileNotFoundException e ) {
            throw new BlastMappingException( e );
        }
    }
}

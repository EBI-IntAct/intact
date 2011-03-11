package uk.ac.ebi.intact.plugins;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.plugin.MojoExecutionException;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;
import uk.ac.ebi.intact.bridges.ontologies.util.OntologyUtils;
import uk.ac.ebi.intact.core.context.IntactContext;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Export a database to MITAB.
 * <p/>
 * Same code as the DatabaseMitabExporterMojo but without the Maven overhead.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.1
 */
public class DatabaseMitabExporter {

    private static final Log log = LogFactory.getLog( DatabaseMitabExporter.class );

    public void export( String mitabFilePath, boolean overwrite, List<OntologyMapping> ontologyMappings  ) throws IOException, MitabExporterException {

        log.debug( "Output MITAB: " + mitabFilePath );
        log.debug( "overwrite:    " + overwrite );
        log.debug( "Ontologies:" );
        if( ontologyMappings != null ) {
            for ( OntologyMapping mapping : ontologyMappings ) {
                log.debug( "\t - " + mapping.getName() + ": " + mapping.getUrl() );

            }
        }

        File mitabFile = new File( mitabFilePath );
        if ( mitabFile.exists() && !overwrite ) {
            throw new IllegalArgumentException( "MITAB file exist and overwrite parameter set to false: " + mitabFilePath );
        }
        Writer mitabWriter = null;
        try {
            mitabWriter = new BufferedWriter( new FileWriter( mitabFile ) );
        } catch ( IOException e ) {
            throw new MitabExporterException( "Failed to prepare a MITAB writer", e );
        }

        final String tempDir = System.getProperty( "java.io.tmpdir" );
        final String separator = System.getProperty( "file.separator" );
        final String random = String.valueOf( System.currentTimeMillis() );

        File interactionIndexDir = new File( tempDir + separator + "interaction-" + random  );
        interactionIndexDir.deleteOnExit();

        File interactorIndexDir = new File( tempDir + separator + "interactor-" + random  );
        interactorIndexDir.deleteOnExit();

        File ontologyIndexDir = new File( tempDir + separator + "ontologies-" + random  );
        ontologyIndexDir.deleteOnExit();

        Directory interactionDirectory = null;
        try {
            interactionDirectory = FSDirectory.getDirectory( interactionIndexDir );
        } catch ( IOException e ) {
            throw new MitabExporterException( "Failed to build the interaction Directory", e );
        }

        Directory interactorDirectory = null;
        try {
            interactorDirectory = FSDirectory.getDirectory( interactorIndexDir );
        } catch ( IOException e ) {
            throw new MitabExporterException( "Failed to build the interactor Directory", e );
        }


        // Action !!

        Directory ontologyIndex = null;
        try {
            log.debug( "Starting to index ontologies..." );
            ontologyIndex = FSDirectory.getDirectory( ontologyIndexDir );
            OntologyUtils.buildIndexFromObo( ontologyIndex, ontologyMappings.toArray( new OntologyMapping[ontologyMappings.size()] ), true );
            log.debug( "Completed ontologies indexing..." );
        } catch ( Throwable e ) {
            throw new MitabExporterException( "Error while building ontology index.", e );
        }

        Collection<String> ontologyNames = new ArrayList<String>( ontologyMappings.size() );
        for ( OntologyMapping ontology : ontologyMappings ) {
            ontologyNames.add( ontology.getName() );
        }

        OntologyIndexSearcher ontologyIndexSearcher = new OntologyIndexSearcher( ontologyIndex );

        uk.ac.ebi.intact.psimitab.converters.util.DatabaseMitabExporter exporter =
                new uk.ac.ebi.intact.psimitab.converters.util.DatabaseMitabExporter( ontologyIndexSearcher,
                                                                                     ontologyNames.toArray( new String[ontologyNames.size()] ) );
        try {
            log.debug( "Starting to export MITAB..." );
            exporter.exportAllInteractors( mitabWriter, interactionDirectory, interactorDirectory );
            log.debug( "Completed MITAB export..." );
        } catch ( Throwable e ) {
            throw new MitabExporterException( "Error while exporting MITAB data.", e );
        }

        ontologyIndexSearcher.close();
    }


    //////////////////////////
    // Runner

    public static void main( String[] args ) throws IOException, MitabExporterException {

        if( args.length < 1 || (args.length % 2 ) != 1 ) {
            // at least a file name
            // then a list off pairs of ontologyName-URL
            // so the total count should be an odd number.
            System.err.println( "usage: DatabaseMitabExporter <mitab.output.file> [<ontologyName> <ontologyOboUrl>]*" );
            System.exit( 1 );
        }

        // Init database - this spring file is a template with a number of variables that should be filled by whatever maven profile given on the command line
        // db.hbm2ddl
        // db.dialect
        // db.driver
        // db.jdbcurl
        // db.user
        // db.password
        IntactContext.initContext(new String[] {"/META-INF/intact.spring.xml"});

        String mitabFile = args[0];

        List<OntologyMapping> ontologyMappings = new ArrayList<OntologyMapping>();
        if( args.length > 1 ) {
            // we know it is an odd number so we can read values in pairs
            for ( int i = 1; i < args.length; ) {
                String ontologyName = args[i];
                String url = args[i+1];
                ontologyMappings.add( new OntologyMapping( ontologyName, new URL( url ) ) );

                i += 2;
            }
        }

        final DatabaseMitabExporter exporter = new DatabaseMitabExporter();
        exporter.export( mitabFile, false, ontologyMappings );
    }
}

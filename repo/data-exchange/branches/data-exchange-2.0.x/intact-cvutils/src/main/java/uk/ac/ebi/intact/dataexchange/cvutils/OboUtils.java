package uk.ac.ebi.intact.dataexchange.cvutils;

import org.obo.dataadapter.DefaultOBOParser;
import org.obo.dataadapter.OBOParseEngine;
import org.obo.dataadapter.OBOParseException;
import org.obo.dataadapter.OBOFileAdapter;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.OBOObject;
import org.obo.datamodel.Link;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.bbop.dataadapter.DataAdapterException;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfoDataset;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfoDatasetFactory;
import uk.ac.ebi.intact.dataexchange.cvutils.model.IntactOntology;
import uk.ac.ebi.intact.model.CvDagObject;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Set of methods to deal with OBO files
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OboUtils {

    private static final Log log = LogFactory.getLog( OboUtils.class );

    private static final String PSI_MI_OBO_LOCATION_OLD = "http://intact.svn.sourceforge.net/viewvc/*checkout*/intact/repo/utils/data/controlledVocab/psi-mi25-4intact.obo";
    private static final String PSI_MI_LOCAL_ANNOTATIONS = "http://intact.svn.sourceforge.net/viewvc/*checkout*/intact/repo/utils/data/controlledVocab/additional-annotations.csv";

    //file location for OBO file pointing directly to psi cvs
    public static final String PSI_MI_OBO_LOCATION = "http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/mi/rel25/data/psi-mi25.obo";

    private OboUtils() {}

    public static OBOSession createOBOSession( URL... paths ) throws IOException, OBOParseException {
        String[] strPaths = new String[paths.length];

        for ( int i = 0; i < strPaths.length; i++ ) {
            strPaths[i] = paths[i].toString();
        }

        return createOBOSession( strPaths );
    }

    public static OBOSession createOBOSession( String... paths ) throws IOException, OBOParseException {
        DefaultOBOParser parser = new DefaultOBOParser();
        OBOParseEngine engine = new OBOParseEngine( parser );
        //OBOParseEngine can parse several files at once
        //and create one munged-together ontology,
        //so we need to provide a Collection to the setPaths() method
        engine.setPaths( Arrays.asList( paths ) );
        engine.parse();
        OBOSession session = parser.getSession();
        return session;
    }

    public static OBOSession createOBOSessionFromLatestMi() throws IOException, OBOParseException {
        return createOBOSessionFromDefault( "HEAD" );

    }

    public static AnnotationInfoDataset createAnnotationInfoDatasetFromLatestResource() throws IOException {
        URL url = new URL( PSI_MI_LOCAL_ANNOTATIONS );
        return createAnnotationInfoDatasetFromResource( url.openStream() );
    }

    public static OBOSession createOBOSessionFromDefault( String revision ) throws IOException, OBOParseException {
        URL url = new URL( PSI_MI_OBO_LOCATION + "?revision=" + revision );
        return createOBOSession( url );
    }

    public static AnnotationInfoDataset createAnnotationInfoDatasetFromDefault( int revision ) throws IOException, PsiLoaderException {
        URL url = new URL( PSI_MI_LOCAL_ANNOTATIONS + "?revision=" + revision );
        return createAnnotationInfoDatasetFromResource( url.openStream() );
    }

    public static AnnotationInfoDataset createAnnotationInfoDatasetFromResource( InputStream is ) throws IOException {
        return AnnotationInfoDatasetFactory.buildFromCsv( is );
    }

    public static AnnotationInfoDataset createAnnotationInfoDatasetFromResource( InputStream is, char separator ) throws IOException {
        return AnnotationInfoDatasetFactory.buildFromCsv( is, separator, '\b' );
    }

    public static AnnotationInfoDataset createAnnotationInfoDatasetFromResource( InputStream is, char separator, char delimiter ) throws IOException {
        return AnnotationInfoDatasetFactory.buildFromCsv( is, separator, delimiter );
    }

    @Deprecated
    public static IntactOntology createOntologyFromOboLatestPsiMi() throws IOException, PsiLoaderException {
        URL url = new URL( PSI_MI_OBO_LOCATION_OLD );
        return createOntologyFromObo( url );
    }

    @Deprecated
    public static IntactOntology createOntologyFromOboDefault( int revision ) throws IOException, PsiLoaderException {
        URL url = new URL( PSI_MI_OBO_LOCATION_OLD + "?revision=" + revision );
        return createOntologyFromObo( url );
    }

    @Deprecated
    public static IntactOntology createOntologyFromObo( URL url ) throws IOException, PsiLoaderException {
        PSILoader psi = new PSILoader();
        IntactOntology ontology = psi.parseOboFile( url );

        return ontology;
    }

    @Deprecated
    public static IntactOntology createOntologyFromObo( File oboFile ) throws IOException, PsiLoaderException {
        PSILoader psi = new PSILoader();
        IntactOntology ontology = psi.parseOboFile( oboFile );

        return ontology;
    }

    //////////////////////////
    // OBO hierarchy methods

    private static void findParents( Collection<OBOObject> parents, OBOObject oboObject, boolean recursive, Set<String> processedIdSet ) {

        final String id = oboObject.getID();
        if( processedIdSet.contains( id ) ) {
            return; // cuts infinite loop as the ontology can have cycles.
        } else  {
            processedIdSet.add( id );
        }

        for ( Link parentLink : oboObject.getParents() ) {

            OBOObject parent = (OBOObject) parentLink.getParent();
            parents.add( parent );

            if( recursive && !parent.getParents().isEmpty() ) {
                if ( log.isTraceEnabled() ) {
                    log.trace( "Calling recursively findParent( "+ parent.getID() +" - "+ parent.getName() +" ) ..." );
                }
                findParents( parents, parent, recursive, processedIdSet );
            }
        }
    }

    public static Collection<OBOObject> findParents( OBOObject oboObject, boolean recursive ) {
        final Collection<OBOObject> parents = Lists.newArrayList();
        final Set<String> processedIdSet = Sets.newHashSet();
        findParents( parents, oboObject, recursive, processedIdSet );
        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + parents.size() + " parents for term " + oboObject.getID() );
        }
        return parents;
    }

    private static void findChildren( Collection<OBOObject> children, OBOObject oboObject, boolean recursive, Set<String> processedIdSet ) {

        final String id = oboObject.getID();
        if( processedIdSet.contains( id ) ) {
            return; // cuts infinite loop as the ontology can have cycles.
        } else  {
            processedIdSet.add( id );
        }

        for ( Link childLink : oboObject.getChildren() ) {
            OBOObject child = (OBOObject) childLink.getChild();
            children.add( child );
            if( recursive && !child.getChildren().isEmpty() ) {
                if ( log.isTraceEnabled() ) {
                    log.trace( "Calling recursively findChildren( "+ child.getID() +" - "+ child.getName() +" ) ..." );
                }
                findChildren( children, child, recursive, processedIdSet );
            }
        }
    }

    public static Collection<OBOObject> findChildren( OBOObject oboObject, boolean recursive ) {
        List<OBOObject> children = Lists.newArrayList();
        final Set<String> processedIdSet = Sets.newHashSet();
        findChildren( children, oboObject, recursive, processedIdSet );
        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + children.size() + " children for term " + oboObject.getID() );
        }
        return children;
    }

    ////////////////////
    // Session I/O

    public static void saveSession( OBOSession session, File outputFile ) throws DataAdapterException {
        if ( log.isTraceEnabled() ) {
            log.trace( "Writting OBO session containing "+ session.getObjects().size() +
                       " term(s) to " + outputFile.getAbsolutePath() );
        }

        final OBOFileAdapter.OBOAdapterConfiguration config = new OBOFileAdapter.OBOAdapterConfiguration();
        config.setWritePath( outputFile.getAbsolutePath() );
        OBOFileAdapter adapter = new OBOFileAdapter();
        adapter.doOperation( OBOFileAdapter.WRITE_ONTOLOGY, config, session );
    }
}
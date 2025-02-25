package uk.ac.ebi.intact.application.hierarchview.highlightment.source.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.tab.model.CrossReference;
import uk.ac.ebi.intact.application.hierarchview.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchview.business.graph.Network;
import uk.ac.ebi.intact.application.hierarchview.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchview.struts.view.utils.SourceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.service.graph.Node;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Interface allowing to wrap an highlightment source.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class GoHighlightmentSource extends NodeHighlightmentSource {

    private static final Log logger = LogFactory.getLog( GoHighlightmentSource.class );

    /**
     * The key for this source 'go'
     */
    public static final String SOURCE_KEY;

    /**
     * The class for this source 'go'
     */
    static final String SOURCE_CLASS;

    private static final String goPath;

    private Map<String, CrossReference> goRefMap;

    private Map <String, Set<String>> goNodeMap;

    static {

        // get in the Highlightment properties file where is go
        Properties props = IntactUserI.HIGHLIGHTING_PROPERTIES;

        if ( null == props ) {
            String msg = "Unable to find the go hostname. The properties file '"
                         + StrutsConstants.HIGHLIGHTING_PROPERTY_FILE
                         + "' couldn't be loaded.";
            logger.error( msg );
            throw new IntactException( msg );
        }

        SOURCE_KEY = props.getProperty( "highlightment.source.node.GO.label" );
        if ( null == SOURCE_KEY ) {
            String msg = "Unable to find the go Label. "
                         + "Check the 'highlightment.source.node.GO.label' property in the '"
                         + StrutsConstants.HIGHLIGHTING_PROPERTY_FILE
                         + "' properties file";
            logger.error( msg );
            throw new IntactException( msg );
        }

        goPath = props.getProperty( "highlightment.source.node.GO.applicationPath" );

        if ( null == goPath ) {
            String msg = "Unable to find the interpro hostname. "
                         + "Check the 'highlightment.source.node.GO.goPath' property in the '"
                         + StrutsConstants.HIGHLIGHTING_PROPERTY_FILE
                         + "' properties file";
            logger.error( msg );
            throw new IntactException( msg );
        }

        SOURCE_CLASS = props.getProperty( "highlightment.source.node.GO.class" );

        if ( null == SOURCE_CLASS ) {
            String msg = "Unable to find the GO Class. "
                         + "Check the 'highlightment.source.node.GO.class' property in the '"
                         + StrutsConstants.HIGHLIGHTING_PROPERTY_FILE
                         + "' properties file";
            logger.error( msg );
            throw new IntactException( msg );
        }
    }

    public GoHighlightmentSource() {
        goRefMap = new HashMap<String, CrossReference>();
        goNodeMap = new Hashtable();
    }

    public void addToSourceMap( String termId, CrossReference termObject ) {
        goRefMap.put( termId, termObject );
    }

    public void addToNodeMap( String termId, Node node ) {

        // the nodes realted to the given sourceID are fetched
        Set<String> sourceNodes = goNodeMap.get( termId );

        // if no set exists a new one is created and put into the sourceMap
        if ( sourceNodes == null ) {
            // a hashset is used to avoid duplicate entries
            sourceNodes = new HashSet<String>();
            goNodeMap.put( termId, sourceNodes );
        }
        sourceNodes.add( node.getId() );
    }

    public Map<String, Set<String>> getNodeMap() {
        return goNodeMap;
    }

    public List<SourceBean> getSourceUrls(Network network,
                                          Collection<String> selectedSourceTerms,
                                          HttpServletRequest request, String applicationPath) {

//        if ( goNodeMap == null || goNodeMap.isEmpty() ) {
//            network.initHighlightMap(request);
//        }

        List<SourceBean> urls = new ArrayList<SourceBean>();

        if ( goNodeMap != null && !goNodeMap.isEmpty() ) {
            Set<String> keySet = goNodeMap.keySet();

            if ( keySet != null && !keySet.isEmpty() ) {
                // Cloning the current KeySet, because map could mixed up if user is to fast
                Set<String> cloneKeySet = new HashSet();
                cloneKeySet.addAll( keySet );
                keySet = cloneKeySet;

                for ( String termInfo : keySet ) {

                    String termType = SOURCE_KEY;
                    String termId = termInfo;
                    String termDescription = null;

                    if ( goRefMap != null ) {
                        CrossReference xref = goRefMap.get( termInfo );
                        if ( xref != null ) {
                            termId = xref.getIdentifier();

                            if ( xref.hasText() ) {
                                termDescription = xref.getText();
                            }
                        }
                    }

                    int termCount = goNodeMap.get( termId ).size();

                    // to summarize
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( "goTermType=" + termType + " | " +
                                      "goTermId=" + termId + " | " +
                                      "goTermDescription=" + termDescription + " | " +
                                      "goTermCount=" + termCount );
                    }

                    /*
                    * In order to avoid the browser to cache the response to that request
                    * we stick at its end of the generated URL.
                    */
                    String randomParam = "&now=" + System.currentTimeMillis();
                    String directHighlightUrl = null;
                    String hierarchViewUrl = getHierarchViewUrl( randomParam, applicationPath );
                    String quickGoUrl = goPath + "/DisplayGoTerm?id=" + termId + "&format=contentonly";
                    String quickGoGraphUrl = goPath + "/DisplayGoTerm?selected="
                                             + termId + "&intact=true&format=contentonly&url="
                                             + hierarchViewUrl + "&frame=_top";

                    boolean selected = false;
                    if ( selectedSourceTerms != null ) {
                        if ( selectedSourceTerms.contains( termId ) ) {
                            if ( logger.isInfoEnabled() ) logger.info( termId + " SELECTED" );
                            selected = true;
                        }
                        directHighlightUrl = getDirectHighlightUrl( applicationPath, termId, selectedSourceTerms, termType, randomParam );
                    } else {
                        directHighlightUrl = getDirectHighlightUrl( applicationPath, termId, termType, randomParam );
                    }

                    urls.add( new SourceBean( termId, termType, termDescription, termCount,
                                              quickGoUrl, quickGoGraphUrl, directHighlightUrl, selected,
                                              applicationPath ) );
                }
                // sort the source list by count
                Collections.sort( urls );
            }
        }
        return urls;
    }

    public void prepare() {
        goNodeMap.clear();
        goRefMap.clear();
    }    

    public static GoHighlightmentSource getInstance(HttpServletRequest request) {
        String attName = GoHighlightmentSource.class.getName();

        final GoHighlightmentSource source;

        if (request.getSession().getAttribute(attName) != null) {
            source = (GoHighlightmentSource) request.getSession().getAttribute(attName);
        } else {
            source = new GoHighlightmentSource();
            request.getSession().setAttribute(attName, source);
        }

        return source;
    }
}


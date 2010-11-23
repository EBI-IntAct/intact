package uk.ac.ebi.intact.bridges.olslight;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Light OLS client using JSON to transfer data.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class OlsLightService implements OntologyService {

    // TODO provide access to term hierarchy.

    private static final Log    log          = LogFactory.getLog( OlsLightService.class );

    private static final String CHILDREN_KEY = "children";
    private static final String ID_KEY       = "id";
    private static final String NAME_KEY     = "name";

    private Map<String, String> getJsonChildren( final OntologyId ontologyId,
                                                 final String parentTerm,
                                                 final int depth,
                                                 final boolean includeParent ) throws OntologyServiceException {

        Map<String, String> id2nameMap = new HashMap<String, String>();
        final String jsonQuery = "http://www.ebi.ac.uk/ontology-lookup/json/termchildren?termId=" + parentTerm + "&ontology=" + ontologyId.getId() + "&depth=" + depth;

        String jsonData = getJsonData( jsonQuery );

        JSONObject json = ( JSONObject ) JSONSerializer.toJSON( jsonData );

        if( includeParent ){
            String termId = json.getString( ID_KEY );
            String termName = json.getString( NAME_KEY );
            id2nameMap.put( termId, termName );
        }

        if( json.has( CHILDREN_KEY ) ){
            JSONArray children = json.getJSONArray( CHILDREN_KEY );
            getMapIdNameFromJsonArray( id2nameMap, children );
        }

        return id2nameMap;
    }

    private void getMapIdNameFromJsonArray( final Map<String, String> id2nameMap, final JSONArray term ) throws OntologyServiceException {
        for( int i = 0; i < term.size(); ++ i ){
            JSONObject child = term.getJSONObject( i );
            String termId = child.getString( ID_KEY );
            String termName = child.getString( NAME_KEY );
            id2nameMap.put( termId, termName );
            if( child.has( CHILDREN_KEY ) ){
                JSONArray children = child.getJSONArray( CHILDREN_KEY );
                if( children.size() > 0 ){
                    getMapIdNameFromJsonArray( id2nameMap, children );
                }
            }
        }
    }

    //////////////////////
    // Ontology Service

    private String getJsonData( final String url ) throws OntologyServiceException {
        if( log.isTraceEnabled() ){
            log.trace( "jsonQuery = " + url );
        }

        final StringBuilder jsonBuffer = new StringBuilder( 512 );
        try {
            final URL jsonUrl = new URL( url );
            final URLConnection olsConnection = jsonUrl.openConnection();
            final BufferedReader in = new BufferedReader( new InputStreamReader( olsConnection.getInputStream() ) );
            String inputLine;
            while( (inputLine = in.readLine()) != null ) {
                jsonBuffer.append( inputLine );
            }
            in.close();

        } catch( MalformedURLException e ){
            throw new OntologyServiceException( "Malformed URL: " + url, e );
        } catch( IOException e ){
            throw new OntologyServiceException( "Error while reading JSON data from: " + url, e );
        }

        return jsonBuffer.toString();
    }

    /**
     * Finds the name of a given ontology term.
     *
     * @param ontologyId the ontology to query.
     * @param termId     the ontology term.
     * @return a ontology term name.
     * @throws OntologyServiceException should anything go wrong.
     */
    @Override
    public String getTermName( OntologyId ontologyId, String termId ) throws OntologyServiceException {
        Map<String, String> children = getJsonChildren( ontologyId, termId, 0, true );
        return children.get( termId );
    }

    /**
     * Collects the direct children of the given term.
     *
     * @param ontologyId the ontology to query.
     * @param termId     the ontology term.
     * @return a non null Map(id -> name) of the direct children of the given term.
     * @throws OntologyServiceException should anything go wrong.
     */
    @Override
    public Map<String, String> getTermDirectChildren( OntologyId ontologyId, String termId ) throws OntologyServiceException {
        return getJsonChildren( ontologyId, termId, 1, false );
    }

    /**
     * Collects the children (potentially over several hierarchy level) of the given term.
     *
     * @param ontologyId the ontology to query.
     * @param termId     the ontology term.
     * @param depth      the depth into which we look.
     * @return a non null Map(id -> name) of the direct children of the given term.
     * @throws OntologyServiceException should anything go wrong.
     */
    @Override
    public Map<String, String> getTermChildren( OntologyId ontologyId, String termId, int depth ) throws OntologyServiceException {
        return getJsonChildren( ontologyId, termId, depth, false );
    }
}

/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.view.webapp.controller.browse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.hupo.psi.mi.psicquic.model.PsicquicSolrServer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrSearchResult;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrSearcher;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.view.webapp.controller.JpaBaseController;
import uk.ac.ebi.intact.view.webapp.controller.config.IntactViewConfiguration;
import uk.ac.ebi.intact.view.webapp.controller.search.SearchController;
import uk.ac.ebi.intact.view.webapp.controller.search.UserQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the browse tab.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
@Controller( "browseBean" )
@Scope( "conversation.access" )
@ConversationName( "general" )
public class BrowseController extends JpaBaseController {

    private static final Log log = LogFactory.getLog( BrowseController.class );

    //identifier separators
    public static final String INTERPRO_SEPERATOR = ",";
    public static final String CHROMOSOME_SEPERATOR = ";id=";
    public static final String EXPRESSION_SEPERATOR = "+";

    private int maxSize = 200;

    private Set<String> uniprotAcs;

    //browsing
    private String interproIdentifierList;
    private String chromosomalLocationIdentifierList;
    private String mRNAExpressionIdentifierList;
    private String[] reactomeIdentifierList;

    public BrowseController() {
    }


    public String createListofIdentifiersAndBrowse() {
        createListOfIdentifiers();
        return "/pages/browse/browse?faces-redirect=true&includeViewParams=true";
    }

    public void createListOfIdentifiers() {
        buildListOfIdentifiers();
        SearchController searchController = (SearchController) getBean("searchBean");
        searchController.doInteractorsSearch();
    }

    private void buildListOfIdentifiers() {
        uniprotAcs = new HashSet<String>(maxSize);

        final String uniprotFieldNameA = FieldNames.ID_A_FACET;
        final String uniprotFieldNameB = FieldNames.ID_B_FACET;

        UserQuery userQuery = (UserQuery) getBean("userQuery");
        IntactViewConfiguration intactViewConfig = (IntactViewConfiguration) getBean("intactViewConfiguration");
        final SolrServer solrServer = intactViewConfig.getInteractionSolrServer();
        IntactSolrSearcher solrSearcher = new IntactSolrSearcher(solrServer);

        int numberUniprotProcessed = 0;
        int first = 0;
        while (numberUniprotProcessed < maxSize){
            try {
                String [] facetFields = buildFacetFields(uniprotFieldNameA, uniprotFieldNameB, numberUniprotProcessed);

                IntactSolrSearchResult result = solrSearcher.searchWithFacets(userQuery.getSearchQuery(), 0, 0, PsicquicSolrServer.RETURN_TYPE_COUNT, new String[] {FieldNames.ID_FACET+":\"uniprotkb:*\""}, facetFields, first, maxSize);

                List<FacetField> facetFieldList = result.getFacetFieldList();
                if (facetFieldList == null || facetFieldList.isEmpty()){
                    break;
                }

                for (FacetField facet : facetFieldList){
                    // collect uniprot ids
                    for (FacetField.Count count : facet.getValues()){
                        if (numberUniprotProcessed < maxSize){
                            String uniprotkbPrefix=CvDatabase.UNIPROT+":";
                            // only process uniprot ids
                            if (count.getName().startsWith(uniprotkbPrefix)){
                                if (uniprotAcs.add(count.getName().substring(uniprotkbPrefix.length()+1))){
                                    numberUniprotProcessed++;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                addErrorMessage("Problem loading uniprot ACs", e.getMessage());
                e.printStackTrace();
                break;
            }
        }

        if (log.isDebugEnabled()) log.debug("Browse uniprot ACs: "+uniprotAcs);

        this.interproIdentifierList = appendIdentifiers( uniprotAcs, INTERPRO_SEPERATOR);
        this.chromosomalLocationIdentifierList = appendIdentifiers( uniprotAcs, CHROMOSOME_SEPERATOR);
        this.mRNAExpressionIdentifierList = appendIdentifiers( uniprotAcs, EXPRESSION_SEPERATOR);
        this.reactomeIdentifierList =  uniprotAcs.toArray( new String[uniprotAcs.size()] );
    }

    private String[] buildFacetFields(String uniprotFieldNameA, String uniprotFieldNameB, int numberUniprotProcessed) {
        String[] facetFields;

        // collect uniprot ids
        if (numberUniprotProcessed < 200){
            facetFields = new String[]{uniprotFieldNameA, uniprotFieldNameB};
        }
        else {
            facetFields = new String[]{};
        }
        return facetFields;
    }

    private String appendIdentifiers( Collection<String> uniqueIdentifiers, String separator ) {
        if ( uniqueIdentifiers != null && separator != null && !uniqueIdentifiers.isEmpty()) {
            return StringUtils.join( uniqueIdentifiers, separator );
        }

        return "";
    }

    public Set<String> getUniprotAcs() {
        return uniprotAcs;
    }

    public String getInterproIdentifierList() {
        return interproIdentifierList;
    }

    public void setInterproIdentifierList( String interproIdentifierList ) {
        this.interproIdentifierList = interproIdentifierList;
    }

    public String getChromosomalLocationIdentifierList() {
        return chromosomalLocationIdentifierList;
    }

    public void setChromosomalLocationIdentifierList( String chromosomalLocationIdentifierList ) {
        this.chromosomalLocationIdentifierList = chromosomalLocationIdentifierList;
    }

    public String getMRNAExpressionIdentifierList() {
        return mRNAExpressionIdentifierList;
    }

    public void setMRNAExpressionIdentifierList( String mRNAExpressionIdentifierList ) {
        this.mRNAExpressionIdentifierList = mRNAExpressionIdentifierList;
    }

    public String[] getReactomeIdentifierList() {
        return reactomeIdentifierList;
    }

    public void setReactomeIdentifierList( String[] reactomeIdentifierList ) {
        this.reactomeIdentifierList = reactomeIdentifierList;
    }

    public int getMaxSize() {
        return maxSize;
    }
}

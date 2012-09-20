package uk.ac.ebi.intact.view.webapp.controller.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.view.webapp.IntactViewException;
import uk.ac.ebi.intact.view.webapp.application.OntologyInteractorTypeConfig;
import uk.ac.ebi.intact.view.webapp.application.PsicquicThreadConfig;
import uk.ac.ebi.intact.view.webapp.controller.JpaBaseController;
import uk.ac.ebi.intact.view.webapp.controller.application.StatisticsController;
import uk.ac.ebi.intact.view.webapp.controller.browse.BrowseController;
import uk.ac.ebi.intact.view.webapp.controller.config.IntactViewConfiguration;
import uk.ac.ebi.intact.view.webapp.controller.details.DetailsController;
import uk.ac.ebi.intact.view.webapp.controller.list.InteractorListController;
import uk.ac.ebi.intact.view.webapp.controller.moleculeview.MoleculeViewController;
import uk.ac.ebi.intact.view.webapp.model.InteractorSearchResultDataModel;
import uk.ac.ebi.intact.view.webapp.model.InteractorWrapper;
import uk.ac.ebi.intact.view.webapp.model.LazySearchResultDataModel;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Search controller.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller("searchBean")
@Scope("conversation.access")
@ConversationName("general")
public class SearchController extends JpaBaseController {

    private static final Log log = LogFactory.getLog(SearchController.class);

    @Autowired
    private IntactViewConfiguration intactViewConfiguration;

    private int totalResults;
    private int interactorTotalResults;
    private int proteinTotalResults;
    private int smallMoleculeTotalResults;
    private int geneTotalResults;
    private int nucleicAcidTotalResults;

    private UserQuery userQuery;

    private String currentQuery;

    private boolean hasLoadedSearchControllerResults=false;
    private boolean hasLoadedInteractorResults=false;

    // results
    private LazySearchResultDataModel results;
    private InteractorSearchResultDataModel proteinResults;
    private InteractorSearchResultDataModel smallMoleculeResults;
    private InteractorSearchResultDataModel nucleicAcidResults;
    private InteractorSearchResultDataModel geneResults;

    // io
    private String exportFormat;

    //sorting
    private static final String DEFAULT_SORT_COLUMN = FieldNames.INTACT_SCORE_NAME;
    private static final boolean DEFAULT_SORT_ORDER = false;

    private String userSortColumn = DEFAULT_SORT_COLUMN;
    //as the Sort constructor is Sort(String field, boolean reverse)
    private boolean ascending = DEFAULT_SORT_ORDER;

    private ExecutorService executorService;

    @Autowired
    private PsicquicThreadConfig psicquicThreadConfig;

    private PsicquicSearchManager psicquicController;
    private BrowseController browseController;
    private OntologyInteractorTypeConfig typeConfig;

    private String acPrefix;

    private boolean filterNegative=false;
    private boolean filterSpoke=false;
    private boolean isOntologyQuery=false;

    public SearchController() {
    }

    @PostConstruct
    public void initialSearch() {
        StatisticsController statisticsController = (StatisticsController) getBean("statisticsController");
        this.totalResults = statisticsController.getBinaryInteractionCount();
        this.currentQuery = null;

        if (executorService == null){

            executorService = psicquicThreadConfig.getExecutorService();
        }

        try {
            this.psicquicController = new PsicquicSearchManager(this.executorService, this.intactViewConfiguration);
        } catch (PsicquicRegistryClientException e) {
            addErrorMessage("Problem counting results in other databases", "Registry not available");
            e.printStackTrace();
        }
    }

    public void searchOnLoad(ComponentSystemEvent evt) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            UserQuery userQuery = getUserQuery();
            if (this.currentQuery == null || !userQuery.getSearchQuery().equals(this.currentQuery) || !hasLoadedSearchControllerResults){
                doBinarySearch(userQuery.createSolrQuery());
                isOntologyQuery = false;
                filterNegative = false;
                filterSpoke = false;
            }
        }
    }

    public String doBinarySearchAction() {
        UserQuery userQuery = getUserQuery();
        SolrQuery solrQuery = userQuery.createSolrQuery();

        this.filterNegative=false;
        this.filterSpoke=false;
        this.isOntologyQuery=false;

        doBinarySearch(solrQuery);

        return "/pages/interactions/interactions.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public String doBinarySearchActionFromOntologySearch() {
        UserQuery userQuery = getUserQuery();
        SolrQuery solrQuery = userQuery.createSolrQueryForOntologySearch();

        this.filterNegative=false;
        this.filterSpoke=false;
        this.isOntologyQuery=true;

        doBinarySearch(solrQuery);

        return "/pages/interactions/interactions.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public String doBinarySearchActionFilterNegative() {
        UserQuery userQuery = getUserQuery();
        SolrQuery solrQuery = userQuery.createSolrQuery();

        // the true binary interactions have - in the complex expansion column
        solrQuery.addFilterQuery(FieldNames.NEGATIVE+":false");
        filterNegative = true;

        // add spoke filter if necessary
        if (filterSpoke){
            solrQuery.addFilterQuery(FieldNames.COMPLEX_EXPANSION+":\"-\"");
        }
        this.isOntologyQuery=false;
        doBinarySearchOnly(solrQuery);
        return "/pages/interactions/interactions.xhtml?faces-redirect=true&includeViewParams=true";
    }
    public String doBinarySearchActionFilterSpokeExpanded() {
        UserQuery userQuery = getUserQuery();
        SolrQuery solrQuery = userQuery.createSolrQuery();

        // the true binary interactions have - in the complex expansion column
        solrQuery.addFilterQuery(FieldNames.COMPLEX_EXPANSION+":\"-\"");
        filterSpoke = true;

        // add spoke filter if necessary
        if (filterNegative){
            solrQuery.addFilterQuery(FieldNames.NEGATIVE+":false");
        }

        doBinarySearchOnly(solrQuery);
        return "/pages/interactions/interactions.xhtml?faces-redirect=true&includeViewParams=true";
    }

    private void resetDetailControllers() {
        DetailsController detailsController = (DetailsController) getBean("detailsBean");
        MoleculeViewController moleculeViewController = (MoleculeViewController) getBean("moleculeViewBean");

        detailsController.setInteraction(null);
        moleculeViewController.setInteractor(null);
    }

    public void doBinarySearch(SolrQuery solrQuery) {

        try {
            if ( log.isDebugEnabled() ) {log.debug( "\tSolrQuery:  "+ solrQuery.getQuery() );}

            final SolrQuery solrQueryCopy = solrQuery;
            final SolrServer solrServer = intactViewConfiguration.getInteractionSolrServer();
            final int pageSize = getUserQuery().getPageSize();
            final PsicquicSearchManager psicquicController = this.psicquicController;
            final String query = solrQuery.getQuery();

            // prepare solr search
            Callable<Integer> intactRunnable = createIntactSearchRunnable(solrQueryCopy, solrServer, pageSize);

            // we are doing a new search so we reset browser
            hasLoadedSearchControllerResults = true;
            hasLoadedInteractorResults = false;

            Future<Integer> intactFuture = executorService.submit(intactRunnable);

            // count psicquic results while searching in Intact
            psicquicController.countResultsInOtherDatabases(query);

            // resume tasks
            checkAndResumeSearchTasks(intactFuture);
            psicquicController.checkAndResumePsicquicTasks();

            if ( log.isDebugEnabled() ) log.debug( "\tResults: " + totalResults );


        } catch ( uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrException solrException ) {

            final String query = solrQuery.getQuery();
            if ( query != null && ( query.startsWith( "*" ) || query.startsWith( "?" ) ) ) {
                getUserQuery().setSearchQuery( "*:*" );
                addErrorMessage( "Your query '"+ query +"' is not correctly formatted",
                        "Currently we do not support queries prefixed with wildcard characters such as '*' or '?'. " +
                                "However, wildcard characters can be used anywhere else in one's query (eg. g?vin or gav* for gavin). " +
                                "Please do reformat your query." );
            }
        }
    }

    public void doBinarySearchOnly(SolrQuery solrQuery) {
        try {
            if ( log.isDebugEnabled() ) {log.debug( "\tSolrQuery:  "+ solrQuery.getQuery() );}

            final SolrQuery solrQueryCopy = solrQuery;
            final SolrServer solrServer = intactViewConfiguration.getInteractionSolrServer();
            final int pageSize = getUserQuery().getPageSize();

            results = createInteractionDataModel( solrQueryCopy, solrServer, pageSize );

            totalResults = results.getRowCount();

            if ( log.isDebugEnabled() ) log.debug( "\tResults: " + results.getRowCount() );

            // store the current query
            this.currentQuery = solrQuery.getQuery();
            // we are doing a new search so we reset browser
            hasLoadedSearchControllerResults = true;
            hasLoadedInteractorResults = false;

        } catch ( uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrException solrException ) {

            final String query = solrQuery.getQuery();
            if ( query != null && ( query.startsWith( "*" ) || query.startsWith( "?" ) ) ) {
                getUserQuery().setSearchQuery( "*:*" );
                addErrorMessage( "Your query '"+ query +"' is not correctly formatted",
                        "Currently we do not support queries prefixed with wildcard characters such as '*' or '?'. " +
                                "However, wildcard characters can be used anywhere else in one's query (eg. g?vin or gav* for gavin). " +
                                "Please do reformat your query." );
            }
        }
    }

    private void checkAndResumeSearchTasks(Future<Integer> intactFuture) {

        try {
            this.totalResults = intactFuture.get();

        } catch (InterruptedException e) {
            log.error("The intact search was interrupted, we cancel the task.", e);
            if (!intactFuture.isCancelled()){
                intactFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            log.error("The intact search could not be executed, we cancel the task.", e);
            if (!intactFuture.isCancelled()){
                intactFuture.cancel(true);
            }
        }
    }

    private Callable<Integer> createIntactSearchRunnable(final SolrQuery solrQueryCopy, final SolrServer solrServer,
                                                         final int pageSize) {
        return new Callable<Integer>() {
                    public Integer call() {

                        results = createInteractionDataModel( solrQueryCopy, solrServer, pageSize );

                        // store the current query
                        currentQuery = solrQueryCopy.getQuery();

                        return results.getRowCount();
                    }
                };
    }

    public String doClearSearchAndGoHome() {
        getUserQuery().reset();

        return "/main?forces-redirect=true";
    }

    public void onTabChanged(TabChangeEvent evt) {
        // load interactions if necessary
        if (evt.getTab() != null && "interactionsTab".equals(evt.getTab().getId())){
            UserQuery userQuery = getUserQuery();
            if (this.currentQuery == null || !userQuery.getSearchQuery().equals(this.currentQuery) || !hasLoadedSearchControllerResults){
                doBinarySearch(userQuery.createSolrQuery());
            }
        }
        else if ("browseTab".equals(evt.getTab().getId())){
            if (browseController == null){
                browseController = (BrowseController) getBean("browseBean");
            }
            if (!browseController.hasLoadedUniprotAcs()){
                doBrowserSearch();
            }
        }
        /*if (evt.getTab() != null && "listsTab".equals(evt.getTab().getId())) {
            doInteractorsSearch();

        }*/
    }

    public void doBrowserSearch() {
        Callable<Set<String>> uniprotAcsRunnable = browseController.createBrowserInteractorListRunnable(getUserQuery(), browseController.getSolrSearcher());
        Future<Set<String>> uniprotAcsFuture = executorService.submit(uniprotAcsRunnable);

        if (this.currentQuery == null || !userQuery.getSearchQuery().equals(this.currentQuery) || !hasLoadedInteractorResults){
            doInteractorsSearch();
        }
        browseController.checkAndResumeBrowserInteractorListTasks(uniprotAcsFuture);
    }


    private LazySearchResultDataModel createInteractionDataModel(SolrQuery query, SolrServer solrServer, int pageSize) {

        final LazySearchResultDataModel resultDataModel = new LazySearchResultDataModel(solrServer, query);
        resultDataModel.setPageSize(pageSize);
        return resultDataModel;
    }

    public void doInteractorsSearch() {

        if (typeConfig == null){
            typeConfig = (OntologyInteractorTypeConfig) getBean("ontologyInteractorTypeConfig");

        }

        final OntologyInteractorTypeConfig config = typeConfig;
        final SolrQuery solrQuery = userQuery.createSolrQuery();
        final SolrServer solrServer = intactViewConfiguration.getInteractionSolrServer();
        final int pageSize = getUserQuery().getPageSize();

        Callable<InteractorSearchResultDataModel> proteinRunnable = createProteinSearchRunnable(config, solrQuery, solrServer, pageSize);
        Callable<InteractorSearchResultDataModel> compoundRunnable = createSmallMoleculeSearchRunnable(config, solrQuery, solrServer, pageSize);
        Callable<InteractorSearchResultDataModel> nucleicAcidRunnable = createNucleicAcidSearchRunnable(config, solrQuery, solrServer, pageSize);
        Callable<InteractorSearchResultDataModel> geneRunnable = createGeneSearchRunnable(config, solrQuery, solrServer, pageSize);

        Future<InteractorSearchResultDataModel> proteinFuture = executorService.submit(proteinRunnable);
        Future<InteractorSearchResultDataModel> compoundFuture = executorService.submit(compoundRunnable);
        Future<InteractorSearchResultDataModel> nucleicAcidFuture = executorService.submit(nucleicAcidRunnable);
        Future<InteractorSearchResultDataModel> geneFuture = executorService.submit(geneRunnable);

        checkAndResumeInteractorTasks(proteinFuture, compoundFuture, nucleicAcidFuture, geneFuture);

        interactorTotalResults = smallMoleculeTotalResults + proteinTotalResults + nucleicAcidTotalResults + geneTotalResults;

        // loaded browse results
        hasLoadedInteractorResults = true;
        if(this.currentQuery == null || !userQuery.getSearchQuery().equals(this.currentQuery)){
            hasLoadedSearchControllerResults = false;
        }
        this.currentQuery = userQuery.getSearchQuery();
    }

    private Callable<InteractorSearchResultDataModel> createProteinSearchRunnable(final OntologyInteractorTypeConfig typeConfig, final SolrQuery solrQuery, final SolrServer solrServer, final int pageSize) {
        return new Callable<InteractorSearchResultDataModel>() {
            public InteractorSearchResultDataModel call() {

                return doInteractorSearch(typeConfig.getProteinTypes(), solrQuery, solrServer, pageSize);
            }
        };
    }
    private Callable<InteractorSearchResultDataModel> createSmallMoleculeSearchRunnable(final OntologyInteractorTypeConfig typeConfig, final SolrQuery solrQuery, final SolrServer solrServer, final int pageSize) {
        return new Callable<InteractorSearchResultDataModel>() {
            public InteractorSearchResultDataModel call() {

                return doInteractorSearch(typeConfig.getCompoundTypes(), solrQuery, solrServer, pageSize);
            }
        };
    }
    private Callable<InteractorSearchResultDataModel> createNucleicAcidSearchRunnable(final OntologyInteractorTypeConfig typeConfig, final SolrQuery solrQuery, final SolrServer solrServer, final int pageSize) {
        return new Callable<InteractorSearchResultDataModel>() {
            public InteractorSearchResultDataModel call() {

                return doInteractorSearch(typeConfig.getNucleicAcidTypes(), solrQuery, solrServer, pageSize);
            }
        };
    }
    private Callable<InteractorSearchResultDataModel> createGeneSearchRunnable(final OntologyInteractorTypeConfig typeConfig, final SolrQuery solrQuery, final SolrServer solrServer, final int pageSize) {
        return new Callable<InteractorSearchResultDataModel>() {
            public InteractorSearchResultDataModel call() {

                return doInteractorSearch(typeConfig.getNucleicAcidTypes(), solrQuery, solrServer, pageSize);
            }
        };
    }

    private void checkAndResumeInteractorTasks(Future<InteractorSearchResultDataModel> proteinFuture, Future<InteractorSearchResultDataModel> compoundFuture,
                                               Future<InteractorSearchResultDataModel> nucleicAcidFuture, Future<InteractorSearchResultDataModel> geneFuture) {

        try {
            this.proteinResults = proteinFuture.get();
            this.proteinTotalResults = proteinResults.getRowCount();

        } catch (InterruptedException e) {
            log.error("The intact protein search was interrupted, we cancel the task.", e);
            if (!proteinFuture.isCancelled()){
                proteinFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            log.error("The intact protein search could not be executed, we cancel the task.", e);
            if (!proteinFuture.isCancelled()){
                proteinFuture.cancel(true);
            }
        }

        try {
            this.smallMoleculeResults = compoundFuture.get();
            this.smallMoleculeTotalResults = smallMoleculeResults.getRowCount();

        } catch (InterruptedException e) {
            log.error("The intact compound search was interrupted, we cancel the task.", e);
            if (!compoundFuture.isCancelled()){
                compoundFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            log.error("The intact compound search could not be executed, we cancel the task.", e);
            if (!compoundFuture.isCancelled()){
                compoundFuture.cancel(true);
            }
        }

        try {
            this.nucleicAcidResults = nucleicAcidFuture.get();
            this.nucleicAcidTotalResults = nucleicAcidResults.getRowCount();

        } catch (InterruptedException e) {
            log.error("The intact nucleic acid search was interrupted, we cancel the task.", e);
            if (!nucleicAcidFuture.isCancelled()){
                nucleicAcidFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            log.error("The intact nucleic acid search could not be executed, we cancel the task.", e);
            if (!nucleicAcidFuture.isCancelled()){
                nucleicAcidFuture.cancel(true);
            }
        }

        try {
            this.geneResults = geneFuture.get();
            this.geneTotalResults = geneResults.getRowCount();

        } catch (InterruptedException e) {
            log.error("The intact gene interactor was interrupted, we cancel the task.", e);
            if (!nucleicAcidFuture.isCancelled()){
                nucleicAcidFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            log.error("The intact gene interactor could not be executed, we cancel the task.", e);
            if (!nucleicAcidFuture.isCancelled()){
                nucleicAcidFuture.cancel(true);
            }
        }
    }

    public InteractorSearchResultDataModel doInteractorSearch(String interactorTypeMi) {
        final SolrQuery solrQuery = getUserQuery().createSolrQuery();
        final String [] interactorTypeMiArray = new String[] {interactorTypeMi};
        final SolrServer solrServer = intactViewConfiguration.getInteractionSolrServer();
        final int pageSize = getUserQuery().getPageSize();

        return doInteractorSearch(interactorTypeMiArray, solrQuery, solrServer, pageSize);
    }

    public InteractorSearchResultDataModel doInteractorSearch(final String[] interactorTypeMis, final SolrQuery solrQuery, final SolrServer solrServer, final int pageSize) {

        if (log.isDebugEnabled()) log.debug("Searching interactors of type ("+ Arrays.toString(interactorTypeMis)+") for query: " + solrQuery);

        final InteractorSearchResultDataModel interactorResults
                = new InteractorSearchResultDataModel(intactViewConfiguration.getInteractionSolrServer(),
                                                      solrQuery,
                                                      interactorTypeMis);
        interactorResults.setPageSize(pageSize);
        return interactorResults;
    }

    public void doSearchInteractionsFromCompoundListSelection(ActionEvent evt) {
        doSearchInteractionsFromListSelection((InteractorListController) getBean("compoundListController"));
    }

    public void doSearchInteractionsFromGeneListSelection(ActionEvent evt) {
        doSearchInteractionsFromListSelection((InteractorListController) getBean("geneListController"));
    }

    public void doSearchInteractionsFromProteinListSelection(ActionEvent evt) {
        doSearchInteractionsFromListSelection((InteractorListController) getBean("proteinListController"));
    }

    public void doSearchInteractionsFromDnaListSelection( ActionEvent evt ) {
        doSearchInteractionsFromListSelection((InteractorListController) getBean("nucleicAcidListController"));
    }

    private void doSearchInteractionsFromListSelection(InteractorListController interactorListController) {
        final List<InteractorWrapper> selected = Arrays.asList(interactorListController.getSelected());

        if (selected.size() == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder( selected.size() * 10 );
        sb.append("identifier:(");

        for (Iterator<InteractorWrapper> iterator = selected.iterator(); iterator.hasNext();) {
            InteractorWrapper interactorWrapper = iterator.next();

            sb.append(interactorWrapper.getAc());

            if (iterator.hasNext()) {
                sb.append(" ");
            }
        }

        sb.append(")");

        final String query = sb.toString();

        UserQuery userQuery = getUserQuery();
        userQuery.setSearchQuery( query );

        SolrQuery solrQuery = userQuery.createSolrQuery();

        doBinarySearch(solrQuery);
    }

    public String getIntactAc(BinaryInteraction binaryInteraction) {
        return getIntactAcXref(binaryInteraction).getIdentifier();
    }

    public CrossReference getMainAccessionXref(BinaryInteraction<?> binaryInteraction) {

        if (acPrefix == null){
            acPrefix = getIntactContext().getConfig().getAcPrefix();
        }
        CrossReference intactXref = null;

        for (CrossReference xref : binaryInteraction.getInteractionAcs()) {
            if (xref.getIdentifier().startsWith(acPrefix)) {
                return xref;
            }
            else if ("intact".equals(xref.getDatabase())) {
                intactXref = xref;
            }
        }

        return intactXref;
    }

    public CrossReference getIntactAcXref(BinaryInteraction<?> binaryInteraction) {

        for (CrossReference xref : binaryInteraction.getInteractionAcs()) {
            if ("intact".equals(xref.getDatabase())) {
                return xref;
            }
        }

        return null;
    }


    // Getters & Setters
    /////////////////////

    public LazySearchResultDataModel getResults() {
        return results;
    }

    public void setResults( LazySearchResultDataModel results ) {
        this.results = results;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat( String exportFormat ) {
        this.exportFormat = exportFormat;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults( int totalResults ) {
        this.totalResults = totalResults;
    }

    public int getInteractorTotalResults() {
        return interactorTotalResults;
    }

    public void setInteractorTotalResults( int interactorTotalResults ) {
        this.interactorTotalResults = interactorTotalResults;
    }

    public InteractorSearchResultDataModel getProteinResults() {
        return proteinResults;
    }

    public void setProteinResults( InteractorSearchResultDataModel proteinResults) {
        this.proteinResults = proteinResults;
    }

    public int getSmallMoleculeTotalResults() {
        return smallMoleculeTotalResults;
    }

    public int getProteinTotalResults() {
        return proteinTotalResults;
    }

    public InteractorSearchResultDataModel getSmallMoleculeResults() {
        return smallMoleculeResults;
    }

    public String getUserSortColumn() {
        return userSortColumn;
    }

    public void setUserSortColumn( String userSortColumn ) {
        this.userSortColumn = userSortColumn;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending( boolean ascending ) {
        this.ascending = ascending;
    }

    public int getNucleicAcidTotalResults() {
        return nucleicAcidTotalResults;
    }

    public void setNucleicAcidTotalResults( int nucleicAcidTotalResults) {
        this.nucleicAcidTotalResults = nucleicAcidTotalResults;
    }

    public int getGeneTotalResults() {
        return geneTotalResults;
    }

    public void setGeneTotalResults(int geneTotalResults) {
        this.geneTotalResults = geneTotalResults;
    }

    public InteractorSearchResultDataModel getNucleicAcidResults() {
        return nucleicAcidResults;
    }

    public InteractorSearchResultDataModel getGeneResults() {
        return geneResults;
    }

    private UserQuery getUserQuery() {
        if (userQuery == null){
           userQuery = (UserQuery) getBean("userQuery");
        }
        return userQuery;
    }

    public PsicquicSearchManager getPsicquicSearchManager() {
        return psicquicController;
    }

    public boolean hasLoadedCurrentQuery() {
        if (this.currentQuery == null || !userQuery.getSearchQuery().equals(this.currentQuery)){
            return false;
        }
        return true;
    }

    public boolean isOntologyQuery() {
        return isOntologyQuery;
    }

    public boolean isFilterNegative() {
        return filterNegative;
    }

    public boolean isFilterSpoke() {
        return filterSpoke;
    }

    public String getExportQueryParameters(){
        try {
            return "format="+exportFormat+"&query="+ URLEncoder.encode(getUserQuery().getSearchQuery(), "UTF-8").replaceAll("\\+", "%20")+"&negative="+filterNegative+"&spoke="+filterSpoke+"&ontology="+isOntologyQuery+"&sort="+userSortColumn+"&asc="+ascending;
        } catch (UnsupportedEncodingException e) {
            throw new IntactViewException("Invalid query", e);
        }
    }
}
package uk.ac.ebi.intact.editor.services.search;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.editor.services.summary.*;
import uk.ac.ebi.intact.editor.util.LazyDataModelFactory;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Search query service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
@Service
public class SearchQueryService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( SearchQueryService.class );

    @Resource(name = "publicationSummaryService")
    private PublicationSummaryService publicationSummaryService;

    @Resource(name = "complexSummaryService")
    private ComplexSummaryService complexSummaryService;

    @Resource(name = "experimentSummaryService")
    private ExperimentSummaryService experimentSummaryService;

    @Resource(name = "interactionSummaryService")
    private InteractionSummaryService interactionSummaryService;

    @Resource(name = "moleculeSummaryService")
    private MoleculeSummaryService moleculeSummaryService;

    @Resource(name = "cvSummaryService")
    private CvSummaryService cvSummaryService;

    //////////////////
    // Constructors

    public SearchQueryService() {
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<CvSummary> loadCvObjects( String query, String originalQuery ) {

        log.info( "Searching for CvObject matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        // all cvobjects
        LazyDataModel<CvSummary> cvobjects = LazyDataModelFactory.createLazyDataModel( this.cvSummaryService,

                                                              "select distinct i " +
                                                              "from IntactCvTerm i left join i.dbXrefs as x " +
                                                              "where    ( i.ac = :ac " +
                                                              "      or lower(i.shortName) like :query " +
                                                              "      or lower(i.fullName) like :query " +
                                                              "      or lower(x.id) like :query ) ",

                                                              "select count(distinct i) " +
                                                              "from IntactCvTerm i left join i.dbXrefs as x " +
                                                              "where   (i.ac = :ac " +
                                                              "      or lower(i.shortName) like :query " +
                                                              "      or lower(i.fullName) like :query " +
                                                              "      or lower(x.id) like :query )",

                                                              params, "i", "updated, i.ac", false);

        log.info( "CvObject found: " + cvobjects.getRowCount() );

        return cvobjects;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<MoleculeSummary> loadMolecules( String query, String originalQuery ) {

        log.info( "Searching for Molecules matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        // all molecules but interactions
        LazyDataModel<MoleculeSummary> molecules = LazyDataModelFactory.createLazyDataModel( moleculeSummaryService,

                                                              "select distinct i " +
                                                              "from IntactInteractor i left join i.dbXrefs as x " +
                                                              "where    ( i.ac = :ac " +
                                                              "      or lower(i.shortName) like :query " +
                                                              "      or lower(i.fullName) like :query " +
                                                              "      or lower(x.id) like :query ) ",
                                                              "select count(distinct i) " +
                                                              "from IntactInteractor i left join i.dbXrefs as x " +
                                                              "where   (i.ac = :ac " +
                                                              "      or lower(i.shortName) like :query " +
                                                              "      or lower(i.fullName) like :query " +
                                                              "      or lower(x.id) like :query )",

                                                              params, "i", "updated, i.ac", false );

        log.info( "Molecules found: " + molecules.getRowCount() );
        return molecules;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<MoleculeSummary> loadMoleculesByOrganism( String organismAc ) {

        log.info( "Searching for Molecules matching organism '" + organismAc + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "ac", organismAc );

        // all molecules but interactions
        LazyDataModel<MoleculeSummary> molecules = LazyDataModelFactory.createLazyDataModel( moleculeSummaryService,

                "select distinct i " +
                        "from IntactInteractor i join i.organism as o " +
                        "where  o.ac = :ac ",

                        "select count(distinct i) " +
                        "from IntactInteractor i join i.organism as o " +
                        "where o.ac = :ac",

                        params, "i", "updated, i.ac", false );

        log.info( "Molecules found: " + molecules.getRowCount() );
        return molecules;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<ComplexSummary> loadComplexesByOrganism( String organismAc ) {

        log.info( "Searching for Complexes matching organism '" + organismAc + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "ac", organismAc );

        // all molecules but interactions
        LazyDataModel<ComplexSummary> molecules = LazyDataModelFactory.createLazyDataModel( complexSummaryService,

                "select distinct i " +
                        "from IntactComplex i join i.organism as o " +
                        "where  o.ac = :ac ",

                        "select count(distinct i) " +
                        "from IntactComplex i join i.organism as o " +
                        "where o.ac = :ac",

                        params, "i", "updated, i.ac", false );

        log.info( "Molecules found: " + molecules.getRowCount() );
        return molecules;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countFeaturesByParticipantAc( IntactParticipantEvidence comp ) {
        return getIntactDao().getParticipantEvidenceDao().countFeaturesForParticipant(comp.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countModelledFeaturesByParticipantAc( String ac ) {
        return getIntactDao().getModelledParticipantDao().countFeaturesForParticipant(ac);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParticipantsExpressIn( String biosourceAc ) {
		return getIntactDao().getParticipantEvidenceDao().countParticipantsByExpressedInOrganism(biosourceAc);
	}

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countComplexesByOrganism( String biosourceAc ) {
        return getIntactDao().getComplexDao().countComplexesByOrganism(biosourceAc);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countExperimentsByHostOrganism( String biosourceAc ) {
		return getIntactDao().getExperimentDao().countExperimentsByHostOrganism(biosourceAc);
	}

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countInteractorsByOrganism( String biosourceAc ) {
		return getIntactDao().getInteractorDao(IntactInteractor.class).countInteractorsByOrganism(biosourceAc);
	}

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<InteractionSummary>  loadInteractions( String query, String originalQuery ) {

        log.info( "Searching for Interactions matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        // Load experiment eagerly to avoid LazyInitializationException when rendering the view
        LazyDataModel<InteractionSummary> interactions = LazyDataModelFactory.createLazyDataModel( interactionSummaryService,

                                                                 "select distinct i " +
                                                                 "from IntactInteractionEvidence i left join i.dbXrefs as x " +
                                                                 "where    (i.ac = :ac " +
                                                                 "      or lower(i.shortName) like :query " +
                                                                 "      or lower(x.id) like :query )",

                                                                 "select count(distinct i.ac) " +
                                                                 "from IntactInteractionEvidence i left join i.dbXrefs as x " +
                                                                 "where    (i.ac = :ac " +
                                                                 "      or lower(i.shortName) like :query " +
                                                                 "      or lower(x.id) like :query )",

                                                                 params, "i", "updated, i.ac", false );

        log.info( "Interactions found: " + interactions.getRowCount() );
        return interactions;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<InteractionSummary>  loadInteractionsByMolecule( String moleculeAc ) {

        log.info( "Searching for Interactions with molecule '" + moleculeAc + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "ac", moleculeAc );

        // Load experiment eagerly to avoid LazyInitializationException when rendering the view
        LazyDataModel<InteractionSummary> interactions = LazyDataModelFactory.createLazyDataModel( interactionSummaryService,

                "select distinct i " +
                        "from IntactInteractionEvidence i join i.participants as p join p.interactor as inter " +
                        "where  inter.ac = :ac",

                        "select count(distinct i.ac) " +
                        "from IntactInteractionEvidence i join i.participants as p join p.interactor as inter " +
                        "where  inter.ac = :ac",

                        params, "i", "updated, i.ac", false );

        log.info( "Interactions found: " + interactions.getRowCount() );
        return interactions;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<ComplexSummary>  loadComplexesByMolecule( String moleculeAc ) {

        log.info( "Searching for Complexes with molecule '" + moleculeAc + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "ac", moleculeAc );

        // Load experiment eagerly to avoid LazyInitializationException when rendering the view
        LazyDataModel<ComplexSummary> interactions = LazyDataModelFactory.createLazyDataModel( complexSummaryService,

                "select distinct i " +
                        "from IntactComplex i join i.participants as p join p.interactor as inter " +
                        "where  inter.ac = :ac",

                        "select count(distinct i.ac) " +
                        "from IntactComplex i join i.participants as p join p.interactor as inter " +
                        "where  inter.ac = :ac",

                        params, "i", "updated, i.ac", false );

        log.info( "Interactions found: " + interactions.getRowCount() );
        return interactions;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<ComplexSummary> loadComplexes( String query, String originalQuery ) {

        log.info( "Searching for Complexes matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );
        // Load experiment eagerly to avoid LazyInitializationException when rendering the view
        LazyDataModel<ComplexSummary> complexes = LazyDataModelFactory.createLazyDataModel( complexSummaryService,

                "select distinct i " +
                        "from IntactComplex i left join i.dbXrefs as x " +
                        "where    i.ac = :ac " +
                        "      or lower(i.shortName) like :query " +
                        "      or lower(x.id) like :query "+
                        "      or i.ac in (select distinct i2.ac from IntactComplex i2 left join i2.dbAliases as a " +
                        "      where lower(a.name) like :query ) "+
                        "      or i.ac in (select distinct i3.ac from IntactComplex i3 left join i3.organism as o " +
                        "      where lower(o.dbTaxid) = :ac )",

                "select count( distinct i.ac ) " +
                        "from IntactComplex i left join i.dbXrefs as x " +
                        "where    i.ac = :ac " +
                        "      or lower(i.shortName) like :query " +
                        "      or lower(x.id) like :query "+
                        "      or i.ac in (select distinct i2.ac from IntactComplex i2 left join i2.dbAliases as a " +
                        "      where lower(a.name) like :query ) "+
                        "      or i.ac in (select distinct i3.ac from IntactComplex i3 left join i3.organism as o " +
                        "      where lower(o.dbTaxid) = :ac )",

                params, "i", "updated, i.ac", false );

        log.info( "Complexes found: " + complexes.getRowCount() );
        return complexes;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<ExperimentSummary> loadExperiments( String query, String originalQuery ) {

        log.info( "Searching for experiments matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );
        params.put( "inferred", Experiment.INFERRED_BY_CURATOR );

        LazyDataModel<ExperimentSummary> experiments = LazyDataModelFactory.createLazyDataModel( experimentSummaryService,

                                                                "select distinct e " +
                                                                "from IntactExperiment e left join e.xrefs as x " +
                                                                        "left join e.interactionDetectionMethod as d " +
                                                                "where  d.shortName <> :inferred and  (e.ac = :ac " +
                                                                "      or lower(e.shortLabel) like :query " +
                                                                "      or lower(x.id) like :query)) ",

                                                                "select count(distinct e) " +
                                                                "from IntactExperiment e left join e.xrefs as x " +
                                                                        "left join e.interactionDetectionMethod as d " +
                                                                "where  d.shortName <> :inferred and (e.ac = :ac " +
                                                                "      or lower(e.shortLabel) like :query " +
                                                                "      or lower(x.id) like :query) ",

                                                                params, "e", "updated, e.ac", false );

        log.info( "Experiment found: " + experiments.getRowCount() );
        return experiments;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<ExperimentSummary> loadExperimentsByHostOrganism( String organismAc ) {

        log.info( "Searching for experiments matching organism '" + organismAc + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "ac", organismAc );
        params.put( "inferred", Experiment.INFERRED_BY_CURATOR );

        LazyDataModel<ExperimentSummary> experiments = LazyDataModelFactory.createLazyDataModel( experimentSummaryService,

                "select distinct e " +
                        "from IntactExperiment e join e.hostOrganism as o " +
                        "left join e.interactionDetectionMethod as d " +
                        "where d.shortName <> :inferred and o.ac = :ac ",

                "select count(distinct e) " +
                        "from IntactExperiment e join e.hostOrganism as o " +
                        "left join e.interactionDetectionMethod as d " +
                        "where d.shortName <> :inferred and o.ac = :ac ",

                params, "e", "updated, e.ac", false );

        log.info( "Experiment found: " + experiments.getRowCount() );
        return experiments;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<PublicationSummary> loadPublication( String query, String originalQuery ) {
        log.info( "Searching for publications matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );
        params.put( "intactReleased", "14681455" );
        params.put( "intactOnHold", "unassigned638" );
        params.put( "pdbOnHold", "24288376" );
        params.put( "chemblOnHold", "24214965" );

        LazyDataModel<PublicationSummary> publications = LazyDataModelFactory.createLazyDataModel( publicationSummaryService,

                                                                 "select distinct p " +
                                                                 "from IntactPublication p left join p.dbXrefs as x " +
                                                                 "where  p.shortLabel not in (:intactReleased, :intactOnHold, :pdbOnHold, :chemblOnHold) " +
                                                                 "and  (p.ac = :ac " +
                                                                 "      or lower(p.shortLabel) like :query " +
                                                                 "      or lower(p.title) like :query " +
                                                                 "      or lower(x.id) like :query) ",

                                                                 "select count(distinct p) " +
                                                                 "from IntactPublication p left join p.dbXrefs as x " +
                                                                 "where  p.shortLabel not in (:intactReleased, :intactOnHold, :pdbOnHold, :chemblOnHold) " +
                                                                 "and  (p.ac = :ac " +
                                                                 "      or lower(p.shortLabel) like :query " +
                                                                 "      or lower(p.title) like :query " +
                                                                 "      or lower(x.id) like :query) ",

                                                                 params, "p", "updated, p.ac", false );

        log.info( "Publications found: " + publications.getRowCount() );
        return publications;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactFeatureEvidence> loadFeatures( String query, String originalQuery ) {
        log.info( "Searching for features matching '" + query + "' or AC '"+originalQuery+"'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        LazyDataModel<IntactFeatureEvidence> features = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                                                                 "select distinct p " +
                                                                 "from IntactFeatureEvidence p left join p.dbXrefs as x " +
                                                                 "where  (p.ac = :ac " +
                                                                 "      or lower(p.shortName) like :query " +
                                                                 "      or lower(p.fullName) like :query " +
                                                                 "      or lower(x.id) like :query) ",

                                                                 "select count(distinct p) " +
                                                                 "from IntactFeatureEvidence p left join p.dbXrefs as x " +
                                                                 "where (p.ac = :ac " +
                                                                 "      or lower(p.shortName) like :query " +
                                                                 "      or lower(p.fullName) like :query " +
                                                                 "      or lower(x.id) like :query) ",

                                                                 params, "p", "updated, p.ac", false);

        log.info( "Features found: " + features.getRowCount() );
        return features;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactOrganism> loadOrganisms( String query, String originalQuery ) {
        log.info( "Searching for organisms matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        // Load experiment eagerly to avoid LazyInitializationException when redering the view
        LazyDataModel<IntactOrganism> organisms = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                                                                 "select distinct b " +
                                                                 "from IntactOrganism b " +
                                                                 "where    b.ac = :ac " +
                                                                 "      or lower(b.commonName) like :query " +
                                                                 "      or lower(b.scientificName) like :query " +
                                                                 "      or lower(b.dbTaxid) like :query ",

                                                                 "select count(distinct b) " +
                                                                 "from IntactOrganism b " +
                                                                 "where    b.ac = :ac " +
                                                                 "      or lower(b.commonName) like :query " +
                                                                 "      or lower(b.scientificName) like :query " +
                                                                 "      or lower(b.dbTaxid) like :query ",

                                                                 params, "b", "updated, b.ac", false);

        log.info( "Organisms found: " + organisms.getRowCount() );
        return organisms;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactParticipantEvidence> loadParticipants( String query, String originalQuery ) {
        log.info( "Searching for participants matching '" + query + "'..." );

        final HashMap<String, String> params = Maps.newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        // Load experiment eagerly to avoid LazyInitializationException when redering the view
        LazyDataModel<IntactParticipantEvidence> participants = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                "select distinct p " +
                        "from IntactParticipantEvidence p left join p.xrefs as x " +
                        "where  (p.ac = :ac " +
                        "      or lower(x.id) like :query) ",

                "select count(distinct p) " +
                        "from IntactParticipantEvidence p left join p.xrefs as x " +
                        "where (p.ac = :ac " +
                        "      or lower(x.id) like :query) ",

                params, "p", "updated, p.ac", false);

        log.info( "Participants found: " + participants.getRowCount() );
        return participants;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactParticipantEvidence> loadParticipantsByOrganism( String organismAc ) {
        log.info( "Searching for participants with organism '" + organismAc + "'..." );

        final HashMap<String, String> params = Maps.newHashMap();
        params.put( "ac", organismAc );

        // Load experiment eagerly to avoid LazyInitializationException when redering the view
        LazyDataModel<IntactParticipantEvidence> participants = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                "select distinct p " +
                        "from IntactParticipantEvidence p join p.expressedInOrganism as o " +
                        "where o.ac = :ac ",

                "select count(distinct p) " +
                        "from IntactParticipantEvidence p join p.expressedInOrganism as o " +
                        "where o.ac = :ac ",

                params, "p", "updated, p.ac", false);

        log.info( "Participants found: " + participants.getRowCount() );
        return participants;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactModelledFeature>  loadModelledFeatures(String query, String originalQuery) {
        log.info( "Searching for complex features matching '" + query + "' or AC '"+originalQuery+"'..." );

        final HashMap<String, String> params = Maps.<String, String>newHashMap();
        params.put( "query", query );
        params.put( "ac", originalQuery );

        LazyDataModel<IntactModelledFeature>  modelledFeatures = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                "select distinct p " +
                        "from IntactModelledFeature p left join p.dbXrefs as x " +
                        "where  p.ac = :ac " +
                        "      or lower(p.shortName) like :query " +
                        "      or lower(p.fullName) like :query " +
                        "      or lower(x.id) like :query ",

                "select count(distinct p) " +
                        "from IntactModelledFeature p left join p.dbXrefs as x " +
                        "where p.ac = :ac " +
                        "      or lower(p.shortName) like :query " +
                        "      or lower(p.fullName) like :query " +
                        "      or lower(x.id) like :query ",

                params, "p", "updated, p.ac", false);

        log.info( "Complex Features found: " + modelledFeatures.getRowCount() );
        return modelledFeatures;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactModelledParticipant> loadModelledParticipants(String finalQuery, String originalQuery) {
        log.info( "Searching for complex participants matching '" + finalQuery + "'..." );

        final HashMap<String, String> params = Maps.newHashMap();
        params.put( "query", finalQuery );
        params.put( "ac", originalQuery );

        // Load experiment eagerly to avoid LazyInitializationException when redering the view
        LazyDataModel<IntactModelledParticipant> modelledParticipants = LazyDataModelFactory.createLazyDataModel( getIntactDao().getEntityManager(),

                "select distinct p " +
                        "from IntactModelledParticipant p left join p.xrefs as x " +
                        "where  p.ac = :ac " +
                        "      or lower(x.id) like :query ",

                "select count(distinct p) " +
                        "from IntactModelledParticipant p left join p.xrefs as x " +
                        "where p.ac = :ac " +
                        "      or lower(x.id) like :query ",

                params, "p", "updated, p.ac", false);

        log.info( "Complex Participants found: " + modelledParticipants.getRowCount() );
        return modelledParticipants;
    }
}

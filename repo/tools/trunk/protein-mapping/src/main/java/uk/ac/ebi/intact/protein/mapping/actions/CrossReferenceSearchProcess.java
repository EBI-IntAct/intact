package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.SimpleUniprotRemoteService;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.ReflectionCrossReferenceBuilder;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.UniprotCrossReference;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.util.*;

/**
 * This class is querying uniprot to retrieve cross references and identifiers of an uniprot entry.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public class CrossReferenceSearchProcess extends ActionNeedingUniprotService{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( CrossReferenceSearchProcess.class );

    /**
     * the uniparc service
     */
    private UniParcQueryService uniParcQueryService = UniProtJAPI.factory.getUniParcQueryService();

    /**
     * The map allowing to convert a sequence database MI to a Uniprot database name
     */
    private Map<String, Set<DatabaseType>> psiMIDatabaseToUniprot = new HashMap<String, Set<DatabaseType>>();

    /**
     * The uniprot remote service
     */
    private static UniprotService uniprotService;

    /**
     * The swissprot database name in uniparc
     */
    private static final String swissprot = "UniProtKB/Swiss-Prot";

    /**
     * The swissprot database name for splice variants in uniparc
     */
    private static final String swissprot_sv = "uniprotkb/swiss-prot protein isoforms";

    /**
     * Create a new CrossReferenceSearchProcess and initialises the list of databases in uniprot with a MI number
     */
    public CrossReferenceSearchProcess(ReportsFactory factory){
        super(factory);
        initialisePsiMIDatabaseToUniprot();
        uniprotService = new SimpleUniprotRemoteService();
    }

    public CrossReferenceSearchProcess(ReportsFactory factory, UniprotService service){
        super(factory);
        initialisePsiMIDatabaseToUniprot();
        uniprotService = service != null ? service : new SimpleUniprotRemoteService();
    }

    /**
     * initialises the list of databases in uniprot with a MI number
     */
    private void initialisePsiMIDatabaseToUniprot(){
        HashSet<DatabaseType> CYGD = new HashSet<DatabaseType>();
        CYGD.add(DatabaseType.CYGD);
        psiMIDatabaseToUniprot.put("MI:0464", CYGD);
        HashSet<DatabaseType> ddbjEmblGenbank = new HashSet<DatabaseType>();
        ddbjEmblGenbank.add(DatabaseType.getDatabaseType("DDBJ"));
        ddbjEmblGenbank.add(DatabaseType.EMBL);
        ddbjEmblGenbank.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0475", ddbjEmblGenbank);
        HashSet<DatabaseType> ensembl = new HashSet<DatabaseType>();
        ensembl.add(DatabaseType.ENSEMBL);
        psiMIDatabaseToUniprot.put("MI:0476", ensembl);
        HashSet<DatabaseType> geneId = new HashSet<DatabaseType>();
        geneId.add(DatabaseType.GENEID);
        psiMIDatabaseToUniprot.put("MI:0477", geneId);
        HashSet<DatabaseType> flyBase = new HashSet<DatabaseType>();
        flyBase.add(DatabaseType.FLYBASE);
        psiMIDatabaseToUniprot.put("MI:0478", flyBase);
        HashSet<DatabaseType> mgi = new HashSet<DatabaseType>();
        mgi.add(DatabaseType.MGI);
        psiMIDatabaseToUniprot.put("MI:0479", mgi);
        HashSet<DatabaseType> mim = new HashSet<DatabaseType>();
        mim.add(DatabaseType.MIM);
        psiMIDatabaseToUniprot.put("MI:0480", mim);
        HashSet<DatabaseType> refSeq = new HashSet<DatabaseType>();
        refSeq.add(DatabaseType.REFSEQ);
        psiMIDatabaseToUniprot.put("MI:0481", refSeq);
        HashSet<DatabaseType> rgd = new HashSet<DatabaseType>();
        rgd.add(DatabaseType.RGD);
        psiMIDatabaseToUniprot.put("MI:0483", rgd);
        HashSet<DatabaseType> sgd = new HashSet<DatabaseType>();
        sgd.add(DatabaseType.SGD);
        psiMIDatabaseToUniprot.put("MI:0484", sgd);
        HashSet<DatabaseType> wormbase = new HashSet<DatabaseType>();
        wormbase.add(DatabaseType.WORMBASE);
        psiMIDatabaseToUniprot.put("MI:0487", wormbase);
        HashSet<DatabaseType> ipi = new HashSet<DatabaseType>();
        ipi.add(DatabaseType.IPI);
        psiMIDatabaseToUniprot.put("MI:0675", ipi);
        HashSet<DatabaseType> peptide = new HashSet<DatabaseType>();
        peptide.add(DatabaseType.PRIDE);
        peptide.add(DatabaseType.PEPTIDEATLAS);
        psiMIDatabaseToUniprot.put("MI:0737", peptide);
        HashSet<DatabaseType> pride = new HashSet<DatabaseType>();
        pride.add(DatabaseType.PRIDE);
        psiMIDatabaseToUniprot.put("MI:0738", pride);
        HashSet<DatabaseType> peptideAtlas = new HashSet<DatabaseType>();
        peptideAtlas.add(DatabaseType.PEPTIDEATLAS);
        psiMIDatabaseToUniprot.put("MI:0741", peptideAtlas);
        HashSet<DatabaseType> genBank = new HashSet<DatabaseType>();
        genBank.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0851", genBank);
        HashSet<DatabaseType> genBank2 = new HashSet<DatabaseType>();
        genBank2.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0860", genBank2);
        HashSet<DatabaseType> genBank3 = new HashSet<DatabaseType>();
        genBank3.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0852", genBank3);
        HashSet<DatabaseType> ensemblgenome = new HashSet<DatabaseType>();
        ensemblgenome.add(DatabaseType.ENSEMBLBACTERIA);
        ensemblgenome.add(DatabaseType.ENSEMBLFUNGI);
        ensemblgenome.add(DatabaseType.ENSEMBLMETAZOA);
        ensemblgenome.add(DatabaseType.ENSEMBLPLANTS);
        ensemblgenome.add(DatabaseType.ENSEMBLPROTISTS);
        psiMIDatabaseToUniprot.put("MI:1013", ensemblgenome);
    }

    /**
     *
     * @param database : the database
     * @return the appropriate name(s) in uniprot if database is a MI number of a sequence database existing in Uniprot, the database name as it was otherwise
     */
    private Set<DatabaseType> convertMINumberInUniprot(String database){

        Set<DatabaseType> name = new HashSet<DatabaseType>();

        if (database == null){
            return name;
        }
        if (this.psiMIDatabaseToUniprot.containsKey(database)){
            name.addAll(this.psiMIDatabaseToUniprot.get(database));
        }

        return name;
    }

    /**
     * Build the query in uniprot to get the uniprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the query as a String
     */
    /*private String buildUniprotDatabaseCrossReferenceQuery(String identifier){
        String query = IndexField.DATABASE_CROSSREFERENCES+ ":" + identifier +
                " OR " + IndexField.UNIPROT_ID+":"+identifier +
                " OR " + IndexField.UNIPROT_IDENTIFIER+":"+identifier +
                " OR " + IndexField.PRIMARY_ACCESSION+":"+identifier +
                " OR " + IndexField.UNIPROT_EXPIRED_IDENTIFIER+":"+identifier;
        return query ;
    }*/

    /**
     * Build the query in uniprot to get the swissprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private EntryIterator<UniProtEntry> getReviewedUniprotDatabaseCrossReferenceIterator(Set<DatabaseType> databases, String identifier){
        EntryIterator<UniProtEntry> iteratorId = uniProtQueryService.getEntryIterator(UniProtQueryBuilder.setReviewedEntries(UniProtQueryBuilder.buildExactMatchIdentifierQuery(identifier)));

        EntryIterator<UniProtEntry> iteratorUniprotXRef = null;

        if (databases.isEmpty()){
            return iteratorId;
        }

        for (DatabaseType database : databases){

            if (database != null){
                EntryIterator<UniProtEntry> singleDtabase = uniProtQueryService.getEntryIterator(UniProtQueryBuilder.setReviewedEntries(UniProtQueryBuilder.buildDatabaseCrossReferenceQuery(database, identifier)));
                if (iteratorUniprotXRef == null){
                    iteratorUniprotXRef = singleDtabase;
                }
                else {
                    iteratorUniprotXRef = uniProtQueryService.getEntryIterator(iteratorUniprotXRef, SetOperation.Or, iteratorUniprotXRef);
                }
            }
        }
        return  uniProtQueryService.getEntryIterator(iteratorId, SetOperation.Or, iteratorUniprotXRef);
    }

    /**
     * Build the query in uniprot to get the uniprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private EntryIterator<UniProtEntry> getUnreviewedUniprotDatabaseCrossReferenceIterator(Set<DatabaseType> databases, String identifier){
        EntryIterator<UniProtEntry> iteratorId = uniProtQueryService.getEntryIterator(UniProtQueryBuilder.setUnreviewedEntries(UniProtQueryBuilder.buildExactMatchIdentifierQuery(identifier)));
        EntryIterator<UniProtEntry> iteratorUniprotXRef = null;

        if (databases.isEmpty()){
            return iteratorId;
        }
        
        for (DatabaseType database : databases){

            if (database != null){
                EntryIterator<UniProtEntry> singleDtabase = uniProtQueryService.getEntryIterator(UniProtQueryBuilder.setUnreviewedEntries(UniProtQueryBuilder.buildDatabaseCrossReferenceQuery(database, identifier)));
                if (iteratorUniprotXRef == null){
                    iteratorUniprotXRef = singleDtabase;
                }
                else {
                    iteratorUniprotXRef = uniProtQueryService.getEntryIterator(iteratorUniprotXRef, SetOperation.Or, iteratorUniprotXRef);
                }
            }
        }
        return  uniProtQueryService.getEntryIterator(iteratorId, SetOperation.Or, iteratorUniprotXRef);
    }

    /**
     * Build the query in uniparc to get the uniparc entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private EntryIterator<UniParcEntry> getUniparcDatabaseCrossReferenceIterator(String identifier){

        List<String> ids = new ArrayList<String>();
        ids.add(identifier);

        EntryIterator<UniParcEntry> iteratorId = uniParcQueryService.getEntryIterator(UniParcQueryBuilder.buildIDListQuery(ids));
        EntryIterator<UniParcEntry> iteratorUniparcXRef = uniParcQueryService.getEntryIterator(UniParcQueryBuilder.buildActiveCrossReferenceQuery(identifier));

        return  uniParcQueryService.getEntryIterator(iteratorId, SetOperation.Or, iteratorUniparcXRef);
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact exact accession with this identifier
     */
    private boolean hasTheExactIdentifierInUniprot(UniProtEntry protein, String identifier){

        if (protein.getPrimaryUniProtAccession().getValue().equals(identifier)){
            return true;
        }

        if (!protein.getSecondaryUniProtAccessions().isEmpty()){
            for (SecondaryUniProtAccession ac : protein.getSecondaryUniProtAccessions()){
                if (ac.getValue() != null && ac.getValue().equals(identifier)){
                    return true;
                }
            }
        }
        if (protein.getUniProtId().getValue().equals(identifier)){
            return true;
        }

        return false;
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @param database : the database name of MI
     * @return the database name in Uniprot if there is an exact cross reference with this identifier
     */
    private String getTheDatabaseOfExactIdentifierInUniprotCrossReferences(UniProtEntry protein, String identifier, String database){

        Collection<DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();
        ReflectionCrossReferenceBuilder builder = new ReflectionCrossReferenceBuilder();

        for ( DatabaseCrossReference ref : databaseCrossReferences ) {
            UniprotCrossReference uref = builder.build( ref );

            if (uref.getAccessionNumber().equals(identifier)){
                String databaseUniprot = uref.getDatabase();

                return databaseUniprot;
            }
        }

        return null;
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @return the database name if there is an exact uniparc cross reference with this identifier
     */
    private String getTheDatabaseOfExactIdentifierInUniparcCrossReferences(UniParcEntry protein, String identifier, String database){

        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();

        for ( uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference ref : databaseCrossReferences ) {

            if (ref.getAccession().equals(identifier)){
                String databaseName = ref.getDatabase().getName();
                return databaseName;
            }
        }

        return null;
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact uniparc accession with this identifier
     */
    private boolean hasTheExactIdentifierInUniparc(UniParcEntry protein, String identifier){

        return protein.getUniParcId().getValue().equals(identifier);
    }

    /**
     *
     * @param uniprot : the uniprot accession
     * @param taxId : the taxId
     * @return  true if the uniprot entry with this accession matches the taxId
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    private boolean hasTheAppropriateOrganism(String uniprot, String taxId) throws ActionProcessingException {

        if (taxId != null){
            Collection<UniprotProtein> proteins = uniprotService.retrieve(uniprot);

            if (proteins.size() != 1){
                throw new ActionProcessingException("The uniprot accession " + uniprot + " could match several uniprot entries.");
            }
            else if (proteins.isEmpty()){
                throw new ActionProcessingException("The uniprot accession " + uniprot + " couldn't match any uniprot entries.");
            }
            else {
                UniprotProtein prot = proteins.iterator().next();

                if (prot.getOrganism() != null){
                    String tax = Integer.toString(prot.getOrganism().getTaxid());
                    if (taxId.equalsIgnoreCase(tax)){
                        return true;
                    }
                }
            }
        }
        else {
            return true;
        }

        return false;
    }

    /**
     * Process the results of the query. If only one entry is matching, the query was successful, if several entries were matching,
     * the results should be reviewed by a curator
     * @param iterator : the results
     * @param report : the current report
     * @param context : the context of the protein
     * @return an unique uniprot accession if successful, null otherwise
     */
    private String processIterator(EntryIterator<UniProtEntry> iterator, MappingReport report, IdentificationContext context){
        // we have only one entry
        if (iterator.getResultSize() == 1){
            UniProtEntry protein = iterator.next();
            String id = protein.getPrimaryUniProtAccession().getValue();

            Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id );
            report.setStatus(status);
            return id;
        }
        // we have several matching entries
        else if (iterator.getResultSize() > 1){

            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + iterator.getResultSize() + " Uniprot entries.");
            report.setStatus(status);
            report.setIsASwissprotEntry(false);
        }
        return null;
    }

    /**
     * Process the results of the query. If only one entry is matching, the query was successful, if several entries were matching,
     * the results should be reviewed by a curator
     * @param iterator : the results
     * @param report : the current report
     * @param context : the context of the protein
     * @return an unique uniprot accession if successful, null otherwise
     */
    /*private String processQuery(EntryIterator<UniProtEntry> iterator, ActionReport report, IdentificationContext context){
        // we have only one entry
        if (iterator.getResultSize() == 1){
            UniProtEntry protein = iterator.next();
            Set<DatabaseType> databaseNames = convertMINumberInUniprot(context.getDatabaseForIdentifier());
            String id = protein.getPrimaryUniProtAccession().getValue();

            // The identifier matches an uniprot accession of identifier
            if (hasTheExactIdentifierInUniprot(protein, context.getIdentifier())){
                Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id );
                report.setStatus(status);
                return id;
            }
            // the identidier doesn't match any uniprot accessions or identifiers
            else {
                // get the database name in uniprot for what the identifier is matching
                String databaseInUniprot = getTheDatabaseOfExactIdentifierInUniprotCrossReferences(protein, context.getIdentifier(), context.getDatabaseForIdentifier());

                // if the exact identifier is not in the cross references of the uniprot entry
                if (databaseInUniprot == null){
                    Status status = new Status(StatusLabel.FAILED, "The identifier " + context.getIdentifier() + " couldn't exactly match a uniprot/uniparc cross reference." );
                    report.setIsASwissprotEntry(false);
                    report.setStatus(status);
                }
                // the exact identifier is present in the cross references and we got the database name
                else {
                    // We don't have a database name or MI for the identifier in the context, we can't decide if the matching cross reference is valid
                    if (context.getDatabaseForIdentifier() == null){
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " can match the cross references of the protein "+ id +" : "+databaseInUniprot+"="+id+". However, as we didn't have" +
                                " a database name for this identifier in context, we can't be sure that we could match the proper cross reference in uniprot." );
                        report.setStatus(status);
                        report.addPossibleAccession(id);
                    }
                    else {
                        for (DatabaseType name : databaseNames){
                            // the database name is matching the one in the context
                            if (name.toString().equalsIgnoreCase(databaseInUniprot)){
                                Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " from the database "+context.getDatabaseForIdentifier()+" has successfully been identified as " + id );
                                report.setStatus(status);
                                return id;
                            }
                        }

                        // The database in the context was not matching the database of the matching identifier in the cross references of the protein, we need a curator
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " can match the cross references of the protein "+ id +" : "+databaseInUniprot+"="+context.getIdentifier()+". However, the database name of the identifier " +
                                " in the context is "+context.getDatabaseForIdentifier()+", we can't be sure that we could match the proper cross reference in uniprot." );
                        report.setStatus(status);
                        report.addPossibleAccession(id);
                    }
                }
            }
        }
        // we have several matching entries
        else if (iterator.getResultSize() > 1){

            for (UniProtEntry u : iterator){
                if (hasTheExactIdentifierInUniprot(u, context.getIdentifier())){
                    report.addPossibleAccession(u.getPrimaryUniProtAccession().getValue());
                }
            }

            if (report.getPossibleAccessions().isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "The identifier " + context.getIdentifier() + " couldn't exactly match any uniprot cross references." );
                report.setIsASwissprotEntry(false);
                report.setStatus(status);
            }
            else if (report.getPossibleAccessions().size() == 1){
                String id = report.getPossibleAccessions().iterator().next();
                Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id );
                report.setStatus(status);
                return id;
            }
            else {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + iterator.getResultSize() + " Uniprot entries.");
                report.setStatus(status);
                report.setIsASwissprotEntry(false);
            }
        }
        return null;
    }*/

    /**
     * process the Uniparc entry to extract its uniprot cross references if any
     * @param entry : the uniparc entry
     * @param taxId : the taxId of the protein we want to retrieve
     * @param setOfSwissprotAccessions : the set of Swissprot cross references in the uniparc results
     * @param setOfUniprotAccessions : the set of Trembl cross references in the uniparc results
     * @throws ActionProcessingException
     */
    private void processUniparcResult(UniParcEntry entry, String taxId, Set<String> setOfSwissprotAccessions, Set<String> setOfUniprotAccessions) throws ActionProcessingException {
        // get the uniparc cross references
        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> uniprotRefs = entry.getUniProtDatabaseCrossReferences();
        if (!uniprotRefs.isEmpty()){
            for (uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference uniprotRef : uniprotRefs){
                // if the taxId is matching the uniprot cross references
                if (hasTheAppropriateOrganism(uniprotRef.getAccession(), taxId)){

                    String databaseName = uniprotRef.getDatabase().getName();
                    // we have a swissprot entry
                    if (swissprot.equalsIgnoreCase(databaseName) || swissprot_sv.equalsIgnoreCase(databaseName)){
                        setOfSwissprotAccessions.add(uniprotRef.getAccession());
                    }
                    // we have a trembl entry
                    else {
                        setOfUniprotAccessions.add(uniprotRef.getAccession());
                    }
                }
            }
        }
    }

    /**
     * Query uniprot to get the swissprot entries with a specific cross reference or identifier and a specific organism. If the query is not successful in Swissprot,
     * Query uniprot without a filter on Swissprot.
     * @param context  : the context of the protein
     * @return a unique uniprot accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    /*public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        String identifier = context.getIdentifier();
        String taxId = null;
        String queryString = addFilterOnSwissprot(buildUniprotDatabaseCrossReferenceQuery(identifier));

        // Create a new report
        ActionReport report = new ActionReport(ActionName.SEARCH_Swissprot_CrossReference);
        this.listOfReports.add(report);

        // if the organism is not null, we can add a filter on the organism
        if (context.getOrganism() != null){
            taxId = context.getOrganism().getTaxId();
            queryString = addTaxIdToQuery(queryString, taxId);
        }
        else {
            report.addWarning("No organism was given for the protein with : identifier =  " + identifier  + ". We will process the identification without looking at the organism.");
        }

        // get the results of the query on swissprot
        EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

        // if the query on Swissprot was not successful
        if (iterator == null || iterator.getResultSize() == 0){
            Status status = new Status(StatusLabel.FAILED, "There is no Swissprot entry matching the identifier " + identifier  + (taxId != null ? " and the taxId " + taxId : ""));
            report.setStatus(status);

            // new query, new report
            ActionReport report2 = new ActionReport(ActionName.SEARCH_Uniprot_CrossReference);
            this.listOfReports.add(report2);

            // build query
            queryString = buildUniprotDatabaseCrossReferenceQuery(identifier);
            if (taxId != null){
                queryString = addTaxIdToQuery(queryString, taxId);
            }

            // get the results of the query on Uniprot
            iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

            // If the query was not successful in Uniprot, we had a status FAILED to the report
            if (iterator == null || iterator.getResultSize() == 0){
                Status status2 = new Status(StatusLabel.FAILED, "There is no Uniprot entry matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                report2.setStatus(status2);
            }
            // query successful : process the results
            else {
                String ac = processQuery(iterator, report2, context);
                if (ac != null){
                    return ac;
                }
            }
        }
        else {
            // the results are not null on swissprot, we process them
            report.setIsASwissprotEntry(true);
            String ac = processQuery(iterator, report, context);
            if (ac != null){
                return ac;
            }
        }

        // we couldn't find a result in uniprot
        if ( report.getPossibleAccessions().isEmpty()){

            // create a new report
            ActionReport reportUniparc = new ActionReport(ActionName.SEARCH_Uniparc_CrossReference);
            this.listOfReports.add(reportUniparc);

            // build query for uniparc
            queryString = identifier;
            if (taxId != null){
                queryString = addTaxIdToQuery(queryString, taxId);
            }
            // get the results of the query on uniparc
            EntryIterator<UniParcEntry> iteratorUniparc = uniParcQueryService.getEntryIterator(UniParcQueryBuilder.buildFullTextSearch(queryString));

            // We don't have any results in Uniparc
            if (iteratorUniparc == null || iteratorUniparc.getResultSize() == 0){
                Status statusUniparc = new Status(StatusLabel.FAILED, "There is no cross reference in Uniparc matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                reportUniparc.setStatus(statusUniparc);
            }
            else {
                // list of Trembl entries in the uniparc entry
                HashSet<String> setOfUniprotAccessions = new HashSet<String>();
                // list of Swissprot entries in the uniparc entry
                HashSet<String> setOfSwissprotAccessions = new HashSet<String>();

                for (UniParcEntry entry : iteratorUniparc){

                    // If the identifier can exactly match a uniparc accession
                    if (hasTheExactIdentifierInUniparc(entry, context.getIdentifier())){
                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                    }
                    // the identidier doesn't match any uniparc accessions or identifiers
                    else {
                        // get the database name in uniparc for what the identifier is matching
                        String databaseInUniparc = getTheDatabaseOfExactIdentifierInUniparcCrossReferences(entry, context.getIdentifier(), context.getDatabaseForIdentifier());
                        Set<DatabaseType> databaseNames = convertMINumberInUniprot(context.getDatabaseForIdentifier());

                        // if the exact identifier is in the cross references of the uniparc entry
                        if (databaseInUniparc != null){
                            // We have a database name or MI for the identifier in the context
                            if (context.getDatabaseForIdentifier() != null){
                                for (DatabaseType name : databaseNames){
                                    // the database name is matching the one in the context
                                    if (name.toString().equalsIgnoreCase(databaseInUniparc)){
                                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                                    }
                                }
                            }
                        }
                    }
                }

                // No results
                if (setOfUniprotAccessions.isEmpty() && setOfSwissprotAccessions.isEmpty()){
                    Status statusUniparc = new Status(StatusLabel.FAILED, "There is no uniprot entry we could find in Uniparc associated with the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                    reportUniparc.setStatus(statusUniparc);
                }
                // one swissprot entry
                else if (setOfSwissprotAccessions.size() == 1){
                    String id = setOfSwissprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(true);

                    return id;
                }
                // several swissprot entries
                else if (setOfSwissprotAccessions.size() > 1){
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfSwissprotAccessions.size() + " Swissprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfSwissprotAccessions);
                }
                // one trembl entry
                else if (setOfUniprotAccessions.size() == 1){
                    String id = setOfUniprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(false);

                    return id;
                }
                // several trembl entries
                else {
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfUniprotAccessions.size() + " Uniprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfUniprotAccessions);
                }
            }
        }

        return null;
    }*/

    /**
     * Query uniprot to get the swissprot entries with a specific cross reference or identifier and a specific organism. If the query is not successful in Swissprot,
     * Query uniprot without a filter on Swissprot.
     * @param context  : the context of the protein
     * @return a unique uniprot accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        String identifier = context.getIdentifier();
        Set<DatabaseType> databaseTypes = convertMINumberInUniprot(context.getDatabaseForIdentifier());

        String taxId = null;

        // get the results of the query on swissprot
        EntryIterator<UniProtEntry> iteratorSwissprot = getReviewedUniprotDatabaseCrossReferenceIterator(databaseTypes, identifier);

        // Create a new report
        MappingReport report = getReportsFactory().getMappingReport(ActionName.SEARCH_Swissprot_CrossReference);
        this.listOfReports.add(report);

        // if the organism is not null, we can add a filter on the organism
        if (context.getOrganism() != null){
            taxId = context.getOrganism().getTaxId();
            iteratorSwissprot = addTaxIdToUniprotIterator(iteratorSwissprot, taxId);
        }
        else {
            report.addWarning("No organism was given for the protein with : identifier =  " + identifier  + ". We will process the identification without looking at the organism.");
        }

        // if the query on Swissprot was not successful
        if (iteratorSwissprot == null || iteratorSwissprot.getResultSize() == 0){
            Status status = new Status(StatusLabel.FAILED, "There is no Swissprot entry matching the identifier " + identifier  + (taxId != null ? " and the taxId " + taxId : ""));
            report.setStatus(status);

            // new query, new report
            MappingReport report2 = getReportsFactory().getMappingReport(ActionName.SEARCH_Uniprot_CrossReference);
            this.listOfReports.add(report2);

            // get the results of the query on Trembl
            EntryIterator<UniProtEntry> iteratorTrembl = getUnreviewedUniprotDatabaseCrossReferenceIterator(databaseTypes, identifier);

            if (taxId != null){
                iteratorTrembl = addTaxIdToUniprotIterator(iteratorTrembl, taxId);
            }

            // If the query was not successful in Uniprot, we had a status FAILED to the report
            if (iteratorTrembl == null || iteratorTrembl.getResultSize() == 0){
                Status status2 = new Status(StatusLabel.FAILED, "There is no Uniprot entry matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                report2.setStatus(status2);
            }
            // query successful : process the results
            else {
                String ac = processIterator(iteratorTrembl, report2, context);
                if (ac != null){
                    return ac;
                }
            }
        }
        else {
            // the results are not null on swissprot, we process them
            report.setIsASwissprotEntry(true);
            String ac = processIterator(iteratorSwissprot, report, context);
            if (ac != null){
                return ac;
            }
        }

        // we couldn't find a result in uniprot
        if ( report.getPossibleAccessions().isEmpty()){

            // create a new report
            MappingReport reportUniparc = getReportsFactory().getMappingReport(ActionName.SEARCH_Uniparc_CrossReference);
            this.listOfReports.add(reportUniparc);

            // build query for uniparc
            // get the results of the query on uniparc
            EntryIterator<UniParcEntry> iteratorUniparc = getUniparcDatabaseCrossReferenceIterator(identifier);

            if (taxId != null){
                iteratorUniparc = addTaxIdToUniparcIterator(iteratorUniparc, taxId);
            }

            // We don't have any results in Uniparc
            if (iteratorUniparc == null || iteratorUniparc.getResultSize() == 0){
                Status statusUniparc = new Status(StatusLabel.FAILED, "There is no cross reference in Uniparc matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                reportUniparc.setStatus(statusUniparc);
            }
            else {
                // list of Trembl entries in the uniparc entry
                HashSet<String> setOfUniprotAccessions = new HashSet<String>();
                // list of Swissprot entries in the uniparc entry
                HashSet<String> setOfSwissprotAccessions = new HashSet<String>();

                for (UniParcEntry entry : iteratorUniparc){

                    // If the identifier can exactly match a uniparc accession
                    if (hasTheExactIdentifierInUniparc(entry, context.getIdentifier())){
                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                    }
                    // the identidier doesn't match any uniparc accessions or identifiers
                    else {
                        // get the database name in uniparc for what the identifier is matching
                        String databaseInUniparc = getTheDatabaseOfExactIdentifierInUniparcCrossReferences(entry, context.getIdentifier(), context.getDatabaseForIdentifier());

                        // if the exact identifier is in the cross references of the uniparc entry
                        if (databaseInUniparc != null){
                            // We have a database name or MI for the identifier in the context
                            if (context.getDatabaseForIdentifier() != null){
                                for (DatabaseType name : databaseTypes){
                                    // the database name is matching the one in the context
                                    if (name.toString().equalsIgnoreCase(databaseInUniparc)){
                                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                                    }
                                }
                            }
                        }
                    }
                }

                // No results
                if (setOfUniprotAccessions.isEmpty() && setOfSwissprotAccessions.isEmpty()){
                    Status statusUniparc = new Status(StatusLabel.FAILED, "There is no uniprot entry we could find in Uniparc associated with the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                    reportUniparc.setStatus(statusUniparc);
                }
                // one swissprot entry
                else if (setOfSwissprotAccessions.size() == 1){
                    String id = setOfSwissprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(true);

                    return id;
                }
                // several swissprot entries
                else if (setOfSwissprotAccessions.size() > 1){
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfSwissprotAccessions.size() + " Swissprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfSwissprotAccessions);
                }
                // one trembl entry
                else if (setOfUniprotAccessions.size() == 1){
                    String id = setOfUniprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(false);

                    return id;
                }
                // several trembl entries
                else {
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfUniprotAccessions.size() + " Uniprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfUniprotAccessions);
                }
            }
        }

        return null;
    }

    /**
     * Add a filter on the Taxid in the initial query
     * @param initialquery : the initial query
     * @param organism  : the organism of the protein
     * @return the query as a String
     */
    protected EntryIterator<UniParcEntry> addTaxIdToUniparcIterator(EntryIterator<UniParcEntry> initialquery, String organism){
        return  uniParcQueryService.getEntryIterator(initialquery, SetOperation.And, uniParcQueryService.getEntryIterator(UniParcQueryBuilder.buildFullTextSearch(buildTaxIdQuery(organism))));
    }
}

package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.ReflectionCrossReferenceBuilder;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.UniprotCrossReference;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.util.IndexField;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.UniParcQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniParcQueryService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    private static final String swissprot = "UniProtKB/Swiss-Prot";

    /**
     * Build the query in uniprot to get the uniprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the query as a String
     */
    private String buildUniprotDatabaseCrossReferenceQuery(String identifier){
        String query = IndexField.DATABASE_CROSSREFERENCES+ ":" + identifier +
                " OR " + IndexField.UNIPROT_ID+":"+identifier +
                " OR " + IndexField.UNIPROT_IDENTIFIER+":"+identifier +
                " OR " + IndexField.PRIMARY_ACCESSION+":"+identifier;
        return query ;
    }

    /**
     * Build the query in uniparc to get the uniprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the query as a String
     */
    private String buildUniparcDatabaseCrossReferenceQuery(String identifier){
        String query = IndexField.DATABASE_CROSSREFERENCES+ ":" + identifier +
                " OR " + IndexField.UNIPARC_ACCESSION+":"+identifier +
                " OR " + IndexField.UNIPARC_ACTIVE_CROSSREFERENCES+":"+identifier +
                " OR " + IndexField.UNIPARC_UNIPROT_CROSSREFERENCES+":"+identifier ;
        return query ;
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact cross reference, exact accession with this identifier
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

        Collection<DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();
        ReflectionCrossReferenceBuilder builder = new ReflectionCrossReferenceBuilder();

        for ( DatabaseCrossReference ref : databaseCrossReferences ) {
            UniprotCrossReference uref = builder.build( ref );

            if (uref.getAccessionNumber().equals(identifier)){
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param protein : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact uniparc accession or exact uniparc cross reference with this identifier
     */
    private boolean hasTheExactIdentifierInUniparc(UniParcEntry protein, String identifier){

        if (protein.getUniParcId().getValue().equals(identifier)){
            return true;
        }

        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();

        for ( uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference ref : databaseCrossReferences ) {

            if (ref.getAccession().equals(identifier)){
                return true;
            }
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
    private String processQuery(EntryIterator<UniProtEntry> iterator, ActionReport report, IdentificationContext context){
        // we have only one entry
        if (iterator.getResultSize() == 1){
            UniProtEntry protein = iterator.next();
            if (hasTheExactIdentifierInUniprot(protein, context.getIdentifier())){
                String id = protein.getPrimaryUniProtAccession().getValue();
                Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id );
                report.setStatus(status);
                return id;
            }
            else {
                Status status = new Status(StatusLabel.FAILED, "The identifier " + context.getIdentifier() + " couldn't exactly match a uniprot/uniparc cross reference." );
                report.setIsASwissprotEntry(false);
                report.setStatus(status);
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
                Status status = new Status(StatusLabel.FAILED, "The identifier " + context.getIdentifier() + " couldn't exactly match a uniprot/uniparc cross reference." );
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
    }

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
            Status status = new Status(StatusLabel.FAILED, "There is no Swissprot entry matching the identifier " + identifier);
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
                Status status2 = new Status(StatusLabel.FAILED, "There is no Uniprot entry matching the identifier " + identifier);
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

        ActionReport lastReport = this.listOfReports.get(this.listOfReports.size() - 1);

        // we couldn't find a result in uniprot
        if ( report.getPossibleAccessions().isEmpty()){

            // create a new report
            ActionReport reportUniparc = new ActionReport(ActionName.SEARCH_Uniparc_CrossReference);
            this.listOfReports.add(reportUniparc);

            // build query for uniparc
            queryString = buildUniparcDatabaseCrossReferenceQuery(identifier);
            if (taxId != null){
                queryString = addTaxIdToQuery(queryString, taxId);
            }
            // get the results of the query on uniparc
            EntryIterator<UniParcEntry> iteratorUniparc = uniParcQueryService.getEntryIterator(UniParcQueryBuilder.buildFullTextSearch(context.getIdentifier()));

            if (iteratorUniparc == null || iteratorUniparc.getResultSize() == 0){
                Status statusUniparc = new Status(StatusLabel.FAILED, "There is no cross reference in Uniparc matching the identifier " + identifier);
                reportUniparc.setStatus(statusUniparc);
            }
            else {
                HashSet<String> setOfUniprotAccessions = new HashSet<String>();
                HashSet<String> setOfSwissprotAccessions = new HashSet<String>();

                for (UniParcEntry entry : iteratorUniparc){
                    if (hasTheExactIdentifierInUniparc(entry, context.getIdentifier())){
                        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> uniprotRefs = entry.getUniProtDatabaseCrossReferences();
                        if (!uniprotRefs.isEmpty()){
                            for (uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference uniprotRef : uniprotRefs){
                                if (uniprotRef.getDatabase().getName().equalsIgnoreCase(swissprot)){
                                    setOfSwissprotAccessions.add(uniprotRef.getAccession());
                                }
                                else {
                                    setOfUniprotAccessions.add(uniprotRef.getAccession());
                                }
                            }
                        }
                    }
                }

                if (setOfUniprotAccessions.isEmpty() && setOfSwissprotAccessions.isEmpty()){
                    Status statusUniparc = new Status(StatusLabel.FAILED, "There is no uniprot entry we could find in Uniparc associated with the identifier " + identifier);
                    reportUniparc.setStatus(statusUniparc);
                }
                else if (setOfSwissprotAccessions.size() == 1){
                    String id = setOfSwissprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(true);

                    return id;
                }
                else if (setOfSwissprotAccessions.size() > 1){
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfSwissprotAccessions.size() + " Swissprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfUniprotAccessions);
                }
                else if (setOfUniprotAccessions.size() == 1){
                    String id = setOfUniprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(false);

                    return id;
                }
                else {
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfSwissprotAccessions.size() + " Uniprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfUniprotAccessions);
                }
            }
        }

        return null;
    }

}

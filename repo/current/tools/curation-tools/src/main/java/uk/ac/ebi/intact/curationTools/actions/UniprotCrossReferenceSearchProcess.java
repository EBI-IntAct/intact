package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.util.IndexField;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;

/**
 * This class is querying uniprot to retrieve cross references and identifiers of an uniprot entry.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public class UniprotCrossReferenceSearchProcess extends ActionNeedingUniprotService{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotCrossReferenceSearchProcess.class );

    /**
     * Build the query in uniprot to get the uniprot entries with this identifier in their cross references
     * @param identifier : the identifier of the protein we want to identify
     * @return the query as a String
     */
    private String buildDatabaseCrossReferenceQuery(String identifier){
        String query = IndexField.DATABASE_CROSSREFERENCES+ ":" + identifier +
                " OR " + IndexField.UNIPARC_ACCESSION+":"+identifier +
                " OR " + IndexField.UNIPARC_ACTIVE_CROSSREFERENCES+":"+identifier +
                " OR " + IndexField.UNIPARC_UNIPROT_CROSSREFERENCES+":"+identifier +
                " OR " + IndexField.UNIPROT_ID+":"+identifier +
                " OR " + IndexField.UNIPROT_IDENTIFIER+":"+identifier +
                " OR " + IndexField.PRIMARY_ACCESSION+":"+identifier +
                " OR " + IndexField.UNIMES_ACCESSION+":"+identifier +
                " OR " + IndexField.UNIMES_PEPTIDE_ID+":"+identifier +
                " OR " + IndexField.UNIREF_ACCESSION+":"+identifier;
        return query ;
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
            String id = iterator.next().getPrimaryUniProtAccession().getValue();
            Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id );
            report.setStatus(status);
            return id;
        }
        // we have several matching entries
        else if (iterator.getResultSize() > 1){
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + iterator.getResultSize() + " Uniprot entries.");
            report.setStatus(status);
            report.setIsASwissprotEntry(false);
            for (UniProtEntry u : iterator){
                report.addPossibleAccession(u.getPrimaryUniProtAccession().getValue());
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
        String queryString = addFilterOnSwissprot(buildDatabaseCrossReferenceQuery(identifier));

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
            queryString = buildDatabaseCrossReferenceQuery(identifier);
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
                return processQuery(iterator, report2, context);
            }
        }
        else {
            // the results are not null on swissprot, we process them
            report.setIsASwissprotEntry(true);
            return processQuery(iterator, report, context);
        }

        return null;
    }

}

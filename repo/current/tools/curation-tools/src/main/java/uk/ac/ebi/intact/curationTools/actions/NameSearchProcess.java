package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.actions.exception.PicrSearchWithSequenceException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.TaxIdContext;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.util.IndexField;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class NameSearchProcess implements IdentificationAction{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( NameSearchProcess.class );
    private TaxIdContext context;
    private List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    private UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();

    private String buildGeneNameQuery(String gene_name){
        String query = IndexField.GENE_ANNOTATION + ":" + gene_name +
                " OR " +
                IndexField.GENE_EXACT + ":" + gene_name +
                " OR " +
                IndexField.GENE_NAME + ":" + gene_name +
                " OR " +
                IndexField.GENE_NAME_EXACT + ":" + gene_name +
                " OR " +
                IndexField.GENE_ORDERED_LOCUS + ":" + gene_name +
                " OR " +
                IndexField.GENE_ORF + ":" + gene_name +
                " OR " +
                IndexField.GENE_SYNONYM + ":" + gene_name +
                " OR " +
                IndexField.GENE_SYNONYM_EXACT + ":" + gene_name;
        return query ;
    }

    private String buildProteinNameQuery(String protein_name){
        String query = IndexField.NAME_ANNOTATION + ":" + protein_name +
                " OR " +
                IndexField.PROTEIN_NAME + ":" + protein_name +
                " OR " +
                IndexField.REC_NAME + ":" + protein_name +
                " OR " +
                IndexField.ALT_NAME + ":" + protein_name;
        return query ;
    }

    private String buildOrganismQuery(String organismName){
        String query = IndexField.NCBI_TAXON_ID + ":" + organismName +
                " OR " +
                IndexField.ORGANISM_COMMON + ":" + organismName +
                " OR " +
                IndexField.ORGANISM_HOST + ":" + organismName +
                " OR " +
                IndexField.ORGANISM_NAMES + ":" + organismName +
                " OR " +
                IndexField.ORGANISM_SCIENTIFIC + ":" + organismName +
                " OR " +
                IndexField.ORGANISM_SYNONYM + ":" + organismName;
        return query ;
    }

    private String buildTaxIdQuery(String organismName){
        String query = IndexField.NCBI_TAXON_ID + ":" + organismName;
        return query ;
    }

    private String buildGenenameAndProteinNameQuery(String gene_name, String protein_name){
        String query = "(" + buildGeneNameQuery(gene_name) + ")" + " AND " + "(" + buildProteinNameQuery(protein_name) + ")";

        return query;
    }

    private String buildGenenameOrProteinNameQuery(String gene_name, String protein_name){
        String query = "(" + buildGeneNameQuery(gene_name) + ")" + " OR " + "(" + buildProteinNameQuery(protein_name) + ")";

        return query;
    }

    private String addOrganismToQuery(String initialquery, String organism){
        String query = "(" + initialquery + ")" + " AND " + "(" + buildOrganismQuery(organism) + ")";

        return query;
    }

    private String addTaxIdToQuery(String initialquery, String organism){
        String query = "(" + initialquery + ")" + " AND " + "(" + buildOrganismQuery(organism) + ")";

        return query;
    }

    private String addFilterOnSwissprot(String initialquery){
        String query = "(" + initialquery + ")" + " AND " + "(" + IndexField.REVIEWED + ")";

        return query;
    }

    private Query getQueryFor(String query){
        return UniProtQueryBuilder.buildQuery(query);
    }

    private Query getQueryForFullText(String name, String taxId){
        if (taxId != null){
            return UniProtQueryBuilder.buildFullTextSearch(addTaxIdToQuery(name, taxId));
        }
        return UniProtQueryBuilder.buildFullTextSearch(name);
    }

    private String processQuery(EntryIterator<UniProtEntry> iterator, ActionReport report, IdentificationContext context){
        if (iterator.getResultSize() == 1){
            String id = iterator.next().getPrimaryUniProtAccession().getValue();
            Status status = new Status(StatusLabel.COMPLETED, "The protein " + (context.getGene_name() != null ? "with the gene name " + context.getGene_name() : (context.getProtein_name() != null ? "with the protein name " + context.getProtein_name() : "")) + " has successfully been identified as " + id );
            report.setStatus(status);

            return id;
        }
        else if (iterator.getResultSize() > 1){
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein " + (context.getGene_name() != null ? "with the gene name " + context.getGene_name() : (context.getProtein_name() != null ? "with the protein name " + context.getProtein_name() : "")) + " could match " + iterator.getResultSize() + " Uniprot entries.");
            report.setStatus(status);
            for (UniProtEntry u : iterator){
                report.addPossibleAccession(u.getPrimaryUniProtAccession().getValue());
            }
        }
        return null;
    }

    public EntryIterator<UniProtEntry> querySwissprotWithGeneNameOrProteinName(String geneName, String proteinName, String globalName, String organsim){
        String queryString = null;

        if (proteinName != null && geneName != null){
            queryString = addFilterOnSwissprot(buildGenenameOrProteinNameQuery(geneName, proteinName));

            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }

            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

            return iterator;
        }
        else if (globalName != null){
            queryString = addFilterOnSwissprot(buildGenenameOrProteinNameQuery(globalName, globalName));

            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }

            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

            return iterator;
        }
        return null;
    }

    public EntryIterator<UniProtEntry> queryUniprotWithGeneNameOrProteinName(String geneName, String proteinName, String globalName, String organsim){
        String queryString = null;

        if (proteinName != null && geneName != null){
            queryString = buildGenenameOrProteinNameQuery(geneName, proteinName);

            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }

            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

            return iterator;
        }
        else if (globalName != null){
            queryString = buildGenenameOrProteinNameQuery(globalName, globalName);

            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }

            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));

            return iterator;
        }
        return null;
    }

    public EntryIterator<UniProtEntry> querySwissprotWith(String geneName, String proteinName, String organsim){
        String queryString = null;

        if (geneName != null && proteinName != null){
            queryString = addFilterOnSwissprot(buildGenenameAndProteinNameQuery(geneName, proteinName));

        }
        else if (geneName != null){
            queryString = addFilterOnSwissprot(buildGeneNameQuery(geneName));
        }
        else if (proteinName != null){
            queryString = addFilterOnSwissprot(buildProteinNameQuery(proteinName));
        }
        else {
            log.error("Either the gene name or the protein name should be not null.");
        }

        if (queryString != null){
            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }
            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));
            if (proteinName != null && geneName != null && (iterator.getResultSize() == 0) || iterator == null){

                iterator = querySwissprotWithGeneNameOrProteinName(geneName, proteinName, null, organsim);
            }
            return iterator;
        }
        return null;
    }

    public EntryIterator<UniProtEntry> queryUniprotWith(String geneName, String proteinName, String organsim){
        String queryString = null;

        if (geneName != null && proteinName != null){
            queryString = buildGenenameAndProteinNameQuery(geneName, proteinName);
        }
        else if (geneName != null){
            queryString = buildGeneNameQuery(geneName);
        }
        else if (proteinName != null){
            queryString = buildProteinNameQuery(proteinName);
        }
        else {
            log.error("Either the gene name or the protein name should be not null.");
        }

        if (queryString != null){
            if (organsim != null){
                queryString = addTaxIdToQuery(queryString, organsim);
            }
            EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(getQueryFor(queryString));
            if (proteinName != null && geneName != null && (iterator.getResultSize() == 0) || iterator == null){
                iterator = queryUniprotWithGeneNameOrProteinName(geneName, proteinName, null, organsim);
            }
            return iterator;
        }
        return null;
    }

    private String processNameSearch(String geneName, String protein_name, String organism, String globalName, ActionReport report){
        if (geneName != null || protein_name != null){

            EntryIterator<UniProtEntry> iterator = querySwissprotWith(geneName, protein_name, organism);

            if (iterator == null || iterator.getResultSize() == 0){
                iterator = queryUniprotWith(geneName, protein_name, organism);

                if (iterator == null || iterator.getResultSize() == 0){
                    Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches : gene name = " + geneName + "; protein name = " + protein_name + "; TaxId = " + organism);
                    report.setStatus(status);
                }
                else {
                    return processQuery(iterator, report, context);
                }
            }
            else {
                return processQuery(iterator, report, context);
            }
        }
        else if (globalName != null){
            EntryIterator<UniProtEntry> iterator = querySwissprotWithGeneNameOrProteinName(null, null, globalName, organism);

            if (iterator == null || iterator.getResultSize() == 0){
                report.addWarning("We couldn't find any Swissprot entry which matches : name = " + globalName + "; TaxId = " + organism + ". We will look in Trembl.");

                iterator = queryUniprotWithGeneNameOrProteinName(null, null, globalName, organism);

                if (iterator == null || iterator.getResultSize() == 0){
                    Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches : name = " + globalName + "; TaxId = " + organism);
                    report.setStatus(status);
                }
                else {
                    return processQuery(iterator, report, context);
                }
            }
            else {
                return processQuery(iterator, report, context);
            }
        }
        else {
            log.error("We don't have any name to query for.");
        }
        return null;
    }

    private void processGlobalQuery(ActionReport report, Query query){
        EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(query);

        if (iterator != null && iterator.getResultSize() != 0){
            for (UniProtEntry e : iterator){
                report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
            }
        }
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();
        
        if (!(context instanceof TaxIdContext)){
            log.error("The NameSearchProcess needs a TaxIdContext instance and the current context is a " + context.getClass().getSimpleName());
        }
        else {
            this.context = (TaxIdContext) context;

            String geneName = this.context.getGene_name();
            String protein_name = this.context.getProtein_name();
            String organism = this.context.getDeducedTaxId();
            String globalName = this.context.getGlobalName();

            ActionReport report = new ActionReport(ActionName.SEARCH_uniprot);
            this.listOfReports.add(report);

            if (organism == null && this.context.getOrganism() != null){
                if (this.context.getOrganism().length() > 0){
                    throw  new PicrSearchWithSequenceException("We couldn't find the TaxId of the organism " + this.context.getOrganism() + ". Could you check that you gave either a valid TaxId or a Biosource shortlabel.");
                }
                else {
                    report.addWarning("No organism was given for the protein with : name =  " + this.context.getGlobalName() != null ? this.context.getGlobalName() : (this.context.getGene_name()!= null ? this.context.getGene_name() : (this.context.getProtein_name() != null ? this.context.getProtein_name() : "")) + ". We will process the identification without looking at the organism and choose the entry with the longest sequence.");
                }
            }

            String accession = processNameSearch(geneName, protein_name, organism, globalName, report);

            if (accession != null){
                return accession;
            }

            if (accession == null && report.getPossibleAccessions().isEmpty()){

                ActionReport report2 = new ActionReport(ActionName.wide_SEARCH_uniprot);
                this.listOfReports.add(report2);

                if (geneName != null){
                    Query query = getQueryForFullText(geneName, organism);
                    processGlobalQuery(report2, query);
                }
                if (protein_name != null){
                    Query query2 = getQueryForFullText(protein_name, organism);
                    processGlobalQuery(report2, query2);
                }
                if (globalName != null){
                    Query query3 = getQueryForFullText(globalName, organism);
                    processGlobalQuery(report2, query3);
                }

                if (report2.getPossibleAccessions().isEmpty()){
                    Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches the name even after we had widened the search.");
                    report2.setStatus(status);
                }
            }
        }
        return null;
    }

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }
}

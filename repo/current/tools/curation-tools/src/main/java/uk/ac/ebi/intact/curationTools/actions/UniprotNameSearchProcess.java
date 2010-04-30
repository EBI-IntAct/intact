package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.kraken.util.IndexField;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class UniprotNameSearchProcess extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotNameSearchProcess.class );

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
        String query = IndexField.PROTEIN_NAME + ":" + protein_name +
                " OR " +
                IndexField.REC_NAME + ":" + protein_name +
                " OR " +
                IndexField.ALT_NAME + ":" + protein_name;
        return query ;
    }

   /* private String buildOrganismQuery(String organismName){
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
    }*/

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

    /*private String addOrganismToQuery(String initialquery, String organism){
        String query = "(" + initialquery + ")" + " AND " + "(" + buildOrganismdQuery(organism) + ")";

        return query;
    }*/

    private String addTaxIdToQuery(String initialquery, String organism){
        String query = "(" + initialquery + ")" + " AND " + "(" + buildTaxIdQuery(organism) + ")";

        return query;
    }

    private String addFilterOnSwissprot(String initialquery){
        String query = "(" + initialquery + ")" + " AND " + "(" + IndexField.REVIEWED + ")";

        return query;
    }

    private Query getQueryFor(String query){
        return UniProtQueryBuilder.buildQuery(query);
    }

    private Query getQueryFor(String name, String taxId){
        if (taxId != null){
            return UniProtQueryBuilder.buildQuery(addTaxIdToQuery(name, taxId));
        }
        return UniProtQueryBuilder.buildQuery(name);
    }

    private boolean hasTheExactGeneName(UniProtEntry protein, String geneName){
        if (protein.getGenes().isEmpty()){
            return false;
        }
        else {
            for (Gene gene : protein.getGenes()){
                if (geneName.equalsIgnoreCase(gene.getGeneName().getValue())){
                    return true;
                }
                if (!gene.getGeneNameSynonyms().isEmpty()){
                    for (GeneNameSynonym synonym : gene.getGeneNameSynonyms()){
                        if (geneName.equalsIgnoreCase(synonym.getValue())){
                            return true;
                        }
                    }
                }
                if (!gene.getOrderedLocusNames().isEmpty()){
                    for (OrderedLocusName orderedLocusName : gene.getOrderedLocusNames()){
                        if (geneName.equalsIgnoreCase(orderedLocusName.getValue())){
                            return true;
                        }
                    }
                }
                if (!gene.getORFNames().isEmpty()){
                    for (ORFName orfName : gene.getORFNames()){
                        if (geneName.equalsIgnoreCase(orfName.getValue())){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    private String processGeneQuery(EntryIterator<UniProtEntry> iterator, ActionReport report, IdentificationContext context){
        if (iterator.getResultSize() == 1){
            String id = iterator.next().getPrimaryUniProtAccession().getValue();
            Status status = new Status(StatusLabel.COMPLETED, "The protein with the gene name " + context.getGene_name() + " has successfully been identified as " + id );
            report.setStatus(status);

            return id;
        }
        else if (iterator.getResultSize() > 1){

            for (UniProtEntry u : iterator){
                if (hasTheExactGeneName(u, context.getGene_name())){
                    report.addPossibleAccession(u.getPrimaryUniProtAccession().getValue());
                }
            }

            if (report.getPossibleAccessions().isEmpty()){
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the gene name " + context.getGene_name() + " could match " + iterator.getResultSize() + " Uniprot entries.");
                report.setStatus(status);

                for (UniProtEntry u : iterator){
                    report.addPossibleAccession(u.getPrimaryUniProtAccession().getValue());
                }
            }
            else if (report.getPossibleAccessions().size() == 1){
                String ac = report.getPossibleAccessions().iterator().next();
                Status status = new Status(StatusLabel.COMPLETED, "The protein with the exact gene name " + context.getGene_name() + " has successfully been identified as " + ac );
                report.setStatus(status);

                return ac;
            }
            else {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the gene name " + context.getGene_name() + " could match " + iterator.getResultSize() + " Uniprot entries.");
                report.setStatus(status);
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
            return iterator;
        }
        return null;
    }

    private String processNameSearch(String geneName, String protein_name, String organism, String globalName, ActionReport report, IdentificationContext context){
        if (geneName != null || protein_name != null){

            EntryIterator<UniProtEntry> iterator = querySwissprotWith(geneName, protein_name, organism);

            if (iterator == null || iterator.getResultSize() == 0){
                iterator = queryUniprotWith(geneName, protein_name, organism);

                if (iterator == null || iterator.getResultSize() == 0){
                    Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches : gene name = " + geneName + "; protein name = " + protein_name + "; TaxId = " + organism);
                    report.setStatus(status);
                }
                else{
                    return processQuery(iterator, report, context);
                }
            }
            else if (iterator.getResultSize() > 1 && geneName != null) {
                return processGeneQuery(iterator, report, context);
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
        System.out.println(query.toString());
        if (iterator != null && iterator.getResultSize() != 0){
            for (UniProtEntry e : iterator){
                report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
            }
        }
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        String geneName = context.getGene_name();
        String protein_name = context.getProtein_name();
        String organism = null;
        if (context.getOrganism() != null){
            organism = context.getOrganism().getTaxId();
        }

        String globalName = context.getGlobalName();

        ActionReport report = new ActionReport(ActionName.SEARCH_uniprot_name);
        this.listOfReports.add(report);

        if (organism == null){

            report.addWarning("No organism was given for the protein with : name =  " + context.getGlobalName() != null ? context.getGlobalName() : (context.getGene_name()!= null ? context.getGene_name() : (context.getProtein_name() != null ? context.getProtein_name() : "")) + ". We will process the identification without looking at the organism.");
        }

        String accession = processNameSearch(geneName, protein_name, organism, globalName, report, context);

        if (accession != null){
            return accession;
        }
        else if (accession == null && report.getPossibleAccessions().isEmpty()){

            ActionReport report2 = new ActionReport(ActionName.wide_SEARCH_uniprot);
            this.listOfReports.add(report2);

            if (geneName != null){
                Query query = getQueryFor(geneName, organism);
                processGlobalQuery(report2, query);
            }
            if (protein_name != null){
                Query query2 = getQueryFor(protein_name, organism);
                processGlobalQuery(report2, query2);
            }
            if (globalName != null){
                Query query3 = getQueryFor(globalName, organism);
                processGlobalQuery(report2, query3);
            }

            if (report2.getPossibleAccessions().isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches the name even after we had widened the search.");
                report2.setStatus(status);
            }
            else {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "We found "+report2.getPossibleAccessions().size()+" Uniprot entry(ies) which matche(s) the name after we had widened the search.");
                report2.setStatus(status);
            }
        }
        return null;
    }
}

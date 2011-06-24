package uk.ac.ebi.intact.protein.mapping.actions;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract class which implements IdentificationAction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public abstract class IdentificationActionImpl implements IdentificationAction {

    /**
     * List of reports
     */
    protected List<MappingReport> listOfReports = new ArrayList<MappingReport>();

    private ReportsFactory reportsFactory;

    public IdentificationActionImpl(ReportsFactory factory){
        this.reportsFactory = factory;
    }

    /**
     *
     * @return the list of reports of this object
     */
    public List<MappingReport> getListOfActionReports() {
        return this.listOfReports;
    }

    /**
     * Merge the isoforms of a same protein if the list of BlastProteins contains any.
     * @param blastProteins : the results of a BLAST
     * @return the list of accessions of the merged proteins
     */
    protected Set<String> mergeIsoformsFromBlastResults(List<BlastProtein> blastProteins){
        Set<String> isoformMerged = new HashSet<String>();
        for (BlastProtein b : blastProteins){
            if (b != null){

                if (b.getAccession() != null){
                    String primaryId;
                    if (IdentifierChecker.isSpliceVariantId(b.getAccession())){
                        primaryId = b.getAccession().substring(0, b.getAccession().indexOf("-"));
                    }
                    else {
                        primaryId = b.getAccession();
                    }
                    isoformMerged.add(primaryId);
                }
            }
        }

        return isoformMerged;
    }

    /**
     * Merge the isoforms of a same protein if the list of BlastProteins contains any.
     * @param blastProteins : the results of a BLAST
     * @return the list of accessions of the merged proteins
     */
    protected Set<String> mergeIsoformsFromBlastProteins(List<BlastResults> blastProteins){
        Set<String> isoformMerged = new HashSet<String>();
        for (BlastResults b : blastProteins){
            if (b != null){

                if (b.getAccession() != null){
                    String primaryId;
                    if (IdentifierChecker.isSpliceVariantId(b.getAccession())){
                        primaryId = b.getAccession().substring(0, b.getAccession().indexOf("-"));
                    }
                    else {
                        primaryId = b.getAccession();
                    }
                    isoformMerged.add(primaryId);
                }
            }
        }

        return isoformMerged;
    }

    /**
     * Merge the isoforms of a same protein if the list of accessions contains any.
     * @param accessions : the proteins to merge
     * @return the list of accessions of the merged proteins
     */
    protected Set<String> mergeIsoforms(List<String> accessions){
        Set<String> isoformMerged = new HashSet<String>();
        for (String b : accessions){
            if (b != null){

                if (b != null){
                    String primaryId;
                    if (IdentifierChecker.isSpliceVariantId(b)){
                        primaryId = b.substring(0, b.indexOf("-"));
                    }
                    else {
                        primaryId = b;
                    }
                    isoformMerged.add(primaryId);
                }
            }
        }

        return isoformMerged;
    }

    public ReportsFactory getReportsFactory() {
        return reportsFactory;
    }

    public void setReportsFactory(ReportsFactory reportsFactory) {
        this.reportsFactory = reportsFactory;
    }
}

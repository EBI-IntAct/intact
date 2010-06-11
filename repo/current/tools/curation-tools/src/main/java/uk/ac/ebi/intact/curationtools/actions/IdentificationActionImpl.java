package uk.ac.ebi.intact.curationtools.actions;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.curationtools.model.actionReport.ActionReport;
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
    protected List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    /**
     *
     * @return the list of reports of this object
     */
    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }

    /**
     * Merge the isoforms of a same protein if the list of BlastProteins contains any.
     * @param blastProteins : the results of a BLAST
     * @return the list of accessions of the merged proteins
     */
    protected Set<String> mergeIsoformsFromBlastProteins(List<BlastProtein> blastProteins){
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
}

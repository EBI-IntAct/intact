package uk.ac.ebi.intact.curationTools.actions;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public abstract class IdentificationActionImpl implements IdentificationAction {

    protected List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }

    protected Set<String> mergeIsoformsFromBlastProteins(ArrayList<BlastProtein> blastProteins){
        HashSet<String> isoformMerged = new HashSet<String>();
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

    protected Set<String> mergeIsoforms(ArrayList<String> accessions){
        HashSet<String> isoformMerged = new HashSet<String>();
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

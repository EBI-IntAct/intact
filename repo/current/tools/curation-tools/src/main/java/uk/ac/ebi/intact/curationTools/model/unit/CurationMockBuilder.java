package uk.ac.ebi.intact.curationTools.model.unit;

import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */

public class CurationMockBuilder {

    public BlastResults createAutomaticSwissprotRemappingResults(){
        BlastResults results = new BlastResults();

        results.setTremblAccession("Q8R3H6");
        results.setAccession("P01867");
        results.setDatabase("SP");
        results.setDescription("Ig gamma-2B chain C region");
        results.setStartQuery(140);
        results.setEndQuery(174);
        results.setStartMatch(1);
        results.setEndQuery(335);
        results.setIdentity(99);

        return results;
    }

    public BlastResults createAutomaticBlastResults(){
        BlastResults results = new BlastResults();

        results.setAccession("Q8R3H6");
        results.setDatabase("TR");
        results.setStartQuery(140);
        results.setEndQuery(174);
        results.setStartMatch(1);
        results.setEndQuery(335);
        results.setIdentity(99);

        return results;
    }

    public BlastResults createSwissprotRemappingResults(String trembl, String swissprotAc, int sartQuery, int endQuery, int startMatch, int endMatch, float identity){
        BlastResults results = new BlastResults();

        results.setTremblAccession(trembl);
        results.setAccession(swissprotAc);
        results.setDatabase("SP");
        results.setStartQuery(sartQuery);
        results.setEndQuery(endQuery);
        results.setStartMatch(startMatch);
        results.setEndQuery(endMatch);
        results.setIdentity(identity);

        return results;
    }

    public BlastReport createAutomaticSwissprotRemappingReport(){
        BlastReport report = new BlastReport(ActionName.BLAST_Swissprot_Remapping);

        report.setASwissprotEntry(true);
        report.setStatus(new Status(StatusLabel.COMPLETED, "mapping successful"));
        return report;
    }

    public PICRCrossReferences createAutomaticPICRCrossReferences(){
        PICRCrossReferences pc = new PICRCrossReferences();

        pc.setDatabase("Ensembl");
        pc.addAccession("ENSG0007777");

        return pc;
    }

    public PICRReport createAutomaticPICRReport(){
        PICRReport report = new PICRReport(ActionName.PICR_accession);

        report.setASwissprotEntry(false);
        report.setStatus(new Status(StatusLabel.COMPLETED, null));
        return report;
    }

    public ActionReport createAutomaticActionReportWithWarning(){
        ActionReport report = new ActionReport(ActionName.BLAST_uniprot);

        report.setStatus(new Status(StatusLabel.TO_BE_REVIEWED, null));
        report.setASwissprotEntry(false);
        report.addWarning("To be reviewed by a curator");
        report.addPossibleAccession("P02134");
        report.addPossibleAccession("P12345");

        return report;
    }

    public ActionReport createAutomaticActionReportWithoutWarning(){
        ActionReport report = new ActionReport(ActionName.BLAST_uniprot);

        report.setStatus(new Status(StatusLabel.TO_BE_REVIEWED, null));
        report.setASwissprotEntry(false);
        report.addPossibleAccession("P02134");
        report.addPossibleAccession("P12345");

        return report;
    }

    public ActionReport createAutomaticActionReportWithoutPossibleUniprot(){
        ActionReport report = new ActionReport(ActionName.BLAST_uniprot);

        report.setStatus(new Status(StatusLabel.TO_BE_REVIEWED, null));
        report.setASwissprotEntry(false);
        report.addWarning("To be reviewed by a curator");        

        return report;
    }

    public BlastReport createAutomaticBlastReport(){
         BlastReport report = new BlastReport(ActionName.BLAST_uniprot);

        report.setASwissprotEntry(true);
        report.setStatus(new Status(StatusLabel.COMPLETED, "mapping successful"));
        return report;
    }

    public UpdateResults createAutomaticUpdateResult(){
         UpdateResults results = new UpdateResults();

        results.setIntactAccession("EBI-0001001");
        results.setFinalUniprotId("P01234");
        return results;
    }
}

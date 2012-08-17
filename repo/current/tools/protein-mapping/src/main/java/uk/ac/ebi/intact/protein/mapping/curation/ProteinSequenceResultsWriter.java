package uk.ac.ebi.intact.protein.mapping.curation;

import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;

import java.io.*;
import java.util.Set;

/**
 * Writer of protein seuqnec results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/08/12</pre>
 */

public class ProteinSequenceResultsWriter {

    File ouptutFile;
    public final String NEW_COLUMN = "\t";
    public final String NEW_LINE = "\n";
    public final String EMPTY = "-";

    public ProteinSequenceResultsWriter(String fileName){
        if (fileName == null){
            throw new IllegalArgumentException("The file name where to write the results must be provided");
        }
        this.ouptutFile = new File(fileName);
    }

    public ProteinSequenceResultsWriter(File file){
        if (file == null){
            throw new IllegalArgumentException("The file where to write the results must be provided");
        }
        this.ouptutFile = file;
    }

    public void writeResults(FastaSequence fastaSequence, IdentificationResults<? extends MappingReport> identificationResults) throws IOException {

        if (identificationResults == null){
            throw new IllegalArgumentException("Identification results are expected");
        }

        if (fastaSequence == null){
            throw new IllegalArgumentException("A fast sequence is expected");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(ouptutFile, true));

        // PICR/swissprot remapping could identify one single uniprot id. We want to give the blast results of swissprot remapping if necessary
        if (identificationResults.hasUniqueUniprotId()){
            // fast identifier
            writer.write(fastaSequence.getIdentifier());

            // we retrieved the uniprot id from PICR
            writer.write(NEW_COLUMN);
            writer.write(identificationResults.getFinalUniprotId());

            // no other possible uniprot ids from PICR
            writer.write(NEW_COLUMN);
            writer.write(EMPTY);

            // check if last report is not blast remapping to be reviewed
            MappingReport report = identificationResults.getLastAction();
            if (report != null && report.getStatusLabel().equals(StatusLabel.TO_BE_REVIEWED) && report.getName().equals(ActionName.BLAST_Swissprot_Remapping)){
                BlastReport<BlastResults> blastReport = (BlastReport<BlastResults>) report;

                // we have one result that can match
                Set<BlastResults> blastResults = blastReport.getBlastMatchingProteins();
                if (blastResults.size() == 1){

                    BlastResults blastResult = blastResults.iterator().next();

                    writeBlastResults(writer, blastResult, fastaSequence);
                }
                // we don't write the other results
                else {

                    writeEmpytBlastResults(writer, fastaSequence);
                }
            }
            // no swissprot remapping to review
            else {
                writeEmpytBlastResults(writer, fastaSequence);
            }
        }
        else {

            // PICR sequence to be reviewed ? if yes, no blast to report
            MappingReport report = identificationResults.getLastAction();
            if (report != null && report.getStatusLabel().equals(StatusLabel.TO_BE_REVIEWED)
                    && (report.getName().equals(ActionName.PICR_sequence_Swissprot) || report.getName().equals(ActionName.PICR_sequence_Trembl))){

                // fasta identifier
                writer.write(fastaSequence.getIdentifier());

                // no unique uniprot id
                writer.write(NEW_COLUMN);
                writer.write(EMPTY);

                // possible uniprot from PICR
                writer.write(NEW_COLUMN);
                writer.write(StringUtils.join(report.getPossibleAccessions(), ", "));

                writeEmpytBlastResults(writer, fastaSequence);
            }
            // for each blast to be reviewed, we need to write a line with blast results
            else if (report != null && report.getStatusLabel().equals(StatusLabel.TO_BE_REVIEWED)
                    && report instanceof BlastReport) {
                 BlastReport<BlastResults> blastReport = (BlastReport<BlastResults>) report;

                if (blastReport.getBlastMatchingProteins().isEmpty()){
                    writeEmptyResults(fastaSequence);
                }
                else {
                    for (BlastResults blastResult : blastReport.getBlastMatchingProteins()){
                        // fasta identifier
                        writer.write(fastaSequence.getIdentifier());

                        // no unique uniprot id
                        writer.write(NEW_COLUMN);
                        writer.write(EMPTY);

                        // no other possible uniprot ids from PICR
                        writer.write(NEW_COLUMN);
                        writer.write(EMPTY);

                        writeBlastResults(writer, blastResult, fastaSequence);
                    }
                }
            }
            // no blast results
            else {
                writeEmptyResults(fastaSequence);
            }
        }

        writer.flush();

        writer.close();
    }

    private void writeBlastResults(Writer writer, BlastResults blastResult, FastaSequence fastaSequence) throws IOException {

        // candidate uniprot
        writer.write(NEW_COLUMN);
        writer.write(blastResult.getAccession());

        // write identity
        writer.write(NEW_COLUMN);
        writer.write(Float.toString(blastResult.getIdentity()));

        // write sequence coverages
        writer.write(NEW_COLUMN);
        writer.write(Float.toString(getQueryCoveragePercentFor(blastResult, fastaSequence.getSequence())));
        writer.write(NEW_COLUMN);
        writer.write(Float.toString(getMatchCoveragePercentFor(blastResult)));

        // write start/end
        writer.write(NEW_COLUMN);
        writer.write(Integer.toString(blastResult.getStartQuery()));
        writer.write("-");
        writer.write(Integer.toString(blastResult.getEndQuery()));
        writer.write(NEW_COLUMN);
        writer.write(Integer.toString(blastResult.getStartMatch()));
        writer.write("-");
        writer.write(Integer.toString(blastResult.getEndMatch()));

        // write query sequence
        writer.write(NEW_COLUMN);
        writer.write(fastaSequence.getSequence());
        // write alignment
        writer.write(NEW_COLUMN);
        writer.write(blastResult.getAlignment());
        // write matching sequence
        writer.write(NEW_COLUMN);
        writer.write(blastResult.getSequence());
        writer.write(NEW_LINE);

        writer.flush();
    }

    private void writeEmpytBlastResults(Writer writer, FastaSequence fastaSequence) throws IOException {

        // candidate uniprot
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);

        // write identity
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);

        // write sequence coverages
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);

        // write start/end
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);

        // write query sequence
        writer.write(NEW_COLUMN);
        writer.write(fastaSequence.getSequence());
        // write alignment
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        // write matching sequence
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_LINE);

        writer.flush();
    }

    public static float getQueryCoveragePercentFor(BlastResults protein, String sequence){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndQuery() - protein.getStartQuery() + 1)) / (float) sequence.length() * 100;
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the match sequence
     */
    public static float getMatchCoveragePercentFor(BlastResults protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndMatch() - protein.getStartMatch() + 1)) / (float) protein.getSequence().length() * 100;
    }

    public void writeHeader() throws IOException {
        FileWriter writer = new FileWriter(ouptutFile);

        writer.write("Fasta identifier");
        writer.write(NEW_COLUMN);
        writer.write("unique uniprot ID");
        writer.write(NEW_COLUMN);
        writer.write("Possible uniprot ids from PICR");
        writer.write(NEW_COLUMN);
        writer.write("Uniprot candidate from blast");
        writer.write(NEW_COLUMN);
        writer.write("Blast Identity");
        writer.write(NEW_COLUMN);
        writer.write("Blast query Sequence coverage");
        writer.write(NEW_COLUMN);
        writer.write("Blast match Sequence coverage");
        writer.write(NEW_COLUMN);
        writer.write("query sequence start and end");
        writer.write(NEW_COLUMN);
        writer.write("Match sequence start and end");
        writer.write(NEW_COLUMN);
        writer.write("query sequence");
        writer.write(NEW_COLUMN);
        writer.write("alignment");
        writer.write(NEW_COLUMN);
        writer.write("match sequence");
        writer.write(NEW_LINE);

        writer.flush();

        writer.close();
    }

    public void writeEmptyResults(FastaSequence fasta) throws IOException {
        FileWriter writer = new FileWriter(ouptutFile);

        writer.write(fasta.getIdentifier());
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(fasta.getSequence());
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_COLUMN);
        writer.write(EMPTY);
        writer.write(NEW_LINE);

        writer.flush();

        writer.close();
    }
}

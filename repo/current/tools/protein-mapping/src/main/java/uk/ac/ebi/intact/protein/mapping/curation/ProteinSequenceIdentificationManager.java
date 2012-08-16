package uk.ac.ebi.intact.protein.mapping.curation;

import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyWithSequence;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class allows to identify protein sequences using PICR or blast
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/08/12</pre>
 */

public class ProteinSequenceIdentificationManager {

    private FastaSequenceIterator fastaSequenceIterator;
    private File inputFile;
    private File outputFile;
    private String taxId;
    private ProteinSequenceResultsWriter resultsWriter;

    private StrategyWithSequence identificationStrategy;

    public ProteinSequenceIdentificationManager(String inputFile, String outputFile, String taxId) throws FileNotFoundException {
        if (inputFile == null){
            throw new IllegalArgumentException("The input file containing the sequences is mandatory");
        }
        if (outputFile == null){
            throw new IllegalArgumentException("The ouput file where to write the results is mandatory");
        }
        if (taxId == null){
            throw new IllegalArgumentException("The taxid for the sequences is mandatory");
        }

        this.inputFile = new File(inputFile);
        this.outputFile = new File(outputFile);
        this.taxId = taxId;

        this.fastaSequenceIterator = new FastaSequenceIterator(inputFile);
        this.resultsWriter = new ProteinSequenceResultsWriter(outputFile);

        this.identificationStrategy = new StrategyWithSequence();
        this.identificationStrategy.setBasicBlastRequired(true);
        this.identificationStrategy.setEnableIntactSearch(false);
        this.identificationStrategy.enableIsoforms(true);
    }

    public ProteinSequenceIdentificationManager(File inputFile, File outputFile, String taxId) throws IOException {
        if (inputFile == null){
            throw new IllegalArgumentException("The input file containing the sequences is mandatory");
        }
        if (outputFile == null){
            throw new IllegalArgumentException("The ouput file where to write the results is mandatory");
        }
        if (taxId == null){
            throw new IllegalArgumentException("The taxid for the sequences is mandatory");
        }

        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.taxId = taxId;

        this.fastaSequenceIterator = new FastaSequenceIterator(inputFile);
        this.resultsWriter = new ProteinSequenceResultsWriter(outputFile);

        this.identificationStrategy = new StrategyWithSequence();
        this.identificationStrategy.setBasicBlastRequired(true);
        this.identificationStrategy.setEnableIntactSearch(false);
        this.identificationStrategy.enableIsoforms(true);

    }

    public void runIdentificationJob() throws IOException {

        while (this.fastaSequenceIterator.hasNext()){
            FastaSequence fastaSequence = this.fastaSequenceIterator.next();

            // set context
            IdentificationContext context = new IdentificationContext();
            context.setSequence(fastaSequence.getSequence());
            context.setOrganism(new BioSource(taxId, taxId));

            try {
                IdentificationResults<? extends MappingReport> results = identificationStrategy.identifyProtein(context);

                this.resultsWriter.writeResults(fastaSequence, results);
            } catch (StrategyException e) {
                e.printStackTrace();
                this.resultsWriter.writeEmptyResults(fastaSequence);
            } catch (IOException e) {
                this.resultsWriter.writeEmptyResults(fastaSequence);
            }
        }
    }
}

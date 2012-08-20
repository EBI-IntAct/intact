package uk.ac.ebi.intact.protein.mapping.curation;

import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyWithSequence;

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
    private final static int maxNumberOfLinesPerFile = 5000;

    private StrategyWithSequence identificationStrategy;

    public ProteinSequenceIdentificationManager(String inputFile, String outputFile, String taxId) throws IOException {
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

        this.resultsWriter.writeHeader();

        int chunkIndex = 1;
        int numberOfLines = 1;

        while (this.fastaSequenceIterator.hasNext()){
            FastaSequence fastaSequence = this.fastaSequenceIterator.next();

            // set context
            IdentificationContext context = new IdentificationContext();
            context.setSequence(fastaSequence.getSequence());
            context.setOrganism(new BioSource(taxId, taxId));

            // create a new file with results if current one is too big;
            if (numberOfLines >= this.maxNumberOfLinesPerFile){
                chunkIndex++;
                numberOfLines = 0;

                String parentDirectory = this.outputFile.getParent();
                String currentName = this.outputFile.getName();
                String extension = currentName.contains(".") ? currentName.substring(currentName.indexOf(".")) : "";
                File newFile = new File(parentDirectory, currentName.replaceAll(extension, "")+"_"+chunkIndex+extension);

                this.outputFile = newFile;
                this.resultsWriter = new ProteinSequenceResultsWriter(outputFile);

                this.resultsWriter.writeHeader();
            }

            try {
                IdentificationResults<? extends MappingReport> results = identificationStrategy.identifyProtein(context);

                this.resultsWriter.writeResults(fastaSequence, results);
            } catch (Exception e) {
                e.printStackTrace();
                this.resultsWriter.writeEmptyResults(fastaSequence);
            }

            System.out.println("Processing fasta sequence " + fastaSequence.getIdentifier());
            numberOfLines++;
        }
    }

    public static void main(String[] args){

        if (args.length != 3){
            System.err.println( "Usage: ProteinSequenceIdentificationManager <input file name> <output file name> <taxid of the sequences>" );
            System.exit( 1 );
        }

        String inputFile = args[0];
        String outputFile = args[1];
        String taxId = args[2];

        try {
            ProteinSequenceIdentificationManager sequenceManager = new ProteinSequenceIdentificationManager(inputFile, outputFile, taxId);

            sequenceManager.runIdentificationJob();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

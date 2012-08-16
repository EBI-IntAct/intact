package uk.ac.ebi.intact.protein.mapping.curation;

import com.google.common.collect.UnmodifiableIterator;

import java.io.*;
import java.util.NoSuchElementException;

/**
 * Iterator of fasta sequences in a file
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/08/12</pre>
 */

public class FastaSequenceIterator extends UnmodifiableIterator<FastaSequence> {

    private final String identifierDelimiter=">";
    private BufferedReader fastaReader;
    private String lastLine;
    private FastaSequence lastFastaSequence;

    public FastaSequenceIterator(String fileName) throws FileNotFoundException {

        if (fileName == null){
            throw new IllegalArgumentException("The fast file should not be empty");
        }

        this.fastaReader = new BufferedReader(new FileReader(fileName));
    }

    public FastaSequenceIterator(File file) throws IOException {

        if (file == null){
            throw new IllegalArgumentException("The fast file should not be null");
        }

        this.fastaReader = new BufferedReader(new FileReader(file));

        // read first fasta sequence
        readNextFastaSequence();
    }

    private void readNextFastaSequence() throws IOException {

        // read next identifier
        lastLine = fastaReader.readLine();

        if (lastLine != null && !lastLine.startsWith(identifierDelimiter)){
            while (lastLine != null && !lastLine.startsWith(identifierDelimiter)){
                lastLine = fastaReader.readLine();
            }
        }

        if (lastLine == null){
            this.lastFastaSequence = null;
            return;
        }

        String identifier = lastLine.substring(lastLine.indexOf(identifierDelimiter));
        StringBuffer sequenceBuffer = new StringBuffer();

        // read sequence
        lastLine = fastaReader.readLine();
        while (lastLine != null && !lastLine.startsWith(identifierDelimiter)){

            sequenceBuffer.append(lastLine.replaceAll("\n",""));
            lastLine = fastaReader.readLine();
        }

        if (sequenceBuffer.length() == 0){
            this.lastFastaSequence = null;
            return;
        }

        this.lastFastaSequence = new FastaSequence(identifier, sequenceBuffer.toString());
    }

    @Override
    public boolean hasNext() {
        if(lastFastaSequence == null){
            if (this.fastaReader != null){
                try {
                    this.fastaReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.lastLine = null;
                this.fastaReader = null;
            }

            return false;
        }

        return true;
    }

    @Override
    public FastaSequence next() {
        if (lastFastaSequence == null){
            throw new NoSuchElementException("The fast file does not contain any fasta sequences");
        }

        return lastFastaSequence;
    }
}

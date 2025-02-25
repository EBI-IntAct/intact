package uk.ac.ebi.intact.util.uniprotExport.writers;

import uk.ac.ebi.intact.util.uniprotExport.converters.encoreconverters.DefaultEncoreInteractionToGoLineConverter;
import uk.ac.ebi.intact.util.uniprotExport.converters.encoreconverters.EncoreInteractionToGoLineConverter;
import uk.ac.ebi.intact.util.uniprotExport.writers.golinewriters.DefaultGOLineWriter;
import uk.ac.ebi.intact.util.uniprotExport.writers.golinewriters.GOLineWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A factory which creates the proper GO writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/03/11</pre>
 */

public class GOWriterFactory {

    /**
     *
     * @param goConverter
     * @param outputStream : the output of the GO line writer
     * @return a GOLine writer which is compatible with this converter, null otherwise
     * @throws java.io.IOException
     */
    public GOLineWriter createGOLineWriterFor(EncoreInteractionToGoLineConverter goConverter, OutputStreamWriter outputStream) throws IOException {

        if (goConverter == null){
            return null;
        }

        if (goConverter instanceof DefaultEncoreInteractionToGoLineConverter){
            return new DefaultGOLineWriter(outputStream);
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param goConverter
     * @param outputStream : the output file name of the GO line writer
     * @return a GOLine writer which is compatible with this converter, null otherwise
     * @throws IOException
     */
    public GOLineWriter createGOLineWriterFor(EncoreInteractionToGoLineConverter goConverter, String outputStream) throws IOException {

        if (goConverter == null){
            return null;
        }

        if (goConverter instanceof DefaultEncoreInteractionToGoLineConverter){
            return new DefaultGOLineWriter(new FileWriter(outputStream));
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param version
     * @param outputStream : the output of the DR line writer
     * @return a GOLine writer which is compatible with this version of the GO line format, null otherwise
     * @throws IOException
     */
    public GOLineWriter createGOLineWriterFor(int version, OutputStreamWriter outputStream) throws IOException {

        if (version == 1){
            return new DefaultGOLineWriter(outputStream);
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param version
     * @param outputStream : the output file name of the DR line writer
     * @return a GOLine writer which is compatible with this version of the GO line format, null otherwise
     * @throws IOException
     */
    public GOLineWriter createGOLineWriterFor(int version, String outputStream) throws IOException {

        if (version == 1){
            return new DefaultGOLineWriter(new FileWriter(outputStream));
        }
        else {
            return null;
        }
    }
}

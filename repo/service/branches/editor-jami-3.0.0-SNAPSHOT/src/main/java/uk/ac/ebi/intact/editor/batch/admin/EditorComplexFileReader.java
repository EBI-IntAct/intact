package uk.ac.ebi.intact.editor.batch.admin;

import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.Resource;
import uk.ac.ebi.intact.dataexchange.dbimporter.reader.ComplexFileReader;

import java.io.IOException;

/**
 * The editor file reader delete the file when it has finished
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/12/14</pre>
 */

public class EditorComplexFileReader extends ComplexFileReader {
    private org.springframework.core.io.Resource  errorResource;

    @Override
    public void close() throws ItemStreamException {
        super.close();
        try {
            getResource().getFile().delete();
            this.errorResource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Resource getErrorResource() {
        return errorResource;
    }

    public void setErrorResource(Resource errorResource) {
        this.errorResource = errorResource;
    }
}

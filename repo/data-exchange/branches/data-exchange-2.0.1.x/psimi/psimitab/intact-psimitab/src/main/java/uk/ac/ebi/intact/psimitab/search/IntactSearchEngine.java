/**
 * 
 */
package uk.ac.ebi.intact.psimitab.search;

import org.apache.lucene.store.Directory;
import psidev.psi.mi.search.column.DefaultColumnSet;
import psidev.psi.mi.search.engine.impl.FastSearchEngine;
import psidev.psi.mi.search.util.DocumentBuilder;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;

import java.io.File;
import java.io.IOException;

/**
 * A Search Engine based on lucene
 *
 * @author Nadin Neuhauser (nneuhaus@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class IntactSearchEngine extends FastSearchEngine<IntactBinaryInteraction> {

    private static final String[] DEFAULT_FIELDS = {"identifiers",
                                                    DefaultColumnSet.PUB_ID.getShortName(),
                                                    DefaultColumnSet.PUB_1ST_AUTHORS.getShortName(),
                                                    "species",
                                                    DefaultColumnSet.INTERACTION_TYPES.getShortName(),
                                                    DefaultColumnSet.INTER_DETECTION_METHODS.getShortName(),
                                                    DefaultColumnSet.INTERACTION_ID.getShortName(),
                                                    "properties",
                                                    IntactColumnSet.HOSTORGANISM.getShortName(),
                                                    IntactColumnSet.EXPANSION_METHOD.getShortName(),
                                                    IntactColumnSet.DATASET.getShortName()};

    public IntactSearchEngine(Directory indexDirectory) throws IOException {
        super(indexDirectory);
    }

    public IntactSearchEngine(File indexDirectory) throws IOException {
        super(indexDirectory);
    }

    public IntactSearchEngine(String indexDirectory) throws IOException {
        super(indexDirectory);
    }

    @Override
    protected DocumentBuilder createDocumentBuilder() {
        return new IntactDocumentBuilder();
    }

    protected String[] getSearchFields() {
        return DEFAULT_FIELDS;
    }

}

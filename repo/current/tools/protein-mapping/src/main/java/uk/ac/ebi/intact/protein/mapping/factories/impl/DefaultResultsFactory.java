package uk.ac.ebi.intact.protein.mapping.factories.impl;

import uk.ac.ebi.intact.protein.mapping.factories.ResultsFactory;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.results.PICRCrossReferences;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultIdentificationResults;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultPICRCrossReferences;

/**
 * Deafault factory for mapping results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public class DefaultResultsFactory implements ResultsFactory{
    @Override
    public BlastResults getBlastResults() {
        return new DefaultBlastResults();
    }

    @Override
    public IdentificationResults getIdentificationResults() {
        return new DefaultIdentificationResults();
    }

    @Override
    public PICRCrossReferences getPICRCrossReferences() {
        return new DefaultPICRCrossReferences();
    }
}

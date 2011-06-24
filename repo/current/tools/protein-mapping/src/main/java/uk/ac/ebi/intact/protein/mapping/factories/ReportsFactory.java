package uk.ac.ebi.intact.protein.mapping.factories;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.*;

/**
 * Factory for the reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface ReportsFactory {

    public BlastReport getBlastReport(ActionName name);
    public IntactCrc64Report getIntactCrc64Report(ActionName name);
    public IntactReport getIntactReport(ActionName name);
    public MappingReport getMappingReport(ActionName name);
    public PICRReport getPICRReport(ActionName name);
}

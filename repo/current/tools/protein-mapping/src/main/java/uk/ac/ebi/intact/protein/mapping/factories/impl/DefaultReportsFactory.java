package uk.ac.ebi.intact.protein.mapping.factories.impl;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.*;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.*;

/**
 * Default implementation of reports factory
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public class DefaultReportsFactory implements ReportsFactory {

    @Override
    public BlastReport getBlastReport(ActionName name) {
        return new DefaultBlastReport(name);
    }

    @Override
    public IntactCrc64Report getIntactCrc64Report(ActionName name) {
        return new DefaultIntactCrc64Report(name);
    }

    @Override
    public IntactReport getIntactReport(ActionName name) {
        return new DefaultIntactReport(name);
    }

    @Override
    public MappingReport getMappingReport(ActionName name) {
        return new DefaultMappingReport(name);
    }

    @Override
    public PICRReport getPICRReport(ActionName name) {
        return new DefaultPICRReport(name);
    }
}

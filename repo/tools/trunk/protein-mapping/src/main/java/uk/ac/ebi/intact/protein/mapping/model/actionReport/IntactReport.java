package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import java.util.Set;

/**
 * Interface to implement for IntactReports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface IntactReport extends MappingReport{

    public String getIntactAc();
    public void setIntactAc(String ac);
    public Set<String> getPossibleIntactIds();
    public void addPossibleIntactAc(String intactid);
}

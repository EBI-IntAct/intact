package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import uk.ac.ebi.intact.protein.mapping.results.PICRCrossReferences;

import java.util.Set;

/**
 * Interface to implement for PICR reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface PICRReport extends MappingReport{

    public Set<PICRCrossReferences> getCrossReferences();
    public void addCrossReference(String databaseName, String accession);
    public void addPICRCrossReference(PICRCrossReferences refs);
}

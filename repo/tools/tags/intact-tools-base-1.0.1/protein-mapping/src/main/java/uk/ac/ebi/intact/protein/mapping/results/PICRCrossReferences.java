package uk.ac.ebi.intact.protein.mapping.results;

import java.util.Set;

/**
 * Interface for PICR corss references
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface PICRCrossReferences {

    public String getDatabase();
    public Set<String> getAccessions();
    public String getListOfAccessions();
    public void addAccession(String accession);
}

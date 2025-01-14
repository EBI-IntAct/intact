package uk.ac.ebi.intact.dbupdate.prot.actions;

import uk.ac.ebi.intact.dbupdate.prot.ProcessorException;
import uk.ac.ebi.intact.dbupdate.prot.event.ProteinEvent;

/**
 * The interface to implement for classes deleting proteins
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Dec-2010</pre>
 */

public interface ProteinDeleter {

    /**
     * Delete the protein from the database
     * @param evt : contains the protein to delete
     * @throws ProcessorException
     */
    public void delete(ProteinEvent evt) throws ProcessorException;
}

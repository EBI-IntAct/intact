package uk.ac.ebi.intact.dbupdate.prot.event;

import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.Protein;

import java.util.Collection;
import java.util.EventObject;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>06-Dec-2010</pre>
 */

public class InvalidIntactParentFoundEvent extends ProteinEvent{

    String oldParentAc;
    private Collection<String> parents;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public InvalidIntactParentFoundEvent(Object source, DataContext context, Protein protein, String oldParentAc, Collection<String> parents) {
        super(source, context, protein);
        this.parents = parents;
        this.oldParentAc = oldParentAc;
    }


    public Collection<String> getParents() {
        return parents;
    }

    public String getOldParentAc() {
        return oldParentAc;
    }
}

package uk.ac.ebi.intact.psixml.converter;

import psidev.psi.mi.xml.model.Entry;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractIntactPsiConverter<I, P> implements IntactPsiConverter<I, P> {

    private IntactContext intactContext;
    private Entry parentEntry;

    public AbstractIntactPsiConverter(IntactContext intactContext, Entry parentEntry) {
        this.intactContext = intactContext;
        this.parentEntry = parentEntry;
    }

    protected IntactContext getIntactContext() {
        return intactContext;
    }

    protected Institution getInstitution() {
        return intactContext.getConfig().getInstitution();
    }

    public Entry getParentEntry() {
        return parentEntry;
    }
}

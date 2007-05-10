package uk.ac.ebi.intact.psixml.converter;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.IntactObject;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractIntactPsiConverter<I extends IntactObject, P> implements IntactPsiConverter<I,P>
{
    private IntactContext intactContext;

    public AbstractIntactPsiConverter(IntactContext intactContext)
    {
        this.intactContext = intactContext;
    }

    protected IntactContext getIntactContext()
    {
        return intactContext;
    }

    protected Institution getInstitution()
    {
        return intactContext.getConfig().getInstitution();
    }
}

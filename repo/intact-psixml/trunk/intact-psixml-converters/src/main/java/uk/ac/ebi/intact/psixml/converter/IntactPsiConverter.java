package uk.ac.ebi.intact.psixml.converter;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * Converter interface, which converters Intact<->Psi must implement
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactPsiConverter<I extends IntactObject,P>
{
    I psiToIntact(P psiObject);
    P intactToPsi(I intactObject);
}

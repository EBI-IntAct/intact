package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;
import java.util.Collections;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@SanityRule (target = AnnotatedObject.class)
public class XrefWithNonValidPrimaryId implements Rule<AnnotatedObject<?,?>>
{
    public Collection<GeneralMessage> check(AnnotatedObject<?,?> intactObject) throws SanityRuleException {
        for (Xref xref : intactObject.getXrefs()) {
            String primaryId = xref.getPrimaryId();

            System.out.println("XrefWithNonValidPrimaryId: "+intactObject.getShortLabel()+" - "+xref.getCvDatabase().getAnnotations());
        }

        return Collections.EMPTY_LIST;
    }
}

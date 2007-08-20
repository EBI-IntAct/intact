package uk.ac.ebi.intact.sanity.rules.cvobject;

import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@SanityRule (target = CvObject.class)
public class CvObjectWithoutXrefs implements Rule<CvObject>
{
    public Collection<GeneralMessage> check(CvObject intactObject) throws SanityRuleException
    {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if (intactObject.getXrefs().isEmpty()) {
            messages.add(new GeneralMessage("CvObject without Xrefs", MessageLevel.MAJOR, "Add Xrefs to the CvObject", intactObject));
        }

        return messages;
    }
}

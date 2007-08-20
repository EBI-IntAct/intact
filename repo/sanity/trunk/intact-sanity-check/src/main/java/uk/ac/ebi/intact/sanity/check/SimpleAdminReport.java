package uk.ac.ebi.intact.sanity.check;

import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleAdminReport
{
    private Collection<GeneralMessage> cvObjectMessages;

    public SimpleAdminReport() {

    }

    public Collection<GeneralMessage> getCvObjectMessages()
    {
        return cvObjectMessages;
    }

    public void setCvObjectMessages(Collection<GeneralMessage> cvObjectMessages)
    {
        this.cvObjectMessages = cvObjectMessages;
    }
}

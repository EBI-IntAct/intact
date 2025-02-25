package uk.ac.ebi.intact.jami.model;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.model.user.User;

import java.util.Date;

/**
 * Interface for IntAct lyfecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface LifeCycleEvent extends Auditable{

    public static String NEW_STATUS = "new";
    public static String RELEASED = "released";
    public static String READY_FOR_RELEASE = "ready for release";

    public CvTerm getEvent();

    public void setEvent( CvTerm event );

    public User getWho();

    public void setWho( User who );

    public Date getWhen();

    public void setWhen( Date when );

    public String getNote();

    public void setNote( String note );
}

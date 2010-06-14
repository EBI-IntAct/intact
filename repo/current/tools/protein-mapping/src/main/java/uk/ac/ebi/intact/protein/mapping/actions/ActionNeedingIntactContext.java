package uk.ac.ebi.intact.protein.mapping.actions;

import uk.ac.ebi.intact.core.context.IntactContext;

/**
 * The abstract class to extend if a process needs an Intact context
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>06-May-2010</pre>
 */

public abstract class ActionNeedingIntactContext extends IdentificationActionImpl{

    /**
     * The intact context of this object
     */
    protected IntactContext intactContext;

    /**
     * Create an ActionNeedingIntactContext with an Intact context which is null and should be set later using the setIntactContext method
     */
    public ActionNeedingIntactContext(){
        super();
        intactContext = null;
    }

    /**
     * Create a ActionNeedingIntactContext with an IntactContext 'context'
     * @param context
     */
    public ActionNeedingIntactContext(IntactContext context){
        super();
        intactContext = context;
    }

    /**
     *
     * @return  The Intact context of this object
     */
    public IntactContext getIntactContext() {
        return intactContext;
    }

    /**
     * Set the intact context of this object to 'intactContext'
     * @param intactContext : the intact context
     */
    public void setIntactContext(IntactContext intactContext) {
        this.intactContext = intactContext;
    }
}

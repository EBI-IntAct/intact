package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/persister of confidenceClass
 * It does not cache persisted confidences. It only synchronize the confidence type (with persist = true) to make sure that the confidence type
 * is persisted before so the confidence can be persisted
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ConfidenceSynchronizerTemplate<T extends Confidence, C extends AbstractIntactConfidence> extends AbstractIntactDbSynchronizer<T, C>
        implements ConfidenceSynchronizer<T,C> {

    private static final Log log = LogFactory.getLog(ConfidenceSynchronizerTemplate.class);

    public ConfidenceSynchronizerTemplate(SynchronizerContext context, Class<? extends C> confClass){
        super(context, confClass);
    }

    public C find(T object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(getContext().getConfidenceTypeSynchronizer().synchronize(type, true));
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(C object) {
        return object.getAc();
    }

    @Override
    protected C instantiateNewPersistentInstance(T object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getType(), object.getValue());
    }

    @Override
    protected void storeInCache(T originalObject, C persistentObject, C existingInstance) {
        // nothing to do
    }

    @Override
    protected C fetchObjectFromCache(T object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, C>(this));
    }
}

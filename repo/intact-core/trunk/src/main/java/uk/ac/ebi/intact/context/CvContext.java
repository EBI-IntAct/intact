/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Application-wide context to cache the loaded CVs, so it is faster to
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Aug-2006</pre>
 */
public final class CvContext implements Serializable
{

    private static final Log log = LogFactory.getLog(CvContext.class);

    private static final String APPLICATION_PARAM_NAME = CvContext.class.getName()+".CONFIG";

    private Map<String,CvObject> cachedByAc;
    private Map<String,CvObject> cachedByLabel;
    private Map<String,CvObject> cachedByMiRef;

    private IntactSession session;

    private CvContext(IntactSession session)
    {
        this.session = session;

        this.cachedByAc = new HashMap<String, CvObject>();
        this.cachedByLabel = new HashMap<String, CvObject>();
        this.cachedByMiRef = new HashMap<String, CvObject>();
    }

    public static CvContext getCurrentInstance(IntactSession session)
    {
        CvContext cvContext
                = (CvContext)session.getApplicationAttribute(APPLICATION_PARAM_NAME);
        if (cvContext == null)
        {
            log.debug("Creating new CvContext");
            cvContext = new CvContext(session);
            session.setApplicationAttribute(APPLICATION_PARAM_NAME, cvContext);
        }
        return cvContext;
    }

    public CvObject getByAc(String ac)
    {
        return getByAc(ac, false);
    }

    public CvObject getByAc(String ac, boolean forceReload)
    {
        if (!forceReload && cachedByAc.containsKey(ac))
        {
            return cachedByAc.get(ac);
        }

        CvObject cvObject = getDaoFactory().getCvObjectDao().getByAc(ac);

        if (cvObject == null)
        {
            throw new IllegalArgumentException("CvObject with this AC does not exist: "+ac);
        }

        putCv(cvObject);

        return cvObject;
    }


    public CvObject getByLabel(String label)
    {
        return getByLabel(label, false);
    }


    public CvObject getByLabel(String label, boolean forceReload)
    {
        if (!forceReload && cachedByLabel.containsKey(label))
        {
            return cachedByLabel.get(label);
        }

        CvObject cvObject = getDaoFactory().getCvObjectDao().getByShortLabel(label);

        if (cvObject == null)
        {
            throw new IllegalArgumentException("CvObject with this label does not exist: "+label);
        }

        putCv(cvObject);

        return cvObject;
    }

    public CvObject getByMiRef(String miRef)
    {
        return getByMiRef(miRef, false);
    }

    public CvObject getByMiRef(String miRef, boolean forceReload)
    {
        if (!forceReload && cachedByMiRef.containsKey(miRef))
        {
            return cachedByMiRef.get(miRef);
        }

        CvObject cvObject = getDaoFactory().getCvObjectDao().getByXref(miRef);

        if (cvObject == null)
        {
            throw new IllegalArgumentException("CvObject with this MI does not exist: "+miRef);
        }

        putCv(cvObject);
        putCvInMiRef(miRef, cvObject);

        return cvObject;
    }

    public void clearCache()
    {
        cachedByAc.clear();
        cachedByLabel.clear();
        cachedByMiRef.clear();
    }

    public void loadCommonCvObjects()
    {
        getByMiRef( CvDatabase.INTACT_MI_REF);
        getByMiRef( CvDatabase.GO_MI_REF);
        getByMiRef( CvDatabase.INTERPRO_MI_REF);
        getByMiRef( CvDatabase.FLYBASE_MI_REF);
        getByMiRef( CvDatabase.REACTOME_PROTEIN_PSI_REF);
        getByMiRef( CvDatabase.HUGE_MI_REF);
        getByMiRef( CvXrefQualifier.IDENTITY_MI_REF);
        getByMiRef( CvXrefQualifier.SECONDARY_AC_MI_REF);
        getByMiRef( CvXrefQualifier.ISOFORM_PARENT_MI_REF);

        // only one search by shortlabel as it still doesn't have MI number.
        getByLabel( CvTopic.ISOFORM_COMMENT);
        getByLabel( CvTopic.NON_UNIPROT);

        getByMiRef( CvAliasType.GENE_NAME_MI_REF);
        getByMiRef( CvAliasType.GENE_NAME_SYNONYM_MI_REF);
        getByMiRef( CvAliasType.ISOFORM_SYNONYM_MI_REF);
        getByMiRef( CvAliasType.LOCUS_NAME_MI_REF);
        getByMiRef( CvAliasType.ORF_NAME_MI_REF);
        getByMiRef( CvInteractorType.getProteinMI());
    }

    public CvComponentRole getBait() throws IntactException
    {
        return (CvComponentRole) getByMiRef( CvComponentRole.BAIT_PSI_REF );
    }

    public CvComponentRole getPrey() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.PREY_PSI_REF );
    }

    public CvComponentRole getNeutral() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.NEUTRAL_PSI_REF );
    }

    public CvComponentRole getSelf() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.SELF_PSI_REF );
    }

    public CvComponentRole getEnzyme() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.ENZYME_PSI_REF );
    }

    public CvComponentRole getEnzymeTarget() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.ENZYME_TARGET_PSI_REF );
    }

    public CvComponentRole getUnspecified() throws IntactException {

        return (CvComponentRole) getByMiRef( CvComponentRole.UNSPECIFIED_PSI_REF );
    }

    private void putCv(CvObject cv)
    {
        cachedByAc.put(cv.getAc(), cv);
        cachedByLabel.put(cv.getShortLabel(), cv);
    }

    private void putCvInMiRef(String miRef, CvObject cv)
    {
        cachedByMiRef.put(miRef, cv);
    }

    private DaoFactory getDaoFactory()
    {
        return DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());
    }

    /**
        * Updates a CvObject already present in the context
        *
        * @param cvObject The new value for the cvObject. Note that it must be equal to the existing one in order to be
        *                 updated
        */
       public void updateCvObject( CvObject cvObject ) {
            if(cvObject == null){
                throw new IllegalArgumentException("The cvObject is null.");
            }
            else if(cvObject.getAc() == null){
                throw new IllegalArgumentException("The cvObject does not have an ac.");
            }else if (cvObject.getShortLabel() == null){
                throw new IllegalArgumentException("The cvObject does not have a shortlabel.");
            }

            //Remove the old cv from the cachedByLabel map
            Iterator it = cachedByLabel.entrySet().iterator();
            while (it.hasNext()) {
            Map.Entry<String,CvObject> pairs = (Map.Entry)it.next();
                if(pairs.getValue().getAc().equals(cvObject.getAc())){
                    it.remove();
                }
            }
            //Add the new one.
            cachedByLabel.put(cvObject.getShortLabel(), cvObject);

            // Update the cvObject in cachedByAc (we don't need to remove it first as it's quite unlikely that the
            // ac changed whereas the shortlabel could change.
            cachedByAc.put(cvObject.getAc(), cvObject);
            
       }

}

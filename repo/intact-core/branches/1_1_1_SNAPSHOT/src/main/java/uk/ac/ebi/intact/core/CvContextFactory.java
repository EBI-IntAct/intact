/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import static uk.ac.ebi.intact.core.CvContext.CvName;
import uk.ac.ebi.intact.core.CvContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Creates new instances of CvContext
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Mar-2006</pre>
 */
public class CvContextFactory
{
    protected static final Logger logger = Logger.getLogger("updateProtein");

    private static final String CV_TOPIC_SEARCH_URL_ASCII = "search-url-ascii";

    /**
     * This method creates a new instance of a <code>CvContext</code> and stores it in the ServletContext
     * so it is only created once
     * @param helper The IntactHelper to use
     * @return the new CvContext
     * @throws UpdateException thrown if something goes wrong during the retrieval of the CvObjects
     * from the database
     */
    public static CvContext createCvContext(IntactHelper helper) throws UpdateException
    {
        CvContext cvContext = new CvContext(){};

        /**
         * Load CVs abd put them in the cvContext.
         */
        try
        {
            CvObject sgdDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.SGD_MI_REF); // sgd
            cvContext.putCvObject(CvName.SGD_DB, sgdDatabase);

            CvObject uniprotDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.UNIPROT_MI_REF); // uniprot
            cvContext.putCvObject(CvName.UNIPROT_DB, uniprotDatabase);

            // search for the SRS link.
            Collection annotations = uniprotDatabase.getAnnotations();
            if (annotations != null)
            {
                // find the CvTopic search-url-ascii
                Annotation searchedAnnotation = null;
                for (Iterator iterator = annotations.iterator(); iterator.hasNext() && searchedAnnotation == null;)
                {
                    Annotation annotation = (Annotation) iterator.next();
                    if (CV_TOPIC_SEARCH_URL_ASCII.equals(annotation.getCvTopic().getShortLabel()))
                    {
                        searchedAnnotation = annotation;
                    }
                }

                if (searchedAnnotation != null)
                {
                    String srsUrl = searchedAnnotation.getAnnotationText();
                    cvContext.setSrsUrl(srsUrl);
                    if (logger != null)
                    {
                        logger.info("Found UniProt URL in the Uniprot CvDatabase: " + srsUrl);
                    }
                }
                else
                {
                    String msg = "Unable to find an annotation having a CvTopic: " + CV_TOPIC_SEARCH_URL_ASCII +
                            " in the UNIPROT database";
                    if (logger != null)
                    {
                        logger.error(msg);
                    }
                    throw new UpdateException(msg);
                }
            }
            else
            {
                String msg = "No Annotation in the UNIPROT database, could not get the UniProt URL.";
                if (logger != null)
                {
                    logger.error(msg);
                }
                throw new UpdateException(msg);
            }

            CvObject intactDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.INTACT_MI_REF);
            cvContext.putCvObject(CvName.INTACT_DB, intactDatabase);
            CvObject goDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.GO_MI_REF);
            cvContext.putCvObject(CvName.GO_DB, goDatabase);
            CvObject interproDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.INTERPRO_MI_REF);
            cvContext.putCvObject(CvName.INTERPRO_DB, interproDatabase);
            CvObject flybaseDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.FLYBASE_MI_REF);
            cvContext.putCvObject(CvName.FLYBASE_DB, flybaseDatabase);
            CvObject reactomeDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.REACTOME_PROTEIN_PSI_REF);
            cvContext.putCvObject(CvName.REACTOME_DB, reactomeDatabase);
            CvObject hugeDatabase = getCvObjectViaMI(helper, CvDatabase.class, CvDatabase.HUGE_MI_REF);
            cvContext.putCvObject(CvName.HUGE_DB, hugeDatabase);

            CvObject identityXrefQualifier = getCvObjectViaMI(helper, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
            cvContext.putCvObject(CvName.IDENTITY_XREF_QUALIFIER, identityXrefQualifier);
            CvObject secondaryXrefQualifier = getCvObjectViaMI(helper, CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF);
            cvContext.putCvObject(CvName.SECONDARY_XREF_QUALIFIER, secondaryXrefQualifier);
            CvObject isoFormParentXrefQualifier = getCvObjectViaMI(helper, CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF);
            cvContext.putCvObject(CvName.ISOFORM_PARENT_XREF_QUALIFIER, isoFormParentXrefQualifier);

            // only one search by shortlabel as it still doesn't have MI number.
            CvObject isoformComment = getCvObject(helper, CvTopic.class, CvTopic.ISOFORM_COMMENT);
            cvContext.putCvObject(CvName.ISOFORM_COMMENT, isoformComment);
            CvObject noUniprotUpdate = getCvObject(helper, CvTopic.class, CvTopic.NON_UNIPROT);
            cvContext.putCvObject(CvName.NO_UNIPROT_UPDATE, noUniprotUpdate);


            CvObject geneNameAliasType = getCvObjectViaMI(helper, CvAliasType.class, CvAliasType.GENE_NAME_MI_REF);
            cvContext.putCvObject(CvName.GENE_NAME_ALIAS_TYPE, geneNameAliasType);
            CvObject geneNameSynonymAliasType = getCvObjectViaMI(helper, CvAliasType.class, CvAliasType.GENE_NAME_SYNONYM_MI_REF);
            cvContext.putCvObject(CvName.GENE_NAME_SYNONYM_ALIAS_TYPE, geneNameSynonymAliasType);
            CvObject isoformSynonym = getCvObjectViaMI(helper, CvAliasType.class, CvAliasType.ISOFORM_SYNONYM_MI_REF);
            cvContext.putCvObject(CvName.ISOFORM_SYNONYM, isoformSynonym);
            CvObject locusNameAliasType = getCvObjectViaMI(helper, CvAliasType.class, CvAliasType.LOCUS_NAME_MI_REF);
            cvContext.putCvObject(CvName.LOCUS_NAME_ALIAS_TYPE, locusNameAliasType);
            CvObject orfNameAliasType = getCvObjectViaMI(helper, CvAliasType.class, CvAliasType.ORF_NAME_MI_REF);
            cvContext.putCvObject(CvName.ORF_NAME_ALIAS_TYPE, orfNameAliasType);

            CvObject proteinType = getCvObjectViaMI(helper, CvInteractorType.class, CvInteractorType.getProteinMI());
            cvContext.putCvObject(CvName.PROTEIN_TYPE, proteinType);

        }
        catch (IntactException e)
        {
            if (logger != null)
            {
                logger.error(e);
            }
            throw new UpdateException("Couldn't find needed object in IntAct, cause: " + e.getMessage());
        }

       return cvContext;
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param clazz      the Class we are looking for
     * @param shortlabel the shortlabel of the object we are looking for
     * @return the CvObject of type <code>clazz</code> and having the shortlabel <code>shorltabel<code>.
     * @throws UpdateException if the object is not found.
     */
    private static CvObject getCvObject
            (IntactHelper helper, Class
                    clazz, String
                    shortlabel) throws IntactException, UpdateException
    {

        CvObjectDao<CvObject> cvObjectDao = DaoFactory.getCvObjectDao();
        CvObject cv =  cvObjectDao.getByShortLabel(shortlabel);
        if (cv == null)
        {
            StringBuffer sb = new StringBuffer(128);
            sb.append("Could not find ");
            sb.append(shortlabel);
            sb.append(' ');
            sb.append(clazz.getName());
            sb.append(" in your IntAct node");

            if (logger != null)
            {
                logger.error(sb.toString());
            }
            throw new UpdateException(sb.toString());
        }

        return cv;
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param clazz the Class we are looking for
     * @param miRef the PSI-MI reference of the object we are looking for
     * @return the CvObject of type <code>clazz</code> and having the PSI-MI reference.
     * @throws IntactException if the search failed
     * @throws UpdateException if the object is not found.
     */
    private static CvObject getCvObjectViaMI
            (IntactHelper helper, Class
                    clazz, String
                    miRef) throws IntactException,
                                  UpdateException
    {
        CvObjectDao<CvObject> cvObjectDao = DaoFactory.getCvObjectDao();
        CvObject cv = cvObjectDao.getByXref(miRef);

        if (cv == null)
        {
            StringBuffer sb = new StringBuffer(128);
            sb.append("Could not find ");
            sb.append(miRef);
            sb.append(' ');
            sb.append(clazz.getName());
            sb.append(" in your IntAct node");

            if (logger != null)
            {
                logger.error(sb.toString());
            }
            throw new UpdateException(sb.toString());
        }

        return cv;
    }

    public static class UpdateException extends RuntimeException
    {

        public UpdateException(String message)
        {
            super(message);
        }
    }

}

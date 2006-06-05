/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.msd.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

/**
 * This class hold commonly used Cv, BioSource ...etc and there respective static getter methods. It is used, to help
 * the creation of IntAct object like Experiment, Interaction... in the intactGenerator package.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class MsdToolBox {

    /**
     * An intactHelper used to retrieve inVitro, owner, authorList...etc
     */
    private static IntactHelper helper;
    /**
     * The in vitro bisource ( the biosource with taxid = -1).
     */
    private static BioSource inVitro;
    /**
     * The owner Institution.
     */
    private static Institution owner;
    /**
     * The author-list CvTopic.
     */
    private static CvTopic authorList;
    /**
     * The contact-email CvTopic.
     */
    private static CvTopic contactEmail;
    /**
     * The journal CvTopic.
     */
    private static CvTopic journal;
    /**
     * The publication-year CvTopic.
     */
    private static CvTopic publicationYear;
    /**
     * The predetermined CvIdentification method.
     */
    private static CvIdentification predetermined;
    /**
     * The pubmed CvDatabase.
     */
    private static CvDatabase pubmed;
    /**
     * The primary-reference XrefQualifier.
     */
    private static CvXrefQualifier primaryRef;
    /**
     * The interaction CvInteractorType.
     */
    private static CvInteractorType interaction;
    /**
     * The direct interaction CvInteractionType
     */
    private static CvInteractionType directInteraction;
    /**
     * The neutral component CvComponentRole.
     */
    private static CvComponentRole neutral;
    /**
     * The identity CvXrefQualifier.
     */
    private static CvXrefQualifier identity;
    /**
     * The uniprotKb CvDatabase.
     */
    private static CvDatabase uniprot;
    /**
     * The pdb cvDatabase.
     */
    private static CvDatabase pdb;

    /**
     * Class used to get the IntactHelper. If the helper is null, it instanciates it and then returns it. If it is not
     * null it returns it directly.
     * @return an IntactHelper object
     * @throws IntactException
     */
    private static IntactHelper getIntactHelper() throws IntactException {
        if(helper==null){
            helper = new IntactHelper();
        }
        return helper;
    }

    /**
     * This method gives the inVitro BioSource. If inVitro is null, it retrieves it from the database.
     * @return the inVitro bioSource.
     * @throws IntactException
     */
    public static BioSource getInVitro() throws IntactException {
        if(inVitro==null){
            IntactHelper helper = getIntactHelper();
            inVitro = helper.getBioSourceByTaxId("-1");
            helper.closeStore();
        }
        return inVitro;
    }
   /**
     * This method gives the owner Institution. If owner is null, it retrieves it from the database.
     * @return the owner Institution.
     * @throws IntactException
     */
    public static Institution getOwner() throws IntactException {
        if(owner == null){
            IntactHelper helper = getIntactHelper();
            owner = helper.getInstitution();
            helper.closeStore();
        }
        return owner;
    }

    /**
     * This method gives the authorList cvTopic. If authorList is null, it retrieves it from the database.
     * @return the authorList cvTopic.
     * @throws IntactException
     */
    public static CvTopic getAuthorList() throws IntactException {
        if(authorList == null){
            IntactHelper helper = getIntactHelper();
            authorList=helper.getObjectByPrimaryId(CvTopic.class, CvTopic.AUTHOR_LIST_MI_REF);
        }
        return authorList;
    }

    /**
     * This method gives the contactEmail cvTopic. If contactEmail is null, it retrieves it from the database.
     * @return the contactEmail cvTopic.
     * @throws IntactException
     */
    public static CvTopic getContactEmail() throws IntactException {
        if(contactEmail == null){
            IntactHelper helper = getIntactHelper();
            contactEmail=helper.getObjectByPrimaryId(CvTopic.class, CvTopic.CONTACT_EMAIL_MI_REF);
        }
        return contactEmail;
    }

    /**
     * This method gives the journal cvTopic. If journal is null, it retrieves it from the database.
     * @return the journal cvTopic.
     * @throws IntactException
     */
    public static CvTopic getJournal() throws IntactException {
        if(journal == null){
            IntactHelper helper = getIntactHelper();
            journal = helper.getObjectByLabel(CvTopic.class, CvTopic.JOURNAL);
        }
        return journal;
    }

    /**
     * This method gives the publicationYear cvTopic. If publicationYear cvTopic is null, it retrieves it from the
     * database.
     * @return the publicationYear cvTopic.
     * @throws IntactException
     */
    public static CvTopic getPublicationYear() throws IntactException {
        if(publicationYear == null){
            IntactHelper helper = getIntactHelper();
            publicationYear = helper.getObjectByLabel(CvTopic.class, CvTopic.PUBLICATION_YEAR);
        }
        return journal;
    }

    /**
     * This method returns the predetermined CvIdentification. If predetermined CvIdentification is null, it retrieves
     * it from the database.
     * @return the predetermined CvIdentification.
     * @throws IntactException
     */
    public static CvIdentification getPredetermined() throws IntactException {
        if(predetermined == null){
            IntactHelper helper = getIntactHelper();
            predetermined=helper.getObjectByPrimaryId(CvIdentification.class, CvIdentification.PREDETERMINED_MI_REF);
        }
        return predetermined;
    }

    /**
     * This method returns the pubmed CvDatabase. If pubmed CvDatabase is null, it retrieves it from the database.
     * @return the pubmed CvDatabase.
     * @throws IntactException
     */
    public static CvDatabase getPubmed() throws IntactException {
        if(pubmed == null){
            IntactHelper helper = getIntactHelper();
            pubmed = helper.getObjectByPrimaryId(CvDatabase.class, CvDatabase.PUBMED_MI_REF);
        }
        return pubmed;
    }

    /**
     * This method returns the primaryRef CvXrefQualifier. If primaryRef CvXrefQualifier is null, it retrieves it from
     *  the database.
     * @return the primaryRef CvXrefQualifier.
     * @throws IntactException
     */
    public static CvXrefQualifier getPrimaryRef() throws IntactException {
        if(primaryRef == null){
            IntactHelper helper = getIntactHelper();
            primaryRef = helper.getObjectByPrimaryId(CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
        }
        return primaryRef;
    }

    /**
     * This method returns the interaction CvInteractorType. If interaction CvInteractorType is null, it retrieves it from
     *  the database.
     * @return the interaction CvInteractorType.
     * @throws IntactException
     */
    public static CvInteractorType getInteraction() throws IntactException {
        if(interaction == null){
            IntactHelper helper = getIntactHelper();
            interaction = helper.getObjectByPrimaryId(CvInteractorType.class, CvInteractorType.INTERACTION_MI_REF);
        }
        return interaction;
    }
    /**
     * This method returns the directInteraction CvInteractionType. If directInteraction CvInteractionType is null, it
     * retrieves it from the database.
     * @return the directInteraction CvInteractionType.
     * @throws IntactException
     */
    public static CvInteractionType getDirectInteraction() throws IntactException {
        if(directInteraction == null){
            IntactHelper helper = getIntactHelper();
            directInteraction = helper.getObjectByPrimaryId(CvInteractionType.class, CvInteractionType.DIRECT_INTERACTION_MI_REF);
        }
        return directInteraction;
    }

    /**
     * This method returns the neutral CvComponentRole. If neutral CvComponentRole is null, it retrieves it from the
     * database.
     * @return the neutral CvComponentRole.
     * @throws IntactException
     */
    public static CvComponentRole getNeutral() throws IntactException {
        if(neutral == null){
            IntactHelper helper = getIntactHelper();
            neutral = helper.getObjectByPrimaryId(CvComponentRole.class, CvComponentRole.NEUTRAL_PSI_REF);
        }
        return neutral;
    }

    /**
     * This method returns the neutral CvXrefQualifier. If neutral CvXrefQualifier is null, it retrieves it from the
     * database.
     * @return the neutral CvXrefQualifier.
     * @throws IntactException
     */
    public static CvXrefQualifier getIdentity() throws IntactException {
        if(identity == null){
            IntactHelper helper = getIntactHelper();
            identity = helper.getObjectByPrimaryId(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        }
        return identity;
    }


    /**
     * This method returns the neutral CvDatabase. If neutral CvDatabase is null, it retrieves it from the
     * database.
     * @return the neutral CvDatabase.
     * @throws IntactException
     */
    public static CvDatabase getUniprot() throws IntactException {
        if(uniprot == null){
            IntactHelper helper = getIntactHelper();
            uniprot = helper.getObjectByPrimaryId(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        }
        return uniprot;
    }

    /**
     * This method returns the pdb CvDatabase. If pdb CvDatabase is null, it retrieves it from the
     * database.
     * @return the pdb CvDatabase.
     * @throws IntactException
     */
    public static CvDatabase getPdb() throws IntactException {
        if(uniprot == null){
            IntactHelper helper = getIntactHelper();
            uniprot = helper.getObjectByPrimaryId(CvDatabase.class, CvDatabase.PDB_MI_REF);
        }
        return uniprot;
    }


    public static void main(String[] args) throws IntactException {
        MsdToolBox.getJournal();
        MsdToolBox.getJournal();
        MsdToolBox.getJournal();


    }
}

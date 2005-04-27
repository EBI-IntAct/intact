/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.business;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.view.bean.CvBean;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides method to retrieve the list of shortlabel for different CVs
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class CvLists {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    // collection holding per CvDatabase object one CvBean
    private Collection CVDatabase = null;

    // collection holding per CvTopic object one CvBean
    private Collection CVTopic = null;

    // collection holding per CvInteraction object one CvBean
    private Collection CVInteraction = null;

    // collection holding per CvInteractionType object one CvBean
    private Collection CVInteractionType = null;

    // collection holding per CvIdentification object one CvBean
    private Collection CVIdentification = null;

   /**
    * this method retrieves all CvDatabase objects and creates one Bean per object.
    * These beans store information (ac, shortlabel, fullname) about the object and are all added into a collection
    * @return collection containing all CVBeans (one per object)
    * @throws IntactException
    */
    public Collection initCVDatabaseList() throws IntactException {
        logger.info("in initCVDatabaseList");
        Collection databases = null;
        this.CVDatabase = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean(null, "-all databases-", "all databases selected");
        this.CVDatabase.add(emptyBean);
        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            databases = helper.search(CvDatabase.class.getName(), "ac", null);
            for (Iterator iterator = databases.iterator(); iterator.hasNext();) {
                CvDatabase cvDb = (CvDatabase) iterator.next();
                bean = new CvBean(cvDb.getAc(), cvDb.getShortLabel(), cvDb.getFullName());
                this.CVDatabase.add(bean);
            }

        } catch (IntactException e) {
            e.printStackTrace();
        } finally {
            helper.closeStore();
        }
        return this.CVDatabase;
    }

    /**
    * this method retrieves all CvTopic objects and creates one Bean per object.
    * These beans store information (ac, shortlabel, fullname) about the object and are all added into a collection
    * @return collection containing all CVBeans (one per object)
    * @throws IntactException
    */
    public Collection initCVTopicList() throws IntactException {
        Collection databases = null;
        this.CVTopic = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean(null, "-all topics-", "all topics selected");
        this.CVTopic.add(emptyBean);
        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            databases = helper.search(CvTopic.class.getName(), "ac", null);
            for (Iterator iterator = databases.iterator(); iterator.hasNext();) {
                CvTopic cvTo = (CvTopic) iterator.next();
                // do not insert the topic 'remark-interal', it should not be seen from outside
                if (!cvTo.getShortLabel().equalsIgnoreCase("remark-internal")) {
                    bean = new CvBean(cvTo.getAc(), cvTo.getShortLabel(), cvTo.getFullName());
                    this.CVTopic.add(bean);
                }
            }

        } catch (IntactException e) {
            e.printStackTrace();
        } finally {
            helper.closeStore();
        }
        return this.CVTopic;
    }


    /**
    * this method retrieves all CvTopic objects and creates one Bean per object.
    * These beans store information (ac, shortlabel, fullname) about the object and are all added into a collection
    * @return collection containing all CVBeans (one per object)
    * @throws IntactException
    */
    public Collection initCVInteractionList() throws IntactException {
        Collection databases = null;
        this.CVInteraction = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean(null, "-no CvInteraction-", "no interaction selected");
        this.CVInteraction.add(emptyBean);
        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            databases = helper.search(CvInteraction.class.getName(), "ac", null);
            for (Iterator iterator = databases.iterator(); iterator.hasNext();) {
                CvInteraction cvInt = (CvInteraction) iterator.next();
                bean = new CvBean(cvInt.getAc(), cvInt.getShortLabel(), cvInt.getFullName());
                this.CVInteraction.add(bean);
            }

        } catch (IntactException e) {
            e.printStackTrace();
        } finally {
            helper.closeStore();
        }
        return this.CVInteraction;
    }



    /**
    * this method retrieves all CvTopic objects and creates one Bean per object.
    * These beans store information (ac, shortlabel, fullname) about the object and are all added into a collection
    * @return collection containing all CVBeans (one per object)
    * @throws IntactException
    */
    public Collection initCVInteractionTypeList() throws IntactException {
        Collection databases = null;
        this.CVInteractionType = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean(null, "-no CvInteractionType-", "no CvInteractionType selected");
        this.CVInteractionType.add(emptyBean);
        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            databases = helper.search(CvInteractionType.class.getName(), "ac", null);
            for (Iterator iterator = databases.iterator(); iterator.hasNext();) {
                CvInteractionType cvInt = (CvInteractionType) iterator.next();
                bean = new CvBean(cvInt.getAc(), cvInt.getShortLabel(), cvInt.getFullName());
                this.CVInteractionType.add(bean);
            }

        } catch (IntactException e) {
            e.printStackTrace();
        } finally {
            helper.closeStore();
        }
        return this.CVInteractionType;
    }


    /**
    * this method retrieves all CvIdentification objects and creates one Bean per object.
    * These beans store information (ac, shortlabel, fullname) about the object and are all added into a collection
    * @return collection containing all CVBeans (one per object)
    * @throws IntactException
    */
    public Collection initCVIdentificationList() throws IntactException {
        Collection databases = null;
        this.CVIdentification = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean(null, "-no CvIdentification-", "no identification selected");
        this.CVIdentification.add(emptyBean);
        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            databases = helper.search(CvIdentification.class.getName(), "ac", null);
            for (Iterator iterator = databases.iterator(); iterator.hasNext();) {
                CvIdentification cvInt = (CvIdentification) iterator.next();
                bean = new CvBean(cvInt.getAc(), cvInt.getShortLabel(), cvInt.getFullName());
                this.CVIdentification.add(bean);
            }

        } catch (IntactException e) {
            e.printStackTrace();
        } finally {
            helper.closeStore();
        }
        return this.CVIdentification;
    }

}


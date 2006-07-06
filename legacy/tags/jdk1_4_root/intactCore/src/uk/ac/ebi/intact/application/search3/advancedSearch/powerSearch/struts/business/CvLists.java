/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.business;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Query;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.view.bean.CvBean;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.AnnotationFilter;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.ObjectBridgeQueryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides method to retrieve the list of shortlabel for different CVs.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class CvLists {

    /**
     * Logger for that class.
     */
    private static Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

    // collection holding per CvDatabase object one CvBean
    private Collection cvDatabase = null;

    // collection holding per CvTopic object one CvBean
    private Collection cvTopic = null;

    // collection holding per CvInteraction object one CvBean
    private Collection cvInteraction = null;

    // collection holding per CvInteractionType object one CvBean
    private Collection cvInteractionType = null;

    // collection holding per CvIdentification object one CvBean
    private Collection cvIdentification = null;


    public CvLists() {
    }

    /**
     * Collects all objects of the specified type and filter out hidden and obsolete terms.
     * @param clazz the class we want to get all instances of.
     * @param list the list to populate.
     */
    public void addMenuListItem( Class clazz, Collection list ) {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            Query query = ObjectBridgeQueryFactory.getInstance().getMenuBuildQuery( clazz );
            Iterator it = helper.getIteratorByReportQuery( query );

            while ( it.hasNext() ) {
                Object[] o = (Object[]) it.next();

                String ac = (String) o[ 0 ];
                String shortlabel = (String) o[ 1 ];
                String fullname = (String) o[ 2 ];

                CvBean bean = new CvBean( ac, shortlabel, fullname );
                list.add( bean );
            }

        } catch ( IntactException e ) {
            logger.error("Error while loading CV terms", e);
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                    logger.error("Error while closing IntactHelper", e);
                }
            }
        }
    }

    /**
     * this method retrieves all CvDatabase objects and creates one Bean per object. These beans store information (ac,
     * shortlabel, fullname) about the object and are all added into a collection
     *
     * @return collection containing all CVBeans (one per object)
     *
     * @throws IntactException
     */
    public Collection initCVDatabaseList() throws IntactException {

        if( this.cvDatabase != null ){
            // use cache.
            return this.cvDatabase;
        }

        logger.info( "in initCVDatabaseList" );
        this.cvDatabase = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-all databases-", "all databases selected" );
        this.cvDatabase.add( emptyBean );

        addMenuListItem( CvDatabase.class, this.cvDatabase );

        return this.cvDatabase;
    }

    /**
     * this method retrieves all CvTopic objects and creates one Bean per object. These beans store information (ac,
     * shortlabel, fullname) about the object and are all added into a collection
     *
     * @return collection containing all CVBeans (one per object)
     *
     * @throws IntactException
     */
    public Collection initCVTopicList() throws IntactException {

        if( this.cvTopic != null ){
            // use cache.
            return this.cvTopic;
        }

        Collection topics = null;
        this.cvTopic = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-all topics-", "all topics selected" );
        this.cvTopic.add( emptyBean );

        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            topics = helper.search( CvTopic.class.getName(), "ac", null );
            for ( Iterator iterator = topics.iterator(); iterator.hasNext(); ) {
                CvTopic cvTo = (CvTopic) iterator.next();

                // remove 'no-export' CvTopic
                if( false == AnnotationFilter.getInstance().isFilteredOut( cvTo ) ) {

                    // do not insert the topic 'remark-interal', it should not be seen from outside
                    bean = new CvBean( cvTo.getAc(), cvTo.getShortLabel(), cvTo.getFullName() );
                    this.cvTopic.add( bean );
                }
            }

        } catch ( IntactException e ) {
            logger.error( "Error while loading CvTopics.", e );
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                    logger.error( "Error while closing helper.", e );
                }
            }
        }
        return this.cvTopic;
    }

    /**
     * this method retrieves all CvTopic objects and creates one Bean per object. These beans store information (ac,
     * shortlabel, fullname) about the object and are all added into a collection
     *
     * @return collection containing all CVBeans (one per object)
     *
     * @throws IntactException
     */
    public Collection initCVInteractionList() throws IntactException {

        if( this.cvInteraction != null ){
            // use cache.
            return this.cvInteraction;
        }

        this.cvInteraction = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvInteraction-", "no interaction selected" );
        this.cvInteraction.add( emptyBean );

        addMenuListItem( CvInteraction.class, this.cvInteraction );

        return this.cvInteraction;
    }

    /**
     * this method retrieves all CvTopic objects and creates one Bean per object. These beans store information (ac,
     * shortlabel, fullname) about the object and are all added into a collection
     *
     * @return collection containing all CVBeans (one per object)
     *
     * @throws IntactException
     */
    public Collection initCVInteractionTypeList() throws IntactException {

        if( this.cvInteractionType != null ){
            // use cache.
            return this.cvInteractionType;
        }

        this.cvInteractionType = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvInteractionType-", "no CvInteractionType selected" );
        this.cvInteractionType.add( emptyBean );

        addMenuListItem( CvInteractionType.class, this.cvInteractionType );

        return this.cvInteractionType;
    }

    /**
     * this method retrieves all CvIdentification objects and creates one Bean per object. These beans store information
     * (ac, shortlabel, fullname) about the object and are all added into a collection
     *
     * @return collection containing all CVBeans (one per object)
     *
     * @throws IntactException
     */
    public Collection initCVIdentificationList() throws IntactException {

        if( this.cvIdentification != null ){
            // use cache.
            return this.cvIdentification;
        }

        this.cvIdentification = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvIdentification-", "no identification selected" );
        this.cvIdentification.add( emptyBean );

        addMenuListItem( CvIdentification.class, this.cvIdentification );

        return this.cvIdentification;
    }
}
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
 * This class provides method to retrieve the list of shortlabel for different CVs
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class CvLists {

    private static Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

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

    private void addMenuListItem( Class clazz, Collection list ) {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            Query query = ObjectBridgeQueryFactory.getInstance().getMenuBuildQuery( clazz );
            Iterator i = helper.getIteratorByReportQuery( query );

            while ( i.hasNext() ) {
                Object[] o = (Object[]) i.next();

                String ac = (String) o[ 0 ];
                String shortlabel = (String) o[ 1 ];
                String fullname = (String) o[ 2 ];

                CvBean bean = new CvBean( ac, shortlabel, fullname );
                list.add( bean );
            }

        } catch ( IntactException e ) {
            e.printStackTrace();
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                    e.printStackTrace();
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
        logger.info( "in initCVDatabaseList" );
        this.CVDatabase = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-all databases-", "all databases selected" );
        this.CVDatabase.add( emptyBean );

        addMenuListItem( CvDatabase.class, this.CVDatabase );

        return this.CVDatabase;
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
        Collection topics = null;
        this.CVTopic = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-all topics-", "all topics selected" );
        this.CVTopic.add( emptyBean );

        CvBean bean;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            topics = helper.search( CvTopic.class.getName(), "ac", null );
            for ( Iterator iterator = topics.iterator(); iterator.hasNext(); ) {
                CvTopic cvTo = (CvTopic) iterator.next();

                if( false == AnnotationFilter.getInstance().isFilteredOut( cvTo ) ) {

                    // do not insert the topic 'remark-interal', it should not be seen from outside
                    bean = new CvBean( cvTo.getAc(), cvTo.getShortLabel(), cvTo.getFullName() );
                    this.CVTopic.add( bean );
                }
            }

        } catch ( IntactException e ) {
            e.printStackTrace();
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                }
            }
        }
        return this.CVTopic;
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
        this.CVInteraction = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvInteraction-", "no interaction selected" );
        this.CVInteraction.add( emptyBean );

        addMenuListItem( CvInteraction.class, this.CVInteraction );

        return this.CVInteraction;
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
        this.CVInteractionType = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvInteractionType-", "no CvInteractionType selected" );
        this.CVInteractionType.add( emptyBean );

        addMenuListItem( CvInteractionType.class, this.CVInteractionType );

        return this.CVInteractionType;
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
        this.CVIdentification = new ArrayList();
        // add an empty CV bean for the default case
        CvBean emptyBean = new CvBean( null, "-no CvIdentification-", "no identification selected" );
        this.CVIdentification.add( emptyBean );

        addMenuListItem( CvIdentification.class, this.CVIdentification );

        return this.CVIdentification;
    }
}
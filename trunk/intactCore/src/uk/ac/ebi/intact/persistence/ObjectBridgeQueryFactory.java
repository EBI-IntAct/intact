/*
 Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;

/**
 * This factory class builds common queries for IntAct.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ObjectBridgeQueryFactory {

    /** Only instance of this class */
    private static final ObjectBridgeQueryFactory OUR_INSTANCE = new ObjectBridgeQueryFactory();

    /**
     * @return returns the only instance of this class.
     */
    public static final ObjectBridgeQueryFactory getInstance() {
        return OUR_INSTANCE;
    }

    /**
     * Returns a query to search for given parameters
     * @param clazz the class to search. Eg., CvTopic.class
     * @param param ac or shortlabel
     * @param value value for search. Eg., abc*
     * @return the query.
     */
    public Query getLikeQuery(Class clazz, String param, String value) {
        // Replace * with % for SQL
        String sqlValue = value.replaceAll("\\*", "%");

        Criteria crit = new Criteria();
        crit.addLike(param, sqlValue);
        return QueryFactory.newQuery(clazz, crit);
    }


    /**
     * Returns a query to build menus. The obsolete terms are not included.
     * @param clazz the class to construct menus. Eg., CvTopic.class
     * @return a query to build menus. The menus are sorted in ascending order.
     */
    public Query getMenuBuildQuery(Class clazz) {
        Criteria crit = new Criteria();
        // Need all records for given class.
        crit.addLike("ac", "%");

        // Filter out obsolete items
        crit.addNotIn("ac", getObsoleteQuery(clazz));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, crit);
        // Limit to ac, shortlabel and fullname
        query.setAttributes(new String[] { "ac", "shortlabel", "fullname" });
        query.addOrderByAscending("shortLabel");
        return query;
    }

    // Helper Methods

    /**
     * Returns a query to get a list of obsolete ACs
     * @param clazz the returned oboslete terms are related to this class.
     * @return a list of obsolete ACs
     */
    private static Query getObsoleteQuery(Class clazz) {
        Criteria crit = new Criteria();
        // Need all records for given class.
        crit.addLike("ac", "%");

        // We only need obsolete items
        Criteria subcrit = new Criteria();
        subcrit.addEqualTo("annotations.cvTopic.shortLabel", "obsolete term");

        // Combine with the sub criteria
        crit.addAndCriteria(subcrit);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, crit);
        // Limit to shortlabel
        query.setAttributes(new String[] { "ac" });
        return query;
    }
}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Query;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.ObjectBridgeQueryFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Performs an intelligent search on the intact database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class SearchHelper implements SearchHelperI {

    /**
     * The Logger
     */
    private transient Logger logger;

    /**
     * The table for the initial search request in the searchFast method
     */
    private final static String SEARCH_TABLE = "ia_search";

    /**
     * Collection of CriteriaBean
     */
    private Collection searchCriteria = new ArrayList();

    /**
     * Boolean to check if the database is connected
     */
    private Boolean connected;

    /**
     * Create a search helper for which all the log message will be written by the provided logger.
     *
     * @param logger the user's logger.
     */
    public SearchHelper(Logger logger) {
        this.logger = logger;
    }

    public Collection getSearchCritera() {
        return searchCriteria;
    }

    public Collection doLookup(String searchClass, String values, IntactUserI user)
            throws IntactException {

        searchCriteria.clear();
        Collection queries = splitQuery(values);

        // avoid to have duplicate intact object in the dataset.
        Collection results = new HashSet();
        String packageName = AnnotatedObject.class.getPackage().getName() + ".";

        logger.info("className supplied in request - going straight to search...");
        String className = packageName + searchClass;
        logger.info("attempting search for " + className + " with values " + values);
        for (Iterator iterator = queries.iterator(); iterator.hasNext();) {
            String subQuery = (String) iterator.next();
            logger.info("Search for subquery: " + subQuery);
            Collection subResult = doSearch(className, subQuery, user);

            if (subResult.isEmpty()) {
                logger.info("no search results found for class: " + className + ", value: " + values);
            }
            else {
                logger.info("found search match - class: " + className + ", value: " + values);
            }

            results.addAll(subResult);
            logger.info("Item count: " + results.size());
        }

        return results;
    }

    public Collection doLookup(List searchClasses, String values, IntactUserI user)
            throws IntactException {

        searchCriteria.clear();
        Collection queries = splitQuery(values);

        // avoid to have duplicate intact object in the dataset.
        Collection results = new HashSet();
        String packageName = AnnotatedObject.class.getPackage().getName() + ".";

        String className = null;
        boolean itemFound = false;
        for (Iterator iterator = queries.iterator(); iterator.hasNext();) {
            String subQuery = (String) iterator.next();
            logger.info("Search for subquery: " + subQuery);

            final int size = searchClasses.size();
            for (int i = 0; i < size; i++) {
                if (false == itemFound) {
                    className = packageName + searchClasses.get(i);
                }
                else {
                    // if there is an item found (i.e. only one class to look for)
                    // we need only one iteration.
                    if (i > 0) break;
                }

                Collection subResult = doSearch(className, subQuery, user);
                logger.info("sub result count: " + subResult.size());

                if (subResult.size() > 0) {
                    results.addAll(subResult);
                    className = subResult.iterator().next().getClass().getName();
                    logger.info("found search match - class: " + className + ", value: " + subQuery);
                    itemFound = true;
                    break; // exit the inner for
                }
            } // inner for
            logger.debug("total result count: " + results.size());
        } // main for

        return results;
    }

    public ResultWrapper searchByQuery(Class clazz, String searchParam,
                                       String searchValue, int max) throws IntactException {
        // The helper to run the query against.
        IntactHelper helper = null;

        // The query factory to get a query.
        ObjectBridgeQueryFactory qf = ObjectBridgeQueryFactory.getInstance();

        // The query to search for AC or shortlabel.
        Query query = qf.getLikeQuery(clazz, searchParam, searchValue);
        try {
            helper = new IntactHelper();
            int count = helper.getCountByQuery(query);
            if (count > max) {
                // Exceeds the maximum size.
                logger.info("return empty resultwrapper");
                return new ResultWrapper(count, max);
            }
            // We have a result which is within limits. Do the search.
            Collection searchResults = helper.getCollectionByQuery(query);
            if (searchResults.isEmpty()) {
                return new ResultWrapper(0, max);
            }
            else {
                return new ResultWrapper(searchResults, max);
            }
        }
        finally {
            if (helper != null) {
                helper.closeStore();
            }
        }
    }

    public ResultWrapper searchByQuery(Query[] queries, int max) throws IntactException {
        // The count returned by the query.
        Object rowCount;

        // The actual search count.
        int count = 0;

        // The helper to run the query against.
        IntactHelper helper = new IntactHelper();

        try {
            Iterator iter0 = helper.getIteratorByReportQuery(queries[0]);
            rowCount = ((Object[]) iter0.next())[0];

            // Check for oracle
            if (rowCount.getClass().isAssignableFrom(BigDecimal.class)) {
                count =  ((BigDecimal) rowCount).intValue();
            }
            else {
                // postgres driver returns Long. Could be a problem for another DB
                // This may throw a classcast exception.
                count =  ((Long) rowCount).intValue();
            }
            if ((count > 0) && (count <= max)) {
                // Not empty and within the max limits. Do the search
                // The result collection to set.
                List results = new ArrayList();
                for (Iterator iter = helper.getIteratorByReportQuery(queries[1]); iter.hasNext();) {
                    results.add(iter.next());
                }
                return new ResultWrapper(results, max);
            }
        }
        finally {
            if (helper != null) {
                helper.closeStore();
            }
        }
        // Either too large or none found (empty search).
        return new ResultWrapper(count, max);
    }


    /**
     * Split the query string. It generated one sub query by comma separated parameter. e.g.
     * {a,b,c,d} will gives {{a}, {b}, {c}, {d}}
     *
     * @param query the query string to split
     * @return one to many subquery of the comma separated list.
     */
    private Collection splitQuery(String query) {
        Collection queries = new LinkedList();

        StringTokenizer st = new StringTokenizer(query, ",");
        while (st.hasMoreTokens()) {
            queries.add(st.nextToken().trim());
        }

        return queries;
    }

    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc. Isolating it here
     * allows us to change initial strategy if we want to. NB this will probably be refactored out
     * into the IntactHelper class later on.
     *
     * @param className The class to search on (only comes from a link clink) - useful for
     *                  optimizing search
     * @param value     the user-specified value
     * @param user      object holding the IntactHelper for a given user/session (passed as a
     *                  parameter to avoid using an instance variable, which may cause thread
     *                  problems).
     * @return Collection the results of the search - an empty Collection if no results found
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
    private Collection doSearch(String className, String value, IntactUserI user)
            throws IntactException {
        //try search on AC first...
        Collection results = user.search(className, "ac", value);
        String currentCriteria = "ac";

        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            logger.info("no match found for " + className + " with ac= " + value);
            logger.info("now searching for class " + className + " with label " + value);
            results = user.search(className, "shortLabel", value);
            currentCriteria = "shortLabel";

            if (results.isEmpty()) {
                //no match on label - try by alias.
                logger.info("no match on label - looking for: " + className +
                            " with name alias ID " +
                            value);
                Collection aliases = user.search(Alias.class.getName(), "name",
                                                 value.toLowerCase());

                //could get more than one alias, eg if the name is a wildcard search value -
                //then need to go through each alias found and accumulate the results...
                for (Iterator it = aliases.iterator(); it.hasNext();) {
                    results.addAll(user.search(className, "ac", ((Alias) it.next()).getParentAc()));
                }
                currentCriteria = "alias";
            }

            if (results.isEmpty()) {
                //no match on label - try by xref....
                logger.info("no match on label - looking for: " + className +
                            " with primary xref ID " +
                            value);
                Collection xrefs = user.search(Xref.class.getName(), "primaryId", value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                for (Iterator it = xrefs.iterator(); it.hasNext();) {
                    results.addAll(user.search(className, "ac", ((Xref) it.next()).getParentAc()));
                }
                currentCriteria = "xref";

                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
                    logger.info("no matches found using ac, shortlabel or xref - trying fullname...");
                    results = user.search(className, "fullName", value);
                    currentCriteria = "fullName";

                    if (results.isEmpty()) {
                        //no match by xref - try finally by name....
                        logger.info("no matches found using ac, shortlabel, xref or fullname... give up.");
                        currentCriteria = null;
                    }
                }
            }
        }

        CriteriaBean cb = new CriteriaBean(value, currentCriteria);
        searchCriteria.add(cb);
        return results;
    }  // doSearch

    /**
     * utility method to handle the logic for a simple lookup, ie trying AC and label only.
     *
     * @param clazz The class to search on.
     * @param value the user-specified value
     * @return the result wrapper which contains the result of the search
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
//    private ResultWrapper doSearchSimple(Class clazz, String value, int max)
//            throws IntactException {
//        //try search on AC first...
//        ResultWrapper rw = searchByQuery(clazz, "ac", value, max);
//        String currentCriteria = "ac";
//
//        if (rw.isEmpty()) {
//            // No matches found - try a search by label now...
//            logger.info("no match found for " + clazz + " with ac= " + value);
//            logger.info("now searching for class " + clazz + " with label " + value);
//            rw = searchByQuery(clazz, "shortLabel", value, max);
//            currentCriteria = "shortLabel";
//        }
//        CriteriaBean cb = new CriteriaBean(value, currentCriteria);
//        searchCriteria.add(cb);
//        return rw;
//    }

    /**
     * Returns true  if a simple count on the ia_search table works, if not false
     *
     * @return true if a simple count on the ia_search table works, if not return false
     */
    public boolean connected() {
        logger.info("check if is connected");
        //set up with first call
        if (connected == null) {
            final String testQuery = "SELECT COUNT(*) FROM " + SEARCH_TABLE;
            IntactHelper helper = null;
            ResultSet rs = null;
            Statement stmt = null;
            try {
                helper = new IntactHelper();
                final Connection conn = helper.getJDBCConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(testQuery);
                // if we got a result, the search table exists
                if (rs.next()) {
                    connected = new Boolean(true);
                }
                // if we got no result,the search table exists
                else {
                    connected = new Boolean(false);
                }
                //  an exception means that something is wrong
            }
            catch (IntactException e) {
                connected = new Boolean(false);
            }
            catch (SQLException e) {
                connected = new Boolean(false);
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (SQLException e) {
                        // what i should do here ?
                        // throw an exception makes no sense
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException e) {
                        // what i should do here ?
                        // throw an exception makes no sense
                    }
                }

                if (helper != null) {
                    try {
                        helper.closeStore();
                    }
                    catch (IntactException e) {
                        // what i should do here ?
                        // throw an exception makes no sense
                    }
                }
            }
        }
        return connected.booleanValue();
    }

    /**
     * This Method provides a full index search on the ia_search table and returns a ResultWrapper which contains
     * the Object which fits by the query. If the result size is too large, you will get back an empty resultwrapper
     * which contains a statistic for the results. This method is private and only for internal usage. it should
     * replaced as soon as possible with a lucene based solution.
     *
     * @param query       the user-specified value
     * @param searchClass String which represents the name of the class  to search on.
     * @param type        String  the filter type (ac, shortlabel, xref etc.) if type is null it will be 'all'
     * @param user        f uk.ac.ebi.intact.application.commons.business.IntactUserI for getting the IntactHelper
     * @return the result wrapper which contains the result of the search
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
    private ResultWrapper search(String query, String searchClass, String type, IntactHelper helper, int maximumResultSize)
            throws IntactException {

        // first check if we got a type, we have to search for a type if the type is not null
        // and not "all"
        boolean hasType = (type != null) && (!type.trim().equals("")) && !type.equals("all");
        boolean hasSearchClass = (searchClass != null) && (!searchClass.trim().equals(""));

        // now check for the search Class



        logger.info("search with value with query : " + query + " searchClass :" + searchClass);
        // replace  the "*" with "%"

        String sqlValue = query.replaceAll("\\*", "%");
        sqlValue = sqlValue.replaceAll("\\'", "");
        sqlValue = sqlValue.toLowerCase();
        logger.info(sqlValue);

        // if we want to search for CvObjects, it's not referenced as such in ia_search,
        // but as its implementation (eg. CvTopic).

        if (searchClass.equalsIgnoreCase("cvobject")) {
            searchClass = "Cv";
        }

        // split the query
        Collection someSearchValues = this.splitQuery(sqlValue);

        // create the tail of the sql query
        StringBuffer sb = new StringBuffer(128);
        String value = null;

        for (Iterator iterator = someSearchValues.iterator(); iterator.hasNext();) {
            value = (String) iterator.next();
            sb.append("(value LIKE '");
            sb.append(value);
            sb.append("')");
            if (iterator.hasNext()) {
                sb.append(" OR ");
            }  // end if
        } // end for

        String sqlCount = "SELECT COUNT(distinct(ac)), objclass FROM " + SEARCH_TABLE + " WHERE " +
                sb.toString();

        // if we are looking for a type expand the query
        if (hasType) {
            sqlCount = sqlCount + " AND type='" + type + "'";
        }
        // if we are looking for a specific searchclass exapnd the query
        if (hasSearchClass) {
            sqlCount = sqlCount + " AND objclass LIKE '%" + searchClass + "%'";
        }

        sqlCount = sqlCount + " GROUP BY objclass";

        logger.info(sqlCount);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        // Access the Connection via the helper.
        try {

            conn = helper.getJDBCConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlCount);

            int count = 0;
            String className = null;
            Map resultInfo = new HashMap();

            while (rs.next()) {
                int classCount = rs.getInt(1);
                logger.info("classCount " + classCount);
                className = rs.getString(2);
                logger.info("ClassName : " + className);
                resultInfo.put(className, new Integer(classCount));
                count += classCount;
                logger.info("Count summ : " + count);
            }

            if (rs != null) {
                rs.close();
                rs = null;
            }
            logger.info("Count = " + count);
            // check the result size if the result is too large return an empty ResultWrapper
            if (count > maximumResultSize) {
                logger.info("Result too Large return an empty result Wrapper");
                return new ResultWrapper(count, maximumResultSize, resultInfo);
            }

            // we got an result, and it's in the limit, so now we need the ac's
            String sql = "SELECT distinct(ac), objclass from " + SEARCH_TABLE + " where " +
                    sb.toString();

            if (hasType) {
                sql = sql + " and type='" + type + "'";
            }

            if (hasSearchClass) {
                sql = sql + "AND objclass LIKE " + "'%" + searchClass + "%'";
            }


            logger.info(sql);
            //stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // get the result from the resultset and query via the intacthelper the objects and
            // put the data in a ResultWrapper

            String ac = null;
            className = null;
            Class clazz = null;
            ArrayList searchResult = new ArrayList(count);

            while (rs.next()) {
                ac = rs.getString(1);
                className = rs.getString(2);
                clazz = Class.forName(className);
                searchResult.add(helper.getObjectByAc(clazz, ac));
            }
            return new ResultWrapper(searchResult, maximumResultSize, resultInfo);

        }
        catch (SQLException se) {
            while ((se.getNextException()) != null) {
                logger.info(se.getSQLState());
                logger.info("SQL: Error Code: " + se.getErrorCode());
            }
            throw new IntactException("SQL errors, see the log out for more info ",se);

        }
        catch (ClassNotFoundException e) {
            throw new IntactException("Received an intact typ which is not valid, see the log out for more info ",e);
        }
        finally {
            //  close all database connections
            try {

                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException se) {
                throw new IntactException("Problems with closing the JDBC Resource ",se);
            }
        }
    }   // end searchFast

    /**
     * Workaround to provide an Interactor search with the ia_search table.
     *
     * @param searchValue the user-specified search value
     * @param type        type String  the filter type (ac, shortlabel, xref etc.) if type is null it will be 'all'
     * @param helper      user f uk.ac.ebi.intact.application.commons.business.IntactUserI for getting the IntactHelper
     * @return the result wrapper which contains the result of the search
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
    private ResultWrapper getInteractors(final String searchValue, String type, IntactHelper helper, int numberOfResults)
            throws IntactException {

        logger.info("search Interactor");

        // getting all results for proteins and interactions
        ResultWrapper proteins = this.search(searchValue, "Protein", type, helper, numberOfResults);
        ResultWrapper interactions = this.search(searchValue, "Interaction", type, helper, numberOfResults);

        // now check whats going on with the results and calculate the summ of both
        if (proteins.isTooLarge() || interactions.isTooLarge()) {

            logger.info("Search Helper: getInteractors method, result too large");
            int count = 0;
            int proteinCount = 0;
            int interactionCount = 0;
            Map resultInfo = new HashMap();

            // the result is too large just getting the info a return an empty resultwrapper
            if (proteins.isTooLarge()) {
                Map proteinInfo = proteins.getInfo();
                proteinCount =
                        ((Integer) proteinInfo.get("uk.ac.ebi.intact.model.ProteinImpl")).intValue();
                resultInfo.put("uk.ac.ebi.intact.model.ProteinImpl", new Integer(proteinCount));
            }

            if (interactions.isTooLarge()) {
                Map interactionInfo = interactions.getInfo();
                interactionCount = ((Integer) interactionInfo.get("uk.ac.ebi.intact.model.InteractionImpl")).intValue();
                resultInfo.put("uk.ac.ebi.intact.model.InteractionImpl",
                               new Integer(interactionCount));
            }

            count = proteinCount + interactionCount;

            // create the info

            logger.info("return empty resultwrapper");

            return new ResultWrapper(count, numberOfResults, resultInfo);

        }
        else {
            // we are in the limit, add everything to a new resultwrapper
            Collection temp = new ArrayList(proteins.getResult().size() + interactions.getResult().size());
            temp.addAll(proteins.getResult());
            temp.addAll(interactions.getResult());
            return new ResultWrapper(temp, numberOfResults);
        }
    }

    /**
     * This Method provides a full index search on the ia_search table and returns a ResultWrapper which contains
     * the object which fits by the query. If the result size is too large, you will get back an empty
     * uk.ac.ebi.intact.application.commons.search.ResultWrapper which contains a statistic for the results
     *
     * @param query  the user-specified search value
     * @param type   String the filter type (ac, shortlabel, xref etc.) if type is null it will be 'all'
     * @param helper user f uk.ac.ebi.intact.application.commons.business.IntactUserI for getting the IntactHelper
     * @return the result wrapper which contains the result of the search
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
    public ResultWrapper searchFast(String query, String searchClass, String type,
                                    IntactHelper helper, int numberOfResults)
            throws IntactException {

        if (searchClass.equalsIgnoreCase("Interactor")) {
            return this.getInteractors(query, type, helper, numberOfResults);
        }
        else {
            return this.search(query, searchClass, type, helper, numberOfResults);
        }
    }

}
/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Xref;

import java.sql.*;
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

    public Collection doLookupSimple(String searchClass, String query,
                                     IntactUserI user) throws IntactException {
        searchCriteria.clear();
        String packageName = AnnotatedObject.class.getPackage().getName() + ".";

        logger.info("className supplied in request - going straight to search...");
        String className = packageName + searchClass;
        logger.info("attempting search for " + className + " with query " + query);

        Collection results = doSearchSimple(className, query, user);

        if (results.isEmpty()) {
            logger.info("no search results found for class: " + className + ", query: " + query);
        } else {
            logger.info("found search match - class: " + className + ", value: " + query);
        }
        logger.info("Item count: " + results.size());

        return results;
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
            } else {
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
                } else {
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

    public ResultWrapper search(Class clazz, String searchParam,
                                String searchValue, int max) throws IntactException {
        IntactHelper helper = new IntactHelper();

        // Replace * with % for SQL
        String sqlValue = searchValue.replaceAll("\\*", "%");
        logger.info("search for class " + clazz.getName());
        String tableName = helper.getTableName(clazz);
        logger.info("table name =" + tableName);

        if (tableName == null) {
            throw new IntactException("Unable to find a table for class" + clazz.getName());
        }

        // The SQL expression to retrieve records.
        String sql = "SELECT COUNT(*) from " + tableName + " where " + searchParam + " LIKE '"
                + sqlValue + "'";

        logger.info(sql);

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement labelStmt = null;
        ResultSet rs = null;
        try {
            // Access the Connection via the helper.
            conn = helper.getJDBCConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                throw new IntactException("Unable to get number of possible records for the query");
            }
            int count = rs.getInt(1);
            logger.info("Count : " + count);
            if (count > max) {
                // Exceeds the maximum size.
                logger.info("return empty resultwrapper");
                return new ResultWrapper(count, max);
            }

            // We have a result which is within limits. Do the search.
            Collection searchResults = helper.search(clazz, searchParam, searchValue);
            if (searchResults.isEmpty()) {
                return new ResultWrapper(0, max);
            } else {
                return new ResultWrapper(searchResults, max);
            }
        } catch (SQLException se) {
            while ((se.getNextException()) != null) {
                logger.info(se.getSQLState());
                logger.info("SQL: Error Code: " + se.getErrorCode());
            }
            throw new IntactException("SQL errors, see the log out for more info");
        } finally {
            helper.closeStore();
            try {
                rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (labelStmt != null) {
                    labelStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                throw new IntactException("SQL errors, see the log out for more info");
            }
        }
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
     * @param className The class to search on.
     * @param value     the user-specified value
     * @param user      The object holding the IntactHelper for a given user/session (passed as a
     *                  parameter to avoid using an instance variable, which may cause thread
     *                  problems).
     * @return Collection the results of the search - an empty Collection if no results found
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there were any search problems
     */
    private Collection doSearchSimple(String className, String value,
                                      IntactUserI user) throws IntactException {
        //try search on AC first...
        Collection results = user.search(className, "ac", value);
        String currentCriteria = "ac";

        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            logger.info("no match found for " + className + " with ac= " + value);
            logger.info("now searching for class " + className + " with label " + value);
            results = user.search(className, "shortLabel", value);
            currentCriteria = "shortLabel";
        }
        CriteriaBean cb = new CriteriaBean(value, currentCriteria);
        searchCriteria.add(cb);
        return results;
    }


    /**
     * This method is only used in the initial request from the Search Application. You enter with a
     * query, It uses the ia_search table for retrieving the results, if the resultset is too large,
     * the ResultWrapper contains no data.
     *
     * @param query is a string with holds the search value
     * @return ResultWrapper ResultWrapper with the result in it. if the resultset is too large,
     *         the ResultWrapper contains no data.
     * @throws IntactException
     */
    public ResultWrapper searchFast(String query) throws IntactException {

        // replace  the "*" with "%"
        String sqlValue = query.replaceAll("\\*", "%");
        sqlValue.toLowerCase();

        // split the query
        Collection someSearchValues = this.splitQuery(sqlValue);

        // create the tail of the sql query
        StringBuffer sb = new StringBuffer();
        String value = null;

        for (Iterator iterator = someSearchValues.iterator(); iterator.hasNext();) {
            value = (String) iterator.next();
            sb.append("value LIKE '");
            sb.append(value);
            sb.append("'");
            sb.append(" OR ac LIKE '");
            sb.append(value.toUpperCase());
            sb.append("'");
            if (iterator.hasNext()) {
                sb.append(" OR ");
            }  // end if
        } // end for

        IntactHelper helper = new IntactHelper();
        String sqlCount = "SELECT COUNT(distinct(ac)), objclass from " + SEARCH_TABLE + " where " +
                sb.toString() +
                " GROUP BY objclass";

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement labelStmt = null;
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
                className = rs.getString(2);
                resultInfo.put(className, new Integer(classCount));
                count += classCount;
            }

            // check the result size if the result is too large return an empty ResultWrapper
            if (count > SearchConstants.MAXIMUM_RESULT_SIZE) {
                return new ResultWrapper(count, SearchConstants.MAXIMUM_RESULT_SIZE, resultInfo);
            }

            // we got an result, and it's in the limit, so now we need the ac's
            String sql = "SELECT distinct(ac), objclass from " + SEARCH_TABLE + " where " +
                    sb.toString();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // get the result from the resultset and query via the intacthelper the objects and
            // put the data in a ResultWrapper

            String ac = null;
            className = null;
            Class clazz = null;
            ArrayList searchResult = new ArrayList();

            while (rs.next()) {
                ac = rs.getString(1);
                className = rs.getString(2);
                clazz = Class.forName(className);
                searchResult.add(helper.getObjectByAc(clazz, ac));
            }

            return new ResultWrapper(searchResult, SearchConstants.MAXIMUM_RESULT_SIZE, resultInfo);

        } catch (SQLException se) {
            while ((se.getNextException()) != null) {
                logger.info(se.getSQLState());
                logger.info("SQL: Error Code: " + se.getErrorCode());
            }
            throw new IntactException("SQL errors, see the log out for more info");

        } catch (ClassNotFoundException e) {
            throw new IntactException("Recieved an intact typ which is not valid, see the log out for more info");

        } catch (NullPointerException e) {
            throw new IntactException("Problems with the resultset, see the log out for more info");

        } finally {
            //  close all database connections
            helper.closeStore();
            try {
                rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (labelStmt != null) {
                    labelStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                throw new IntactException("Problems with closing the IntactHelper");
            } catch (NullPointerException e) {
                throw new IntactException("Problems with closing the IntactHelper");
            }
        }
    }   // end searchFast

    public ResultWrapper searchFast(String query, String searchClass) throws IntactException {


        // replace  the "*" with "%"
        String sqlValue = query.replaceAll("\\*", "%");
        sqlValue.toLowerCase();

        // split the query
        Collection someSearchValues = this.splitQuery(sqlValue);

        // create the tail of the sql query
        StringBuffer sb = new StringBuffer();
        String value = null;

        for (Iterator iterator = someSearchValues.iterator(); iterator.hasNext();) {
            value = (String) iterator.next();
            sb.append("(value LIKE '");
            sb.append(value);
            sb.append("'");
            sb.append(" OR ac LIKE '");
            sb.append(value.toUpperCase());
            sb.append("')");
            if (iterator.hasNext()) {
                sb.append(" OR ");
            }  // end if
        } // end for

        IntactHelper helper = new IntactHelper();

        String sqlCount = "SELECT COUNT(distinct(ac)), objclass from " + SEARCH_TABLE + " where " +
                sb.toString() +
                " and objclass LIKE '%" + searchClass + "%'" + " group by objclass";


        Connection conn = null;
        Statement stmt = null;
        PreparedStatement labelStmt = null;
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
                className = rs.getString(2);
                resultInfo.put(className, new Integer(classCount));
                count += classCount;
            }

            // check the result size if the result is too large return an empty ResultWrapper
            if (count > SearchConstants.MAXIMUM_RESULT_SIZE) {
                return new ResultWrapper(count, SearchConstants.MAXIMUM_RESULT_SIZE, resultInfo);
            }

            // we got an result, and it's in the limit, so now we need the ac's
            String sql = "SELECT distinct(ac), objclass from " + SEARCH_TABLE + " where " +
                    sb.toString() + "AND objclass LIKE " + "'%" + searchClass + "%'";


            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // get the result from the resultset and query via the intacthelper the objects and
            // put the data in a ResultWrapper

            String ac = null;
            className = null;
            Class clazz = null;
            ArrayList searchResult = new ArrayList();

            while (rs.next()) {
                ac = rs.getString(1);
                className = rs.getString(2);
                clazz = Class.forName(className);
                searchResult.add(helper.getObjectByAc(clazz, ac));
            }
            return new ResultWrapper(searchResult, SearchConstants.MAXIMUM_RESULT_SIZE, resultInfo);

        } catch (SQLException se) {
            while ((se.getNextException()) != null) {
                logger.info(se.getSQLState());
                logger.info("SQL: Error Code: " + se.getErrorCode());
            }
            throw new IntactException("SQL errors, see the log out for more info");

        } catch (ClassNotFoundException e) {
            throw new IntactException("Recieved an intact typ which is not valid, see the log out for more info");

        } catch (NullPointerException e) {
            throw new IntactException("Problems with the resultset, see the log out for more info");

        } finally {
            //  close all database connections
            helper.closeStore();
            try {
                rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (labelStmt != null) {
                    labelStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                throw new IntactException("Problems with closing the IntactHelper");
            } catch (NullPointerException e) {
                throw new IntactException("Problems with closing the IntactHelper");
            }
        }
    }   // end searchFast
}
 
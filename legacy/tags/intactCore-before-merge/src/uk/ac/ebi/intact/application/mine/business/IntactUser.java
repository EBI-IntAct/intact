/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.business;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;

import uk.ac.ebi.intact.application.mine.business.graph.model.NodeObject;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

/**
 * @author Andreas Groscurth
 */
public class IntactUser implements IntactUserI {
	static transient Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

	private IntactHelper intactHelper;
	private Collection paths;
	private Collection singletons;
	private String search;

	/**
	 * Creates a new intact user
	 * 
	 * @throws IntactException if the initiation of the intacthelper failed
	 */
	public IntactUser() throws IntactException {
		intactHelper = new IntactHelper();
		paths = new HashSet();
		singletons = new HashSet();
		search = "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getPaths()
	 */
	public Collection getPaths() {
		return paths;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getIntactHelper()
	 */
	public IntactHelper getIntactHelper() {
		return intactHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		try {
			this.intactHelper.closeStore();
			logger.info(
				"IntactHelper datasource closed "
					+ "(cause: removing attribute from session)");
		} catch (IntactException ie) {
			//failed to close the store - not sure what to do here yet....
			logger.error("error when closing the IntactHelper store", ie);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#search(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Collection search(
		String objectType,
		String searchParam,
		String searchValue)
		throws IntactException {
		return intactHelper.search(objectType, searchParam, searchValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#getUserName()
	 */
	public String getUserName() {
		if (this.intactHelper != null) {
			try {
				return this.intactHelper.getDbUserName();
			} catch (LookupException e) {
			} catch (SQLException e) {
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#getDatabaseName()
	 */
	public String getDatabaseName() {
		if (this.intactHelper != null) {
            return this.intactHelper.getDbName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getDBConnection()
	 */
	public Connection getDBConnection() {
		try {
			return intactHelper.getJDBCConnection();
		} catch (IntactException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#addToPath(uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey,
	 *      java.util.Collection)
	 */
	public void addToPath(Collection path) {
		paths.add(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#clearPaths()
	 */
	public void clearAll() {
		if (null != paths) {
			paths.clear();
		}
		if (null != singletons) {
			singletons.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#addToSingletons()
	 */
	public void addToSingletons(Collection element) {
		singletons.addAll(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getSingletons()
	 */
	public Collection getSingletons() {
		return singletons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getHVLink()
	 */
	public String getHVLink(String contextPath) {
		StringBuffer link = new StringBuffer(256);
		link.append(contextPath);

		// the link to hierarchview is added according to the given string
		// in the properties file (e.g. hierarchView/display.jsp?AC=)
		link.append(HIERARCHVIEW_PROPERTIES.getProperty("hv.url"));

		// borders stores the borders of the different minimal connecting
		// networks. E.g. borders=2,4 means for the
		// network=EBI-1,EBI-2,EBI-3,EBI-4 that (EBI-1,EBI-2) is one network
		// and (EBI-3,EBI-4) is one network
		StringBuffer borders = new StringBuffer();
		int boundaries = 0;
		Collection path;

		for (Iterator it = paths.iterator(); it.hasNext();) {
			path = (Collection) it.next();
			// the current path is added to the link
			for (Iterator iter = path.iterator(); iter.hasNext();) {
				link.append(((NodeObject) iter.next()).getShortLabel());
				if (iter.hasNext()) {
					link.append(Constants.COMMA);
				}
			}
			// boundaries stores the sizes of the different networks
			// see comments above for details
			boundaries += path.size();
			borders.append(boundaries);
			if (it.hasNext()) {
				link.append(Constants.COMMA);
				borders.append(Constants.COMMA);
			}
		}
		// the boundaries of the different connecting networks are added to the
		// link
		link.append("&network=").append(borders);

		// if singletons are present they are added to the link
		if (!singletons.isEmpty()) {
			String sing = singletons.toString();
			sing = sing.substring(1, sing.length() - 1);

			link.append("&singletons=" + sing);
		}
		link.append("&method=").append(
			HIERARCHVIEW_PROPERTIES.get("hv.method"));
		link.append("&depth=").append(HIERARCHVIEW_PROPERTIES.get("hv.depth"));
		return link.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getSearch()
	 */
	public String getSearch() {
		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#setSearch(java.lang.String)
	 */
	public void setSearch(String s) {
		search = s;
	}
}
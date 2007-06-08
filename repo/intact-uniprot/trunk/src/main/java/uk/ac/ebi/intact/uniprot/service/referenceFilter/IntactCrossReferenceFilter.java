/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service.referenceFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Oct-2006</pre>
 */
public class IntactCrossReferenceFilter implements CrossReferenceFilter {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactCrossReferenceFilter.class );

    private Map<String,String> db2mi = new HashMap<String,String>();

    public IntactCrossReferenceFilter() {
        db2mi.put( format( "GO" ),"MI:0448" );
        db2mi.put( format( "InterPro" ),"MI:0449" );
        db2mi.put( format( "PDB" ),"MI:0806" );
        db2mi.put( format( "HUGE" ),"MI:0249" );
        db2mi.put( format( "SGD" ),"MI:0484" );
        db2mi.put( format( "FlyBase" ),"MI:0478" );
    }

    private String format( String s ) {
        if ( s == null ) {
            throw new IllegalArgumentException( "Database must not be null." );
        }

        s = s.trim();
        if ( "".equals( s ) ) {
            throw new IllegalArgumentException( "Database must be a non empty String." );
        }

        return s.toLowerCase();
    }

    /////////////////////////////////////
    // CrossReferenceSelector method

    public boolean isSelected( String database ) {
        return db2mi.containsKey( format( database ) );
    }

    public List<String> getFilteredDatabases() {
        // TODO test this
        Set set = db2mi.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> list = new ArrayList<String>();
        if(iterator.hasNext()){
            list.add(iterator.next());
        }
        return Collections.unmodifiableList( list );
    }

    /**
     * Return the mi corresponding to the given databaseName. If not found returns null.
     * @param databaseName a String in lower case. The different database names are go, interpro,
     * huge, pdb, sgd or flybase. In the case this doc. wouldn't be up to date, you can get the list of dabase names by
     * using the getFilteredDatabases() of this class.
     * @return a String represinting the psi-mi id of the given databaseName or null if not in found.
     */
    public String getMi(String databaseName){
        String mi = null;
        if(db2mi.containsKey(databaseName)){
            mi = db2mi.get(databaseName);
        }
        return mi;
    }

    public Map getDb2Mi(){
        return db2mi;
    }
}
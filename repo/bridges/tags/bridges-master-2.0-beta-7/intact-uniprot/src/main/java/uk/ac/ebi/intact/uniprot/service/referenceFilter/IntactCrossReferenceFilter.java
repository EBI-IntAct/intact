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

    /**
     * This list has been by Henning. They are database to which uniprot xref and that we want to have in IntAct.
     * Xref to go and interpro are needed as we have tools depending on those xref. Other xref are taken because they
     * might be an entry point for the a user to get in Intact. (A user could search in IntAct with and ac from sgd db).
     */
    public IntactCrossReferenceFilter() {

        db2mi.put( format( "cygd"), "MI:0464");
        db2mi.put( format( "ensembl"), "MI:0476");
        db2mi.put( format( "flybase"), "MI:0478");
        db2mi.put( format( "go" ), "MI:0448");
        db2mi.put( format( "interpro" ), "MI:0449");
        db2mi.put( format( "pdb" ), "MI:0460");
        db2mi.put( format( "sgd" ), "MI:0484");
        db2mi.put( format( "rgd"), "MI:0483");

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
        Set<String> keySet = db2mi.keySet();
        List<String> databases = new ArrayList();
        for ( String key : keySet ){
            databases.add(key);
        }
        // TODO test this
        return Collections.unmodifiableList( databases );
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

    public Map<String,String> getDb2Mi(){
        return db2mi;
    }
}
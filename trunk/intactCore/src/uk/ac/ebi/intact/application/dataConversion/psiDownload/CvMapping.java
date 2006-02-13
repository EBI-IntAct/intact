/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiDownload;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Xref;

import java.io.*;
import java.util.*;

/**
 * Holds a Controlled Vocabulary Mapping.
 * <p/>
 * The mapping is loaded from a file and the CvMapping is then queried in order to get a remapped CV.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Jun-2005</pre>
 */
public class CvMapping {

    public static final String FILENAME = "CvMapping.ser";

    public static final String TABULATION = "\t";
    public static final String STAR = "*";

    private Map map = new HashMap();

    public CvMapping() {
    }

    private String getPsiReference( CvObject cv ) {

        if ( cv == null ) {
            throw new IllegalArgumentException( "the given CvObject must not be null." );
        }

        for ( Iterator iterator = cv.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();

            if ( CvDatabase.PSI_MI.equals( xref.getCvDatabase().getShortLabel() ) ) {
                return xref.getPrimaryId();
            }
        }

        return cv.getShortLabel();
    }

    private void addMapping( Map map, CvObject from, CvObject to ) {

        String mapping = getPsiReference( from ) + " --> " + getPsiReference( to );
        if ( from.equals( to ) ) {
            System.out.println( "WARNING: skip unuseful mapping, " + mapping );
            return;
        }

        if ( map.containsKey( from ) ) {

            String existing = getPsiReference( from ) + " --> " + getPsiReference( (CvObject) map.get( from ) );
            System.err.println( "WARNING: mapping " + mapping + " is conflicting with " + existing );
            System.err.println( "Skip it." );

        } else {

            System.out.println( "ADD: " + mapping );
            map.put( from, to );
        }
    }

    /**
     * Load a tabulation separated flat file having the following format:
     * <pre>
     *         &lt;MI:1&gt;   &lt;shortalbel1&gt;   &lt;MI:2&gt; &lt;shortalbel2&gt;
     * <p/>
     * &lt;MI:1&gt;         : PSI MI reference of the PSI2 term
     * &lt;shortalbel1&gt;  : shortlabel of the PSI2 term (only for clarity)
     * &lt;MI:2&gt;         : PSI MI reference of the PSI1 term
     * &lt;shortalbel2&gt;  : shortlabel of the PSI1 term (only for clarity)
     * <p/>
     * notes:
     *         - all blank lines are skipped
     *         - all lines starting with # are considered as comments (hence skipped)
     *         - if &lt;MI:1&gt; is a star ('*') we load all the terms, picking the type based on &lt;MI:2&gt;'s
     *         - if &lt;MI:2&gt; is a star ('*') we load all the terms, picking the type based on &lt;MI:1&gt;'s
     * </pre>
     *
     * @param file
     * @param helper
     *
     * @return
     */
    public Map loadFile( File file, IntactHelper helper ) {

        map = new HashMap();

        File serializeFile = new File( file.getName() + ".ser" );

        try {
            if ( serializeFile.exists() ) {

                System.out.println( "Loading mapping from serialized cache (" + serializeFile.getAbsolutePath() + ")" );

                FileInputStream in = new FileInputStream( serializeFile );
                ObjectInputStream ois = new ObjectInputStream( in );

                Integer count = (Integer) ois.readObject();

                for ( int i = 0; i < count.intValue(); i++ ) {

                    CvObject from = (CvObject) ois.readObject();
                    CvObject to = (CvObject) ois.readObject();

                    map.put( from, to );
                }

                ois.close();

                System.out.println( map.size() + " association loaded." );
                return map;
            }
        } catch ( Exception e ) {
            map.clear(); // in case of error, clear the cache.
            e.printStackTrace();
        }

        try {
            BufferedReader in = new BufferedReader( new FileReader( file ) );
            String str;
            int lineCount = 0;
            while ( ( str = in.readLine() ) != null ) {

                lineCount++;

                str = str.trim();

                if ( "".equals( str ) || str.startsWith( "#" ) ) {
                    continue; // skip white line and comments (#)
                }

                StringTokenizer st = new StringTokenizer( str, TABULATION, true );

                String fromMI = null;
                String fromLabel = null;
                String toMI = null;
                String toLabel = null;

                if ( st.hasMoreTokens() ) {
                    fromMI = st.nextToken();
                    if ( fromMI.equals( TABULATION ) ) {
                        fromMI = null;
                    } else {
                        st.nextToken(); // skip next TABULATION
                    }
                    if ( st.hasMoreTokens() ) {
                        fromLabel = st.nextToken();
                        if ( fromLabel.equals( TABULATION ) ) {
                            fromLabel = null;
                        } else {
                            st.nextToken(); // skip next TABULATION
                        }
                        if ( st.hasMoreTokens() ) {
                            toMI = st.nextToken();
                            if ( toMI.equals( TABULATION ) ) {
                                toMI = null;
                            } else {
                                st.nextToken(); // skip next TABULATION
                            }
                            if ( st.hasMoreTokens() ) {
                                toLabel = st.nextToken();
                            }
                        }
                    }
                }

//                System.out.println( "from: " + fromMI );
//                System.out.println( "to: " + toMI );

                try {

                    if ( fromMI != null && toMI != null ) {
                        // either they are both MI, or one of them is MI and the orher a STAR.
                        if ( ( fromMI.startsWith( "MI:" ) || fromMI.equals( STAR ) )
                             && ( toMI.startsWith( "MI:" ) || toMI.equals( STAR ) )
                             && ( !( fromMI.equals( STAR ) && toMI.equals( STAR ) ) ) ) {

                            CvObject fromCvObject = null;
                            if ( fromMI.startsWith( "MI:" ) ) {
                                Collection c = helper.getObjectsByXref( CvObject.class, fromMI );

                                if ( c.size() == 1 ) {
                                    fromCvObject = (CvObject) c.iterator().next();
                                } else if ( c.size() > 1 ) {

                                    // get the class from the other MI ref, and filter using it instead of CvObject
                                    c = helper.getObjectsByXref( CvObject.class, toMI );
                                    if ( c.size() == 1 ) {
                                        // get the class
                                        CvObject cv = (CvObject) c.iterator().next();
                                        Class clazz = cv.getClass();

                                        // there should be only one.
                                        fromCvObject = (CvObject) helper.getObjectByXref( clazz, fromMI );
                                    }
                                }

                                if ( fromCvObject == null ) {
                                    System.err.println( "Line " + lineCount + ": Warning, " + fromMI + " could not be found in the database." );
                                    continue; // skip it
                                }
                            }

                            CvObject toCvObject = null;
                            if ( toMI.startsWith( "MI:" ) ) {
                                toCvObject = (CvObject) helper.getObjectByXref( CvObject.class, toMI );

                                if ( toCvObject == null ) {
                                    System.err.println( "Line " + lineCount + ": Warning, " + toMI + " could not be found in the database." );
                                    continue; // skip it
                                }
                            }

                            Collection fromCollection = null;
                            if ( fromCvObject == null ) {
                                // it was a star, hence check our the type of toCvObject
                                fromCollection = helper.search( toCvObject.getClass(), "ac", null ); // load them all

                                // remove evenutual cycle
                                fromCollection.remove( toCvObject );
                            }

                            Collection toCollection = null;
                            if ( toCvObject == null ) {
                                // it was a star, hence check our the type of fromCvObject
                                toCollection = helper.search( fromCvObject.getClass(), "ac", null ); // load them all

                                // remove evenutual cycle
                                toCollection.remove( fromCvObject );
                            }

                            if ( fromCollection != null ) {

                                for ( Iterator iterator = fromCollection.iterator(); iterator.hasNext(); ) {
                                    CvObject _fromCvObject = (CvObject) iterator.next();

                                    addMapping( map, _fromCvObject, toCvObject );
                                }

                            } else if ( toCollection != null ) {

                                for ( Iterator iterator = toCollection.iterator(); iterator.hasNext(); ) {
                                    CvObject _toCvObject = (CvObject) iterator.next();

                                    addMapping( map, fromCvObject, _toCvObject );
                                }

                            } else {

                                addMapping( map, fromCvObject, toCvObject );
                            }

                        } else {

                            System.err.println( "Line " + lineCount + " from (" + fromMI +
                                                ") and to (" + toMI +
                                                ") format is erroneous. Skip it." );
                        }
                    } else {
                        System.err.println( "Line " + lineCount + ": Mapping wrong. Skip it." );
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }

            in.close();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // try to cache the map on disk for later use.

        FileOutputStream out = null;
        try {
            if ( ! map.isEmpty() ) {
                System.out.println( "Caching " + map.size() + " associations into " + serializeFile.getAbsolutePath() );
                out = new FileOutputStream( serializeFile );
                ObjectOutputStream oos = new ObjectOutputStream( out );

                // write the count of associations
                oos.writeObject( new Integer( map.size() ) );

                for ( Iterator iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                    CvObject from = (CvObject) iterator.next();
                    CvObject to = (CvObject) map.get( from );

                    oos.writeObject( from );
                    oos.writeObject( to );
                }
                oos.flush();
                oos.close();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * If there is a mapping available, replace the given CV by an other one.
     *
     * @param fromCvObject CvObject to remap.
     *
     * @return a CvObject, it can be different if specified in the mapping.
     */
    public CvObject getPSI2toPSI1( CvObject fromCvObject ) {
        CvObject toCvObject = (CvObject) map.get( fromCvObject );
        return ( toCvObject == null ? fromCvObject : toCvObject );
    }

}

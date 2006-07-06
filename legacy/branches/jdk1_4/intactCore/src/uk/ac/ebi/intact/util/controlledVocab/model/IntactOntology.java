/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab.model;

import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * Container for the IntAct Controlled Vocabulary terms.<br> Allow to perform simple queries that will help during the
 * update process.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Sep-2005</pre>
 */
public class IntactOntology {

    ///////////////////////////////
    // Instance variables

    /**
     * Pool of all term contained in that ontology (contains CvTerm)
     */
    private Collection cvTerms = new ArrayList( 1024 );

    /**
     * Mapping of all CvTerm by their ID (String -> CvTerm).
     */
    private static Map mi2cvTerm = new HashMap( 1024 );

    private static Map mi2name = new HashMap();

    /**
     * Maps which IntAct CV maps to which CV root (by one to many MI reference) Contains: Class -> String[]
     */
    private static Map class2mi = new HashMap();

    static {

        // Initialising the mapping of IntAct CV Class to CvTerm IDs.
        // class2mi.put( CvObject.class, new Object[]{ "MI:0000" } );

        // DAG
        class2mi.put( CvInteraction.class, new String[]{ "MI:0001" } );
        class2mi.put( CvInteractionType.class, new String[]{ "MI:0190" } );
        class2mi.put( CvIdentification.class, new String[]{ "MI:0002" } );
        class2mi.put( CvFeatureIdentification.class, new String[]{ "MI:0003" } );
        class2mi.put( CvFeatureType.class, new String[]{ "MI:0116" } );
        class2mi.put( CvInteractorType.class, new String[]{ "MI:0313" } );

        // Non DAG
        class2mi.put( CvFuzzyType.class, new String[]{ "MI:0333" } );
        class2mi.put( CvXrefQualifier.class, new String[]{ "MI:0353" } );
        class2mi.put( CvDatabase.class, new String[]{ "MI:0444" } );
        class2mi.put( CvComponentRole.class, new String[]{ "MI:0500", "MI:0495" } );
        class2mi.put( CvAliasType.class, new String[]{ "MI:0300" } );
        class2mi.put( CvTopic.class, new String[]{ "MI:0590" } );

        // mapping of the non DAG term to their shortlabel
        mi2name.put( "MI:0300", "alias type" );
        mi2name.put( "MI:0333", "feature range status" );
        mi2name.put( "MI:0353", "xref type" );
        mi2name.put( "MI:0444", "database citation" );
        mi2name.put( "MI:0495", "experimental role" );
        mi2name.put( "MI:0500", "biological role" );
        mi2name.put( "MI:0590", "attribute name" );
    }

    /**
     * Maps IntAct CV Class to Ontology Terms. node One IntAct CV can have multiple roots.
     */
    private Map intact2psi = new HashMap();

    //////////////////////////////
    // Private methods

    /**
     * Convert a Object Array into a Collection.
     *
     * @param ids the Array
     *
     * @return a non null Collection
     */
    private Collection createCollection( Object[] ids ) {
        Collection c = new ArrayList( ids.length );
        for ( int i = 0; i < ids.length; i++ ) {
            Object o = ids[ i ];
            c.add( o );
        }
        return c;
    }

    /////////////////////////////
    // Public methods

    /**
     * CvObject --> Collection( "MI:xxx" )
     * @return a copy of the Mapping IntAct CV to MI roots
     */
    public static Map getTypeMapping() {
         return new HashMap( class2mi );
    }

    /**
     * CvObject --> Collection( "MI:xxx" ).
     * If includeDags is false, we take out all concrete class of CvDagObject.
     * @return a copy of the Mapping IntAct CV to MI roots
     */
    public static Map getTypeMapping( boolean includeDags ) {
        Map map = getTypeMapping();
        if( !includeDags ) {
            for ( Iterator iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                Class clazzKey = (Class) iterator.next();
                if( CvDagObject.class.isAssignableFrom( clazzKey ) ) {
                     iterator.remove();
                }
            }
        }
        return map;
    }

    public static Map getNameMapping() {
        return new HashMap( mi2name );
    }

    /**
     * Add a new Term in the pool. It will be indexed by its ID.
     *
     * @param term
     */
    public void addTerm( CvTerm term ) {

        cvTerms.add( term );
        String id = term.getId();
        if ( mi2cvTerm.containsKey( id ) ) {
            CvTerm old = (CvTerm) mi2cvTerm.get( id );
            System.err.println( "WARNING: 2 Objects have the same ID (" + id + "), the old one is being replaced." );
            System.err.println( "         old: '" + old.getShortName() + "'" );
            System.err.println( "         new: '" + term.getShortName() + "'" );
        }
        mi2cvTerm.put( id, term );
    }

    /**
     * Create a relashionship parent to child between two CvTerm.
     *
     * @param parentId The parent term.
     * @param childId  The child term.
     */
    public void addLink( String parentId, String childId ) {

        CvTerm child = (CvTerm) mi2cvTerm.get( childId );
        CvTerm parent = (CvTerm) mi2cvTerm.get( parentId );

        child.addParent( parent );
        parent.addChild( child );
    }

    /**
     * Answer the question: 'Has that ontology any term loaded ?'.
     *
     * @return true is there are any terms loaded, false otherwise.
     */
    public boolean hasTerms() {
        return cvTerms.isEmpty();
    }

    /**
     * Return all IntAct suppoorted CvObject implementation.
     *
     * @return a Collection of Class. never null.
     */
    public Collection getTypes() {
        Collection types = new ArrayList();

        for ( Iterator iterator = class2mi.keySet().iterator(); iterator.hasNext(); ) {
            Class clazz = (Class) iterator.next();
            types.add( clazz );
        }

        return types;
    }

    /**
     * Uses the Mapping( CV class -> Array(MI) ) to create an other mapping CV class -> Collection( CvTerm root ). That
     * method should only be called once all CvTerm have been added.
     */
    public void updateMapping() {

        if ( !intact2psi.isEmpty() ) {
            System.out.println( "WARNING: UpdateMapping requested, clearing existing mapping." );
            intact2psi.clear();
        }

        for ( Iterator iterator = class2mi.keySet().iterator(); iterator.hasNext(); ) {
            Class clazz = (Class) iterator.next();

            Object[] mis = (Object[]) class2mi.get( clazz );
            if ( mis.length == 1 ) {

                String mi = (String) mis[ 0 ];
                intact2psi.put( clazz, createCollection( new Object[]{ mi2cvTerm.get( mi ) } ) );

            } else {

                // collecting all the roots from the given set of MI references
                Collection cvTerms = new ArrayList( mis.length );
                for ( int i = 0; i < mis.length; i++ ) {
                    String mi = (String) mis[ i ];

                    cvTerms.add( mi2cvTerm.get( mi ) );
                }

                // add mapping entry
                intact2psi.put( clazz, createCollection( cvTerms.toArray() ) );
            }
        }
    }

    /**
     * Search for a CvTerm by its ID.
     *
     * @param id
     *
     * @return a CvTerm or null if not found.
     */
    public CvTerm search( String id ) {
        return (CvTerm) mi2cvTerm.get( id );
    }

    /**
     * Get the Root terms of a specific IntAct CV Class.
     *
     * @param cvObjectClass
     *
     * @return
     */
    public Collection getRoots( Class cvObjectClass ) {
        return (Collection) intact2psi.get( cvObjectClass );
    }

    /**
     * Get all CvTerms of a specific IntAct CV Class.
     *
     * @param cvObjectClass
     *
     * @return
     */
    public Set getCvTerms( Class cvObjectClass ) {
        Collection roots = (Collection) intact2psi.get( cvObjectClass );

        Set terms = new HashSet();

        for ( Iterator iterator = roots.iterator(); iterator.hasNext(); ) {
            CvTerm root = (CvTerm) iterator.next();
            terms.addAll( root.getAllChildren() );
        }

        return terms;
    }

    /**
     * Get all CvTerm.
     *
     * @return
     */
    public Collection getCvTerms() {
        return Collections.unmodifiableCollection( cvTerms );
    }

    /**
     * Go through the list of all CV Term and select those that are obsolete.
     *
     * @return a non null Collection of obsolete term.
     */
    public Collection getObsoleteTerms() {

        Collection obsoleteTerms = new ArrayList();

        for ( Iterator iterator = getCvTerms().iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();

            if ( cvTerm.isObsolete() ) {
                obsoleteTerms.add( cvTerm );
            }
        }

        return obsoleteTerms;
    }

    /////////////////////////////////
    // Utility - Display methods

    public void print() {

        System.out.println( cvTerms.size() + " terms to display." );
        System.out.println( intact2psi.size() + " categories." );

        for ( Iterator iterator = intact2psi.keySet().iterator(); iterator.hasNext(); ) {
            Class aClass = (Class) iterator.next();
            Collection roots = (Collection) intact2psi.get( aClass );

            System.out.println( "======================================================" );
            System.out.println( aClass );
            System.out.println( "======================================================" );
            for ( Iterator iterator1 = roots.iterator(); iterator1.hasNext(); ) {
                CvTerm cvTerm = (CvTerm) iterator1.next();
                print( cvTerm );
            }
        }
    }

    private void print( CvTerm term, String indent ) {

        System.out.println( indent + term.getId() + "   " + term.getShortName() + " (" + term.getFullName() + ")" );
        for ( Iterator iterator = term.getChildren().iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();
            print( cvTerm, indent + "  " );
        }
    }

    public void print( CvTerm term ) {
        print( term, "" );
    }

    public void print( Class clazz ) {

        System.out.println( "------------------------------------------------" );
        System.out.println( clazz.getName() );
        System.out.println( "------------------------------------------------" );
        Collection roots = (Collection) this.getRoots( clazz );
        if ( roots != null ) {
            for ( Iterator iterator = roots.iterator(); iterator.hasNext(); ) {
                CvTerm cvTerm = (CvTerm) iterator.next();

                print( cvTerm );
            }
        } else {
            System.err.println( "Could not find a mapping for " + clazz.getName() );
        }
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab.model;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Sep-2005</pre>
 */
public class CvTerm {
    private String id;

    private String shortName;
    private String fullName;

    private String definition;
    private Collection xrefs = new HashSet( 2 ); // <CvTermXref>

    private Collection synonyms = new HashSet( 2 ); // <String>
    private Collection parents = new HashSet( 2 );  // <CvTerm>
    private Collection children = new HashSet( 2 ); // <CvTerm>

    private boolean obsolete = false;
    private String obsoleteMessage;

    private Collection annotations = new ArrayList( 2 ); // <CvTermAnnotation>

    /////////////////////////////
    // Constructor

    public CvTerm() {
    }

    public CvTerm( String id ) {

        if ( id == null ) {
            throw new IllegalArgumentException( "ID can't be null" );
        }

        this.id = id;
    }

    public CvTerm( String id, String shortname, String definition ) {

        this( id );

        if ( shortname == null ) {
            throw new IllegalArgumentException( "shortname can't be null (id: " + id + ")" );
        }

        this.shortName = shortname;
        this.definition = definition;
    }



    //////////////////////
    // Getters

    public String getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDefinition() {
        return definition;
    }

    public Collection getXrefs() {
        return xrefs;
    }

    public Collection getSynonyms() {
        return synonyms;
    }

    public Collection getParents() {
        return parents;
    }

    public Collection getChildren() {
        return children;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public String getObsoleteMessage() {
        return obsoleteMessage;
    }

    public Collection getAnnotations() {
        return annotations;
    }

    ///////////////////
    // Setters

    public void setId( String id ) {
        this.id = id;
    }

    public void setShortName( String shortName ) {
        if ( shortName != null ) {
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    public void setFullName( String fullName ) {
        if ( fullName != null ) {
            fullName = fullName.trim();
        }
        this.fullName = fullName;
    }

    public void setDefinition( String definition ) {
        if ( definition != null ) {
            definition = definition.trim();
        }
        this.definition = definition;
    }

    public void addXref( CvTermXref xref ) {
        xrefs.add( xref );
    }

    public void addSynonym( String synonym ) {
        synonyms.add( synonym );
    }

    public void addParent( CvTerm parent ) {
        parents.add( parent );
    }

    public void addChild( CvTerm child ) {
        children.add( child );
    }

    public void addAnnotation( String topic, String annotation ) {
        if( topic != null ) {
            topic = topic.trim();
        }
        if( annotation != null ) {
            annotation = annotation.trim();
        }
        annotations.add( new CvTermAnnotation( topic, annotation ) );
    }

    public void setObsolete( boolean obsolete ) {
        this.obsolete = obsolete;
    }

    public void setObsoleteMessage( String obsoleteMessage ) {
        this.obsoleteMessage = obsoleteMessage;
    }

    /////////////////////////
    // Equals / hashCode

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof CvTerm ) ) {
            return false;
        }

        final CvTerm cvTerm = (CvTerm) o;

        if ( !id.equals( cvTerm.id ) ) {
            return false;
        }
        if ( !shortName.equals( cvTerm.shortName ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 29 * result + shortName.hashCode();
        return result;
    }

    //////////////////////////
    // Display

    public String toString() {
        final StringBuffer sb = new StringBuffer( 128 );
        sb.append( "CvTerm" );
        sb.append( "{id='" ).append( id ).append( '\'' );
        sb.append( ", shortName='" ).append( shortName ).append( '\'' );
        sb.append( ", fullName='" ).append( fullName ).append( '\'' );
        sb.append( ", definition='" ).append( definition ).append( '\'' );
        sb.append( ", xrefs=" );
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            CvTermXref xref = (CvTermXref) iterator.next();
            sb.append( xref.getDatabase() ).append( ':' ).append( xref.getId() );
            if( iterator.hasNext() ) {
                sb.append( ", ");
            }
        }

        sb.append( ", synonyms=" ).append( synonyms );

        sb.append( ", parents=" );
        for ( Iterator iterator = parents.iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();
            sb.append( cvTerm.getShortName() ).append( "(" ).append( cvTerm.getId() ).append( ")" );
            if( iterator.hasNext() ) {
                sb.append( ", ");
            }
        }

        sb.append( ", children=" );
        for ( Iterator iterator = children.iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();
            sb.append( cvTerm.getShortName() ).append( "(" ).append( cvTerm.getId() ).append( ")" );
            if( iterator.hasNext() ) {
                sb.append( ", ");
            }
        }

        sb.append( ", obsolete=" ).append( obsolete );
        sb.append( ", obsoleteMessage='" ).append( obsoleteMessage ).append( '\'' );
        sb.append( ", annotations=" ).append( annotations );
        sb.append( '}' );
        return sb.toString();
    }

    ////////////////////////////
    // Utility

    /**
     * @param term
     * @param children
     */
    private void getAllChildren( CvTerm term, Collection children ) {

        children.add( term );

        for ( Iterator iterator = term.getChildren().iterator(); iterator.hasNext(); ) {
            CvTerm child = (CvTerm) iterator.next();
            getAllChildren( child, children );
        }
    }

    public Set getAllChildren() {
        Set children = new HashSet();
        getAllChildren( this, children );
        return children;
    }
}
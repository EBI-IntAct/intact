/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a term in OLS
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.1
 */
public class Term implements Serializable {

    private String id;
    private String name;
    private String exactSynonim;
    private String definition;
    private Term parent;
    private Collection<Term> children;
    private Collection<Term> parents;
    private Map<String, String> metadata;

    /**
     * Constructs a new Term
     */
    public Term() {
        this.children = new ArrayList<Term>();
        this.parents = new ArrayList<Term>();
    }

    /**
     * Constructs a new Term
     *
     * @param id   the id for the term
     * @param name name of the term
     */
    public Term( String id, String name, Map<String, String> metadata ) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<Term>();
        this.parents = new ArrayList<Term>();
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Collection<Term> getChildren() {
        return children;
    }

    public void setChildren( Collection<Term> children ) {
        this.children = children;
    }

    public void addChild( Term child ) {
        child.setParent( this );
        children.add( child );
    }


    public void addParents( Term parent ) {
        Collection<Term> children = this.getChildren();
        parent.setChildren( children );
        parents.add( parent );
    }

    public Term getParent() {
        return parent;
    }

    public void setParent( Term parent ) {
        this.parent = parent;
    }

    public String getDefinition() {
        return metadata.get( "definition" );
    }

    public String getExactSynonim() {
        return metadata.get( "exact_synonym" );
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata( Map<String, String> metadata ) {
        this.metadata = metadata;
    }

    public Collection<Term> getParents() {
        return parents;
    }

    public void setParents( Collection<Term> parents ) {
        this.parents = parents;
    }

    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }

        final Term other = ( Term ) obj;

        if ( this.getId() != null &&  other.getId()!=null ) {
            return this.getId().equals( other.getId() );
        }
        if ( this.getName() != null && other.getName()!=null ) {
            return this.getName().equals( other.getName() );
        }


        return true;
    }


    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 29 * result + id.hashCode();
        result = 29 * result + ( name != null ? name.hashCode() : 0 );

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Term" );
        sb.append( "{children=" ).append( children );
        sb.append( ", id='" ).append( id ).append( '\'' );
        sb.append( ", name='" ).append( name ).append( '\'' );
        if ( parent != null ) sb.append( ", parent=" ).append( parent.getId() );
        sb.append( '}' );
        return sb.toString();
    }
}
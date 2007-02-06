/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.newt;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Serializable;

/**
 * Description of a Newt Term.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jan-2007</pre>
 */
public class NewtTerm implements Serializable {

    /**
     * Class version for checking when serializing.
     */
    private static final long serialVersionUID = 1L;  

    /**
     * Regular expression to extract taxid, common and scientific name.
     * The pattern is -- number|short_label|full_name|ignore other text
     */
    private static final Pattern REG_EXP = Pattern.compile( "(\\d+)\\|(.*?)\\|(.*?)\\|.*" );

    /**
     * Set of non redundant parents.
     */
    private Set<NewtTerm> parents = new HashSet<NewtTerm>( 1 );

    /**
     * Set of non redundant children.
     */
    private Set<NewtTerm> children = new HashSet<NewtTerm>( );

    /**
     * Taxid of the newt term.
     */
    private int taxid;

    /**
     * Scientific name of the term.
     */
    private String scientificName;

    /**
     * Common name of the term.
     */
    private String commonName;

    ///////////////////
    // Constructor

    public NewtTerm( int taxid ) {
        setTaxid( taxid );
    }

    public NewtTerm( String newtLine ) {
        // create a dummy term, it will be overriden later
        this( 1 );

        Matcher matcher = REG_EXP.matcher( newtLine );
        if ( !matcher.matches() ) {
            throw new IllegalArgumentException( "Could not parse: " + newtLine );
        }

        // Values from newt stored in
        setTaxid( Integer.parseInt( matcher.group( 1 ) ) );
        setCommonName( matcher.group( 2 ) );
        setScientificName( matcher.group( 3 ) );
    }

    ////////////////////////
    // Getters and Setters

    /**
     * Getter for property 'commonName'.
     *
     * @return Value for property 'commonName'.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Setter for property 'commonName'.
     *
     * @param commonName Value to set for property 'commonName'.
     */
    public void setCommonName( String commonName ) {
        this.commonName = commonName;
    }

    /**
     * Getter for property 'scientificName'.
     *
     * @return Value for property 'scientificName'.
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Setter for property 'scientificName'.
     *
     * @param scientificName Value to set for property 'scientificName'.
     */
    public void setScientificName( String scientificName ) {
        this.scientificName = scientificName;
    }

    /**
     * Getter for property 'taxid'.
     *
     * @return Value for property 'taxid'.
     */
    public int getTaxid() {
        return taxid;
    }

    /**
     * Setter for property 'taxid'.
     *
     * @param taxid Value to set for property 'taxid'.
     */
    public void setTaxid( int taxid ) {
        if ( taxid < -2 || taxid == 0 ) {
            throw new IllegalArgumentException( taxid + ": a taxid must be > 1 or be -1 (in vitro) or -2 (chemical synthesis)." );
        }
        this.taxid = taxid;
    }

    public void addChild( NewtTerm child ) {
        children.add( child );
        child.parents.add( this );
    }

    public void removeChild( NewtTerm child ) {
        children.remove( child );
        child.parents.remove( this );
    }

    public void addParent( NewtTerm parent ) {
        parents.add( parent );
        parent.children.add( this );
    }

    public void removeParent( NewtTerm parent ) {
        parents.remove( parent );
        parent.children.remove( this );
    }

    public Collection<NewtTerm> getChildren() {
        return Collections.unmodifiableSet( children );
    }

    public Collection<NewtTerm> getParents() {
        return Collections.unmodifiableSet( parents );
    }

    ///////////////////////
    // Object override

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "NewtTerm" );
        sb.append( "{commonName='" ).append( commonName ).append( '\'' );
        sb.append( ", taxid=" ).append( taxid );
        sb.append( ", scientificName='" ).append( scientificName ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        NewtTerm newtTerm = ( NewtTerm ) o;

        if ( taxid != newtTerm.taxid ) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return taxid;
    }
}
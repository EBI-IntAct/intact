/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Models a scientific paper and its relationship to (potentialy) many intact Experiments.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2006</pre>
 */
public class Publication extends AnnotatedObjectImpl implements Editable {

    /**
     * PubMed Id of the publication.
     */
    private String pmid;

    /**
     * List of experiments related to that publication.
     */
    Collection<Experiment> experiments = new ArrayList<Experiment>();

    ///////////////////////////
    // Constructors
    @Deprecated
    private Publication() {
    }

    public Publication( Institution owner, String pmid ) {
        super( pmid, owner );
        setPmid( pmid );
    }

    ////////////////////////////
    // Getter and Setter

    public String getPmid() {
        return pmid;
    }

    public void setPmid( String pmid ) {
        if ( pmid == null ) {
            throw new IllegalArgumentException( "You must give a non null PubMed ID." );
        }

        try {
            Integer.parseInt( pmid );
        } catch ( NumberFormatException e ) {
            throw e;
        }

        this.pmid = pmid;
    }

    //////////////////////////////
    // Experiment handling

    public void addExperiment( Experiment experiment ) {
        if ( ! experiments.contains( experiment ) ) {
            experiments.add( experiment );
        }
    }

    public void removeExperiment( Experiment experiment ) {
        experiments.remove( experiment );
    }

    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    ////////////////////////////
    // Object's override

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "Publication" );
        sb.append( "{ac='" + ac + "', pmid='" ).append( pmid ).append( '\'' );

        if ( ! annotations.isEmpty() ) {
            sb.append( ", annotations={" );
            for ( Iterator<Annotation> iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation annotation = iterator.next();
                sb.append( "annotation('" + annotation.getCvTopic().getShortLabel() + "', '" + annotation.getAnnotationText() + "')" );
                if ( iterator.hasNext() ) {
                    sb.append( ", " );
                }
            }
            sb.append( "}" );
        }

        sb.append( '}' );
        return sb.toString();
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final Publication that = (Publication) o;

        if ( !pmid.equals( that.pmid ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + pmid.hashCode();
        return result;
    }
}
/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents a biological source. TODO write a proper comment
 *
 * @author hhe
 * @version $id$
 */
@Entity
@Table(name = "ia_biosource")
@AssociationOverride(name = "annotations",
                     joinColumns = {@JoinColumn(name="annotation_ac")} )
@EditorTopic
public class BioSource extends AnnotatedObjectImpl implements Editable {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String cvCellCycleAc;
    public String cvDevelopmentalStageAc;
    public String cvTissueAc;
    public String cvCellTypeAc;
    public String cvCompartmentAc;


    /**
     * The NCBI tax id.
     */
    private String taxId;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvCellCycle cvCellCycle;

    /**
     * TODO comments
     */
    private CvDevelopmentalStage cvDevelopmentalStage;

    /**
     * TODO comments
     */
    private CvTissue cvTissue;

    /**
     * TODO comments
     */
    private CvCellType cvCellType;

    /**
     * TODO comments
     */
    private CvCompartment cvCompartment;

    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public BioSource() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid BioSource (ie a source organism) instance. A valid instance must have at least a non-null
     * shortLabel specified. A side-effect of this constructor is to set the <code>created</code> and
     * <code>updated</code> fields of the instance to the current time.
     *
     * @param shortLabel The label used to identify this instance
     * @param taxId      the NCBI taxId, which must be unique if defined (may be null)
     * @param owner      The <code>Institution</code> which 'owns' this BioSource
     *
     * @throws NullPointerException thrown if either no shortLabel or Institution specified.
     */
    public BioSource( Institution owner, String shortLabel, String taxId ) {

        //super call sets up a valid AnnotatedObject
        super( shortLabel, owner );

        setTaxId( taxId );
    }


    ///////////////////////////////////////
    //access methods for attributes

    @ManyToMany
    @JoinTable(
        name="ia_biosource2annot",
        joinColumns={@JoinColumn(name="biosource_ac")},
        inverseJoinColumns={@JoinColumn(name="annotation_ac")}
    )
    @Override
    public Collection<Annotation> getAnnotations()
    {
        return super.getAnnotations();
    }

    public String getTaxId() {
        return taxId;

    }

    public void setTaxId( String taxId ) {
        if ( taxId == null ) {
            throw new NullPointerException( "Valid BioSource must have a non-null taxId!" );
        } else {
            try {
                Integer.parseInt( taxId );
            } catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "A BioSource's taxid has to be an integer value." );
            }
        }

        // Note: negative in can be given: -1=in vitro, -2=chemical synthesis, -3=not affected yet

        this.taxId = taxId;
    }

    ///////////////////////////////////////
    // access methods for associations
    @Transient
    public CvCellCycle getCvCellCycle() {
        return cvCellCycle;
    }

    public void setCvCellCycle( CvCellCycle cvCellCycle ) {
        this.cvCellCycle = cvCellCycle;
    }

    @Transient
    public CvDevelopmentalStage getCvDevelopmentalStage() {
        return cvDevelopmentalStage;
    }

    public void setCvDevelopmentalStage( CvDevelopmentalStage cvDevelopmentalStage ) {
        this.cvDevelopmentalStage = cvDevelopmentalStage;
    }

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "tissue_ac")
    public CvTissue getCvTissue() {
        return cvTissue;
    }

    public void setCvTissue( CvTissue cvTissue ) {
        this.cvTissue = cvTissue;
    }

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "celltype_ac")
    public CvCellType getCvCellType() {
        return cvCellType;
    }

    public void setCvCellType( CvCellType cvCellType ) {
        this.cvCellType = cvCellType;
    }

    @Transient
    public CvCompartment getCvCompartment() {
        return cvCompartment;
    }

    public void setCvCompartment( CvCompartment cvCompartment ) {
        this.cvCompartment = cvCompartment;
    }

    @Column(name = "celltype_ac", insertable = false, updatable = false)
    public String getCvCellTypeAc() {
        return cvCellTypeAc;
    }

    public void setCvCellTypeAc( String ac ) {
        this.cvCellTypeAc = ac;
    }

    @Column(name = "tissue_ac", insertable = false, updatable = false)
    public String getCvTissueAc() {
        return cvTissueAc;
    }

    public void setCvTissueAc( String ac ) {
        this.cvTissueAc = ac;
    }

    /**
     * Equality for BioSources is currently based on equality for <code>AnnotatedObjects</code> and taxIds (String
     * representation of an integer).
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.AnnotatedObjectImpl
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof BioSource ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final BioSource bioSource = (BioSource) o;

        //check the taxId...
        if ( taxId != null ) {
            return ( taxId.equals( bioSource.getTaxId() ) );
        }

        return bioSource.taxId == null;
        //if (taxId != null ? !taxId.equals(bioSource.taxId) : bioSource.taxId != null) return false;

        //return true;
    }

    @Override
    public int hashCode() {

        //YUK AGAIN!! This ends up calling the java Object hashcode -
        //which generates an int based on memory address (ie for object
        //identity) - not what we want here...
        //int code = super.hashCode();

        int code = 29;
        if ( taxId != null ) {
            code = 29 * code + taxId.hashCode();
        }
        return code;
    }

    // TODO write toString !!

} // end BioSource






/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comments
 *
 * @author hhe
 * @version $Id$
 */
public class Feature extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String xrefAc;
    protected String proteinAc;
    protected String componentAc;
    protected String boundDomainAc;
    protected String cvFeatureIdentificationAc;

    /**
     * References a description of a domain in an external database.
     */
    private Xref xref;

    ///////////////////////////////////////
    // associations

    /**
     *  TODO comments
     */
    private CvFeatureType cvFeatureType;

    /**
     *  TODO comments
     */
    private Protein protein;

    /**
     * The Substrate a domain belongs to.
     */
    private Component component;

    /**
     * The domain the current domain binds to.
     */
    private Feature boundDomain;

    /**
     *  TODO comments
     */
    private Collection ranges = new ArrayList();

    /**
     *  TODO comments
     */
    private CvFeatureIdentification cvFeatureIdentification;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    public Feature() {
        super();
    }


    ///////////////////////////////////////
    //access methods for attributes

    public Xref getXref() {
        return xref;
    }
    public void setXref(Xref xref) {
        this.xref = xref;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvFeatureType getCvFeatureType() {
        return cvFeatureType;
    }

    public void setCvFeatureType(CvFeatureType cvFeatureType) {
        this.cvFeatureType = cvFeatureType;
    }
    public Protein getProtein() {
        return protein;
    }
    // TODO comments
    public void setProtein(Protein protein) {
        if (this.protein != protein) {
            if (this.protein != null) this.protein.removeFeature(this);
            this.protein = protein;
            if (protein != null) protein.addFeature(this);
        }
    }
    public Component getComponent() {
        return component;
    }
    // TODO comments
    public void setComponent(Component component) {
        if (this.component != component) {
            if (this.component != null) this.component.removeBindingDomain(this);
            this.component = component;
            if (component != null) component.addBindingDomain(this);
        }
    }
    public Feature getBoundDomain() {
        return boundDomain;
    }

    public void setBoundDomain(Feature feature) {
        this.boundDomain = feature;
    }
    public Collection getRanges() {
        return ranges;
    }
    public void addRange(Range range) {
        if (! this.ranges.contains(range)) this.ranges.add(range);
    }
    public void removeRange(Range range) {
        this.ranges.remove(range);
    }
    public CvFeatureIdentification getCvFeatureIdentification() {
        return cvFeatureIdentification;
    }

    public void setCvFeatureIdentification(CvFeatureIdentification cvFeatureIdentification) {
        this.cvFeatureIdentification = cvFeatureIdentification;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getXrefAc(){
        return this.xrefAc;
    }
    public void setXrefAc(String ac){
        this.xrefAc = ac;
    }

    public String getProteinAc(){
        return this.proteinAc;
    }
    public void setProteinAc(String ac){
        this.proteinAc = ac;
    }

    public String getComponentAc(){
        return this.componentAc;
    }
    public void setComponentAc(String ac){
        this.componentAc = ac;
    }

    public String getBoundDomainAc(){
        return this.boundDomainAc;
    }
    public void setBoundDomainAc(String ac){
        this.boundDomainAc = ac;
    }

    public String getCvFeatureIdentificationAc(){
        return this.cvFeatureIdentificationAc;
    }
    public void setCvFeatureIdentificationAc(String ac){
        this.cvFeatureIdentificationAc = ac;
    }

} // end Feature





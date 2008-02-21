/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.*;

/**
 * Represents a specific parameter value of an interaction.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.8.0
 */
@Entity
@Table( name = "ia_interaction_parameter" )
public class InteractionParameter extends BasicObjectImpl {
    private	String base;
    private String exponent;
    private String factor;
    private String uncertainty;
	private CvParameterType cvParameterType;
    private CvParameterUnit cvParameterUnit;
    private InteractionImpl interaction;

	public InteractionParameter() {
		super();
		this.base = "10";
		this.exponent = "0";
	}
	
	public InteractionParameter( Institution owner, CvParameterType cvParameterType, String factor ) {
        super(owner);
        setFactor(factor);
        setCvParameterType(cvParameterType);
    }
	
	public InteractionParameter( Institution owner, CvParameterType cvParameterType, CvParameterUnit cvParameterUnit, String factor ) {
        super(owner);
        setFactor(factor);
        setCvParameterType(cvParameterType);
        setCvParameterUnit(cvParameterUnit);
    }
	
	public void setBase( String base ) {
        this.base = base;
    }
	
	public void setExponent( String exponent ) {
        this.exponent = exponent;
    }
	
	public void setFactor( String factor ) {
        this.factor = factor;
    }
	
	public void setUncertainty( String uncertainty ) {
        this.uncertainty = uncertainty;
    }
    
    public String getBase() {
        return this.base;
    }
	
	public String getExponent() {
        return this.exponent;
    }
	
	public String getFactor() {
        return this.factor = factor;
    }
	
	public String getUncertainty() {
        return this.uncertainty;
    }
    
    @ManyToOne ( targetEntity = InteractionImpl.class )
    @JoinColumn (name = "interaction_ac")
     public InteractionImpl getInteraction() {
        return interaction;
    }
    
    public void setInteraction( InteractionImpl interaction ) {
        this.interaction = interaction;
    }
    
    @ManyToOne
    @JoinColumn( name = "parametertype_ac" )
    public CvParameterType getCvParameterType() {
        return cvParameterType;
    }
    
	public void setCvParameterType( CvParameterType cvParameterType ) {
        this.cvParameterType = cvParameterType;
    }

    @ManyToOne
    @JoinColumn( name = "parameterunit_ac" )
    public CvParameterUnit getCvParameterUnit() {
        return cvParameterUnit;
    }
    
	public void setCvParameterUnit( CvParameterUnit cvParameterUnit ) {
        this.cvParameterUnit = cvParameterUnit;
    }
}

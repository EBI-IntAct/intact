package uk.ac.ebi.intact.editor.controller.curate.interaction;

import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains experimental conditions for a specific variable parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11/12/14</pre>
 */

public class ImportExperimentalCondition {

    private String description;
    private String unit;
    private List<SelectItem> variableValues;
    private VariableParameterValue selectedValue;

    public ImportExperimentalCondition(VariableParameter param){
        if (param == null){
            throw new IllegalArgumentException("The variable parameter cannot be null");
        }
        this.description = param.getDescription();
        this.unit = param.getUnit() != null ? param.getUnit().getShortName() : "-";
        this.variableValues = new ArrayList<SelectItem>(param.getVariableValues().size());
        variableValues.add( new SelectItem( null, "-- Select variable condition value --", "-- Select variable condition value --", false, false, true ) );

        for (VariableParameterValue v : param.getVariableValues()){
            variableValues.add(new SelectItem( v, v.getValue(), v.getValue()+", order "+v.getOrder()));
        }
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public List<SelectItem> getVariableValues() {
        return variableValues;
    }

    public VariableParameterValue getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(VariableParameterValue selectedValue) {
        this.selectedValue = selectedValue;
    }
}

-- PROMPT "Adding index on ia_interaction_parameter"
CREATE INDEX i_interaction_parameter_factor  on IA_interaction_parameter(factor);
CREATE INDEX i_interaction_parameter_parametertype   on IA_interaction_parameter(parametertype_ac);
CREATE INDEX i_interaction_parameter_parameterunit   on IA_interaction_parameter(parameterunit_ac);
CREATE INDEX i_interaction_parameter_interaction   on IA_interaction_parameter(interaction_ac) ;
CREATE INDEX i_interaction_parameter_experiment   on IA_interaction_parameter(experiment_ac) ;

-- PROMPT "Adding index on ia_component_parameter"
CREATE INDEX i_component_parameter_factor  on IA_component_parameter(factor);
CREATE INDEX i_component_parameter_parametertype   on IA_component_parameter(parametertype_ac);
CREATE INDEX i_component_parameter_parameterunit   on IA_component_parameter(parameterunit_ac);
CREATE INDEX i_component_parameter_component   on IA_component_parameter(component_ac) ;
CREATE INDEX i_component_parameter_experiment   on IA_component_parameter(experiment_ac) ;
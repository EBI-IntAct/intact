-- PROMPT "Adding index on ia_interaction_parameter"
CREATE INDEX i_int_param_int_ac             on ia_interaction_parameter(interaction_ac)   ;
CREATE INDEX i_int_param_parametertype_ac   on ia_interaction_parameter(parametertype_ac) ;
CREATE INDEX i_int_param_parameterunit_ac   on ia_interaction_parameter(parameterunit_ac) ;
CREATE INDEX i_int_param_factor             on ia_interaction_parameter(factor)           ;
CREATE INDEX i_int_param_experiment_ac      on ia_interaction_parameter(experiment_ac)    ;


-- PROMPT "Adding index on ia_component_parameter"
CREATE INDEX i_comp_param_comp_ac           on ia_component_parameter(component_ac)     ;
CREATE INDEX i_comp_param_parametertype_ac  on ia_component_parameter(parametertype_ac) ;
CREATE INDEX i_comp_param_parameterunit_ac  on ia_component_parameter(parameterunit_ac) ;
CREATE INDEX i_comp_param_factor            on ia_component_parameter(factor)           ;
CREATE INDEX i_comp_param_experiment        on ia_component_parameter(experiment_ac)    ;
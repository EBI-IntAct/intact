PROMPT "Adding index on ia_interaction_parameter"
CREATE INDEX i_int_param$int_ac             on ia_interaction_parameter(interaction_ac)   TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_int_param$parametertype_ac   on ia_interaction_parameter(parametertype_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_int_param$parameterunit_ac   on ia_interaction_parameter(parameterunit_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_int_param$factor             on ia_interaction_parameter(factor)           TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_int_param$experiment_ac      on ia_interaction_parameter(experiment_ac)    TABLESPACE &&intactIndexTablespace;


PROMPT "Adding index on ia_component_parameter"
CREATE INDEX i_comp_param$comp_ac           on ia_component_parameter(component_ac)     TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_comp_param$parametertype_ac  on ia_component_parameter(parametertype_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_comp_param$parameterunit_ac  on ia_component_parameter(parameterunit_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_comp_param$factor            on ia_component_parameter(factor)           TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_comp_param$experiment        on ia_component_parameter(experiment_ac)    TABLESPACE &&intactIndexTablespace;
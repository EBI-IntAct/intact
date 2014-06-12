-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on ia_experimental_causal_relationship"
grant select on ia_experimental_causal_relationship to INTACT_SELECT;
grant select, insert,update,delete on ia_experimental_causal_relationship to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_causal_relationship"
grant select on ia_modelled_causal_relationship to INTACT_SELECT;
grant select, insert,update,delete on ia_modelled_causal_relationship to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_feature2linked_feature"
grant select on ia_modelled_feature2linked_feature to INTACT_SELECT;
grant select, insert,update,delete on ia_modelled_feature2linked_feature to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_evidence2linkedfeature"
grant select on ia_feature_evidence2linkedfeature to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_evidence2linkedfeature to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature2detection_method"
grant select on ia_feature2detection_method to INTACT_SELECT;
grant select, insert,update,delete on ia_feature2detection_method to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_parameter"
grant select on ia_feature_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_parameter to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_resulting_sequence_xref"
grant select on ia_modelled_resulting_sequence_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_modelled_resulting_sequence_xref to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_experimental_resulting_sequence_xref"
grant select on ia_experimental_resulting_sequence_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_experimental_resulting_sequence_xref to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interactor_pool2interactor"
grant select on ia_interactor_pool2interactor to INTACT_SELECT;
grant select, insert,update,delete on ia_interactor_pool2interactor to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter"
grant select on ia_variable_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter_value"
grant select on ia_variable_parameter_value to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter_value to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interaction_var_parameters"
grant select on ia_interaction_var_parameters to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_var_parameters to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_varset2parametervalue"
grant select on ia_varset2parametervalue to INTACT_SELECT;
grant select, insert,update,delete on ia_varset2parametervalue to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_complex_lifecycle_event"
grant select on ia_complex_lifecycle_event to INTACT_SELECT;
grant select, insert,update,delete on ia_complex_lifecycle_event to INTACT_CURATOR;

---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on ia_experimental_causal_relationship_audit"
grant select on ia_experimental_causal_relationship_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_experimental_causal_relationship_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_causal_relationship_audit"
grant select on ia_modelled_causal_relationship_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_experimental_causal_relationship_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_feature2linked_feature_audit"
grant select on ia_modelled_feature2linked_feature_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_modelled_feature2linked_feature_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_evidence2linkedfeature_audit"
grant select on ia_feature_evidence2linkedfeature_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_evidence2linkedfeature_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature2detection_method_audit"
grant select on ia_feature2detection_method_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_feature2detection_method_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_parameter_audit"
grant select on ia_feature_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_parameter_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modelled_resulting_sequence_xref_audit"
grant select on ia_modelled_resulting_sequence_xref_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_modelled_resulting_sequence_xref_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_experimental_resulting_sequence_xref_audit"
grant select on ia_experimental_resulting_sequence_xref_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_experimental_resulting_sequence_xref_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interactor_pool2interactor_audit"
grant select on ia_interactor_pool2interactor_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_interactor_pool2interactor_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter_audit"
grant select on ia_variable_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter_value_audit"
grant select on ia_variable_parameter_value_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter_value_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interaction_var_parameters_audit"
grant select on ia_interaction_var_parameters_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_var_parameters_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_varset2parametervalue_audit"
grant select on ia_varset2parametervalue_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_varset2parametervalue_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_complex_lifecycle_event_audit"
grant select on ia_complex_lifecycle_event_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_complex_lifecycle_event_audit to INTACT_CURATOR;

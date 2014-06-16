-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on ia_exp_causal_relations"
grant select on ia_exp_causal_relations to INTACT_SELECT;
grant select, insert,update,delete on ia_exp_causal_relations to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_mod_causal_relations"
grant select on ia_mod_causal_relations to INTACT_SELECT;
grant select, insert,update,delete on ia_mod_causal_relations to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modfeature2feature"
grant select on ia_modfeature2feature to INTACT_SELECT;
grant select, insert,update,delete on ia_modfeature2feature to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_expfeature2feature"
grant select on ia_expfeature2feature to INTACT_SELECT;
grant select, insert,update,delete on ia_expfeature2feature to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature2method"
grant select on ia_feature2method to INTACT_SELECT;
grant select, insert,update,delete on ia_feature2method to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_parameter"
grant select on ia_feature_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_parameter to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_mod_sequence_xref"
grant select on ia_mod_sequence_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_mod_sequence_xref to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_exp_sequence_xref"
grant select on ia_exp_sequence_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_exp_sequence_xref to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_pool2interactor"
grant select on ia_pool2interactor to INTACT_SELECT;
grant select, insert,update,delete on ia_pool2interactor to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter"
grant select on ia_variable_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_var_parameter_value"
grant select on ia_var_parameter_value to INTACT_SELECT;
grant select, insert,update,delete on ia_var_parameter_value to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interaction_varparam"
grant select on ia_interaction_varparam to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_varparam to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_varset2paramvalue"
grant select on ia_varset2paramvalue to INTACT_SELECT;
grant select, insert,update,delete on ia_varset2paramvalue to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_complex_lcycle_evt"
grant select on ia_complex_lcycle_evt to INTACT_SELECT;
grant select, insert,update,delete on ia_complex_lcycle_evt to INTACT_CURATOR;

---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on ia_exp_causal_relations_audit"
grant select on ia_exp_causal_relations_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_exp_causal_relations_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_mod_causal_relations_audit"
grant select on ia_mod_causal_relations_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_mod_causal_relations_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_modfeature2feature_audit"
grant select on ia_modfeature2feature_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_modfeature2feature_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_expfeature2feature_audit"
grant select on ia_expfeature2feature_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_expfeature2feature_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature2method_audit"
grant select on ia_feature2method_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_feature2method_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_feature_parameter_audit"
grant select on ia_feature_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_feature_parameter_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_mod_sequence_xref_audit"
grant select on ia_mod_sequence_xref_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_mod_sequence_xref_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_exp_sequence_xref_audit"
grant select on ia_exp_sequence_xref_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_exp_sequence_xref_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_pool2interactor_audit"
grant select on ia_pool2interactor_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_pool2interactor_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_variable_parameter_audit"
grant select on ia_variable_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_variable_parameter_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_var_parameter_value_audit"
grant select on ia_var_parameter_value_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_var_parameter_value_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_interaction_varparam_audit"
grant select on ia_interaction_varparam_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_varparam_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_varset2paramvalue_audit"
grant select on ia_varset2paramvalue_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_varset2paramvalue_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_complex_lcycle_evt_audit"
grant select on ia_complex_lcycle_evt_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_complex_lcycle_evt_audit to INTACT_CURATOR;

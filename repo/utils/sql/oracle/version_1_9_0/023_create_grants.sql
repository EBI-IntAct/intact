-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on ia_interaction_parameter"
grant select on ia_interaction_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_parameter to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component_parameter"
grant select on ia_component_parameter to INTACT_SELECT;
grant select, insert,update,delete on ia_component_parameter to INTACT_CURATOR;


---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on ia_interaction_parameter_audit"
grant select on ia_interaction_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_interaction_parameter_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component_parameter_audit"
grant select on ia_component_parameter_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_component_parameter_audit to INTACT_CURATOR;
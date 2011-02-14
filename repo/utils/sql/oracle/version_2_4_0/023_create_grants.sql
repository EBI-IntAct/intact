-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on ia_component_confidence"
grant select on ia_component_confidence to INTACT_SELECT;
grant select, insert,update,delete on ia_component_confidence to INTACT_CURATOR;


---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on ia_component_confidence_audit"
grant select on ia_component_confidence_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_component_confidence_audit to INTACT_CURATOR;

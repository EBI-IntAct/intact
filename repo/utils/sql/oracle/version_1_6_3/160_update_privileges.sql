
-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on ia_institution_alias"
grant select on ia_institution_alias to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_alias to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_institution_xref"
grant select on ia_institution_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_xref to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_institution2annot"
grant select on ia_institution2annot to INTACT_SELECT;
grant select, insert,update,delete on ia_institution2annot to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component2exp_preps"
grant select on ia_component2exp_preps to INTACT_SELECT;
grant select, insert,update,delete on ia_component2exp_preps to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component2part_detect"
grant select on ia_component2part_detect to INTACT_SELECT;
grant select, insert,update,delete on ia_component2part_detect to INTACT_CURATOR;


---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on ia_institution_alias_audit"
grant select on ia_institution_alias_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_alias_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_institution_xref_audit"
grant select on ia_institution_xref_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_xref_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_institution2annot_audit"
grant select on ia_institution2annot_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_institution2annot_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component2exp_preps_audit"
grant select on ia_component2exp_preps_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_component2exp_preps_audit to INTACT_CURATOR;

PROMPT Creating table "Updating rights on ia_component2part_detect_audit"
grant select on ia_component2part_detect_audit to INTACT_SELECT;
grant select, insert,update,delete on ia_component2part_detect_audit to INTACT_CURATOR;

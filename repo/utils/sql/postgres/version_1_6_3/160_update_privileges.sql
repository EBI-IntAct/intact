
-------------------------------------
-- Update right on created objects --
-------------------------------------

SELECT 'Creating table "Updating rights on ia_institution_alias"';
grant select on ia_institution_alias to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_alias to INTACT_CURATOR;

SELECT 'Creating table "Updating rights on ia_institution_xref"';
grant select on ia_institution_xref to INTACT_SELECT;
grant select, insert,update,delete on ia_institution_xref to INTACT_CURATOR;

SELECT 'Creating table "Updating rights on ia_institution2annot"';
grant select on ia_institution2annot to INTACT_SELECT;
grant select, insert,update,delete on ia_institution2annot to INTACT_CURATOR;

SELECT 'Creating table "Updating rights on ia_component2exp_preps"';
grant select on ia_component2exp_preps to INTACT_SELECT;
grant select, insert,update,delete on ia_component2exp_preps to INTACT_CURATOR;

SELECT 'Creating table "Updating rights on ia_component2part_detect"';
grant select on ia_component2part_detect to INTACT_SELECT;
grant select, insert,update,delete on ia_component2part_detect to INTACT_CURATOR;


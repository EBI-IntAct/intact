
-------------------------------------
-- Update right on created objects --
-------------------------------------

PROMPT Creating table "Updating rights on IA_IMEX_IMPORT"
grant select on IA_IMEX_IMPORT to INTACT_SELECT ;
grant select,insert,update,delete on IA_IMEX_IMPORT to INTACT_CURATOR;

PROMPT Creating table "Updating rights on IA_IMEX_IMPORT_PUB"
grant select on IA_IMEX_IMPORT_PUB to INTACT_SELECT ;
grant select,insert,update,delete on IA_IMEX_IMPORT_PUB to INTACT_CURATOR;

---------------------------------------------
-- Update right on respective audit tables --
---------------------------------------------

PROMPT Creating table "Updating rights on IA_IMEX_IMPORT_AUDIT"
grant select on IA_IMEX_IMPORT_AUDIT to INTACT_SELECT ;
grant select,insert,update,delete on IA_IMEX_IMPORT_AUDIT to INTACT_CURATOR;

PROMPT Creating table "Updating rights on IA_IMEX_IMPORT_PUB_AUDIT"
grant select on IA_IMEX_IMPORT_PUB_AUDIT to INTACT_SELECT ;
grant select,insert,update,delete on IA_IMEX_IMPORT_PUB_AUDIT to INTACT_CURATOR;
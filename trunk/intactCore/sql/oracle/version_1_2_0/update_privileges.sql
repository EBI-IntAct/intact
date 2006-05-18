------------------------------------
-- Update right on created objects
------------------------------------

-- IA_Publication
PROMPT Creating table "Updating rights on IA_Publication"
grant select on IA_Publication to INTACT_SELECT;
grant select, insert,update,delete on IA_Publication to INTACT_CURATOR;

-- IA_Pub2Annot
PROMPT Creating table "Updating rights on IA_Pub2Annot"
grant select on IA_Pub2Annot to INTACT_SELECT;
grant select, insert,update,delete on IA_Pub2Annot to INTACT_CURATOR;

-- IA_Key_Assigner_Request
PROMPT Creating table "Updating rights on IA_Key_Assigner_Request"
grant select on IA_Key_Assigner_Request to INTACT_SELECT;
grant select, insert,update,delete on IA_Key_Assigner_Request to INTACT_CURATOR;


---------------------------------
-- Update right on audit tables
---------------------------------

-- IA_Publication_audit
PROMPT Creating table "Updating rights on IA_Publication_audit"
grant select on IA_Publication_audit to INTACT_SELECT;
grant select, insert,update,delete on IA_Publication_audit to INTACT_CURATOR;

-- IA_Pub2Annot_audit
PROMPT Creating table "Updating rights on IA_Pub2Annot_audit"
grant select on IA_Pub2Annot_audit to INTACT_SELECT;
grant select, insert,update,delete on IA_Pub2Annot_audit to INTACT_CURATOR;

-- IA_Key_Assigner_Request_audit
PROMPT Creating table "Updating rights on IA_Key_Assigner_Request_audit"
grant select on IA_Key_Assigner_Request_audit to INTACT_SELECT;
grant select, insert,update,delete on IA_Key_Assigner_Request_audit to INTACT_CURATOR;


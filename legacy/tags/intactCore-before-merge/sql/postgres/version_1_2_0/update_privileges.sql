/*-----------------------------------
-- Update right on created objects
-----------------------------------*/

/* IA_Publication */
SELECT 'Creating table "Updating rights on IA_Publication';
grant select on IA_Publication to INTACT_SELECT;
grant select, insert,update,delete on IA_Publication to INTACT_CURATOR;

/* IA_Pub2Annot */
SELECT 'Creating table "Updating rights on IA_Pub2Annot';
grant select on IA_Pub2Annot to INTACT_SELECT;
grant select, insert,update,delete on IA_Pub2Annot to INTACT_CURATOR;

/* IA_Key_Assigner_Request */
SELECT 'Creating table "Updating rights on IA_Key_Assigner_Request';
grant select on IA_Key_Assigner_Request to INTACT_SELECT;
grant select, insert,update,delete on IA_Key_Assigner_Request to INTACT_CURATOR;




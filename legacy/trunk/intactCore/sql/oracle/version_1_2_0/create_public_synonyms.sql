
@setup_tablespaces.sql

-------------------------------------------------
-- Creating public synonyms for new tables
-------------------------------------------------
PROMPT "Creating public synonym IA_Publication"
create public synonym IA_Publication for INTACT.IA_Publication;

PROMPT "Creating public synonym IA_Pub2Annot"
create public synonym IA_Pub2Annot for INTACT.IA_Pub2Annot;

PROMPT "Creating public synonym IA_Key_Assigner_Request"
create public synonym IA_Key_Assigner_Request for INTACT.IA_Key_Assigner_Request;


---------------------------------------------
-- Creating public synonyms for Audit table
---------------------------------------------

PROMPT "Creating public synonym IA_Publication_audit"
create public synonym IA_Publication_audit for INTACT.IA_Publication_audit;

PROMPT "Creating public synonym IA_Pub2Annot_audit"
create public synonym IA_Pub2Annot_audit for INTACT.IA_Pub2Annot_audit;

PROMPT "Creating public synonym IA_Key_Assigner_Request_audit"
create public synonym IA_Key_Assigner_Request_audit for INTACT.IA_Key_Assigner_Request_audit;

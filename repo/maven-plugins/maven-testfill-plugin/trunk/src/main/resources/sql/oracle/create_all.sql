    


  
DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactIndexTablespace        = INTACT_IDX
DEFINE intactLobTablespace          = INTACT_TAB
DEFINE intactAuditTablespace        = INTACT_TAB
DEFINE intactIndexAuditTablespace   = INTACT_IDX
DEFINE spoolFile                    = ddl.sql







CLEAR SCREEN

SET   SERVEROUT ON
SET   FEEDBACK OFF
SET   VERIFY OFF
SET   LINES 150
SET   PAGES 20000
SET   DOC OFF

PROMPT **********************************************************************************
PROMPT
PROMPT   Copyright (c) 2003 The European Bioinformatics Institute, and others.
PROMPT   All rights reserved. Please see the file LICENSE
PROMPT   in the root directory of this distribution.
PROMPT
PROMPT  **********************************************************************************
PROMPT
PROMPT   Package:    IntAct core
PROMPT
PROMPT   Purpose:    Create schema objects for IntAct
PROMPT
PROMPT   Usage:      sqlplus username/password@INSTANCE @create_all.sql
PROMPT
PROMPT   $Date: 2004-06-15 16:09:37 +0100 (dt, 15 jun 2004) $
PROMPT   $Locker$
PROMPT
PROMPT *********************************************************************************

begin
   dbms_output.enable(1000000000000);
   for r_usr in (select user from dual) loop
      dbms_output.put_line ('You are currently connected as user : '||r_usr.user );
   end loop;
end;
/
PROMPT
PROMPT  The script will use designated tablespaces to store various types of objects.
PROMPT  You should agree with these tablespaces or otherwsie alter this script to set
PROMPT  your prefered own values !
PROMPT
PROMPT  Tablespaces :
PROMPT    - Tablespace &&intactMainTablespace will be used for the Intact main tables
PROMPT    - Tablespace &&intactIndexTablespace will be used for the Intact indexes
PROMPT    - Tablespace &&intactLobTablespace will be used to store the CLOB of PolymerSeq
PROMPT    - Tablespace &&intactAuditTablespace  will be used for the Intact audit tables    
PROMPT    - Tablespace &&intactIndexAuditTablespace  will be used for the Intact audit indexes
PROMPT    - File &&spoolfile will be used as a working file
PROMPT
PROMPT  Do you agree with these settings ?????
PROMPT  --> To ABORT press CTRL+C, to continue press ENTER.
ACCEPT continue PROMPT >>>


PROMPT *********************************************************************************/
PROMPT Starting creation.
PROMPT
PROMPT First dropping possibly existing objects ..
@drop_tables.sql
PROMPT
PROMPT
PROMPT Creating tables and sequences ..
PROMPT **********************************
@create_tables.sql
PROMPT
PROMPT
PROMPT Creating audit tables ...
PROMPT **********************************
@create_audit_tables.sql
PROMPT
PROMPT
PROMPT Creating audit triggers ...
PROMPT **********************************
@create_audit_triggers.sql
PROMPT
PROMPT
PROMPT Creating parentAc triggers ...
PROMPT **********************************
@create_xref_trigger.sql
PROMPT
PROMPT
PROMPT Done !
PROMPT
PROMPT

exit;





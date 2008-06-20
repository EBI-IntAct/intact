set serveroutput on size 1000000

spool install_190.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

-- Define here Oracle global parameters used across installation files
DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactAuditTablespace        = INTACT_TAB

-- D003
DEFINE intactIndexTablespace        = INTACT_TAB

-- ZPRO
-- DEFINE intactIndexTablespace        = INTACT_IDX


PROMPT *********************************************************************************/
PROMPT "Creating new table ia_confidence..."
@010_create_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit tables for new tables..."
@020_create_audit_table.sql


PROMPT *********************************************************************************/
PROMPT "Create audit triggers for the parameter tables..."
PROMPT
@030_create_audit_trigger.sql


PROMPT *********************************************************************************/
PROMPT "Add indexes for the parameters..."
PROMPT
@040_add_index.sql


PROMPT *********************************************************************************/
PROMPT "Update the mi_identifier column in ia_controlledvocab table"
PROMPT

-- TODO 050_rename_identifier_column.sql


PROMPT *********************************************************************************/
PROMPT "Update the data stored in ia_controlledvocab.identifier"
PROMPT

-- TODO 060_update_identifier_column.sql


PROMPT *********************************************************************************/
PROMPT "Update the length of the dbi_key and dbi_value in the table ia_db_info"
PROMPT

-- TODO 

-- TODO 070_update_db_info_columns.sql
PROMPT *********************************************************************************/
PROMPT "Update schema version to 1.9.0"
PROMPT
UPDATE ia_db_info
SET value = '1.9.0'
WHERE UPPER(dbi_key) ='SCHEMA_VERSION';

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;



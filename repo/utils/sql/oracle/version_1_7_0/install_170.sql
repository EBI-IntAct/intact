set serveroutput on size 1000000

spool install_170.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

-- Define here Oracle global parameters used across installation files
DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactAuditTablespace        = INTACT_TAB

-- D003
DEFINE intactIndexTablespace        = INTACT_TAB

-- ZPRO
-- DEFINE intactIndexTablespace        = INTACT_IDX


PROMPT *********************************************************************************/
PROMPT "Creating new tables required for migration..."
PROMPT
@100_create_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating missing audit tables..."
PROMPT
@110_create_audit_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit triggers..."
PROMPT
@120_create_triggers.sql


PROMPT *********************************************************************************/
PROMPT "updating priviledges on new tables..."
PROMPT
@130_update_privileges.sql q


PROMPT *********************************************************************************/
PROMPT "Creating public synonyms..."
PROMPT
@140_create_public_synonyms.sql


PROMPT *********************************************************************************/
PROMPT "Update schema version to 1.7.0"
PROMPT
UPDATE ia_db_info
set value = '1.7.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;

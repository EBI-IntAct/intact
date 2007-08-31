set serveroutput on size 1000000

spool install_163.log

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
PROMPT "Updating IA_COMPONENT and IA_COMPONENT_AUDIT..."
PROMPT
@110_update_ia_component.sql


PROMPT *********************************************************************************/
PROMPT "Updating IA_COMPONENT audit trigger..."
PROMPT
@120_update_ia_component_audit_trigger.sql


PROMPT *********************************************************************************/
PROMPT "Creating missing audit tables..."
PROMPT
@130_create_audit_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit triggers..."
PROMPT
@140_create_triggers.sql


PROMPT *********************************************************************************/
PROMPT "Creating public synonyms..."
PROMPT
@150_create_public_synonyms.sql


PROMPT *********************************************************************************/
PROMPT "updating priviledges on new tables..."
PROMPT
@160_update_privileges.sql


PROMPT *********************************************************************************/
PROMPT "Update schema version to 1.6.3"
PROMPT
UPDATE ia_db_info
set value = '1.6.3'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;

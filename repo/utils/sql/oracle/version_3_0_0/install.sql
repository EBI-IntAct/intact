set serveroutput on size 1000000

spool install_300.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

-- Define here Oracle global parameters used across installation files
DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactAuditTablespace        = INTACT_TAB

-- ZDEV
DEFINE intactIndexTablespace        = INTACT_TAB

-- ZPRO
-- DEFINE intactIndexTablespace        = INTACT_IDX


PROMPT *********************************************************************************/
PROMPT "Verifying current database schema version..."
@check_schema_version.sql


PROMPT *********************************************************************************/
PROMPT "Creating tables..."
@create_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit tables..."
@create_audit_table.sql

PROMPT *********************************************************************************/
PROMPT "Creating grants..."
@create_grants.sql

PROMPT *********************************************************************************/
PROMPT "Creating public synonyms..."
@create_public_synonyms.sql

@alter_tables.sql

PROMPT *********************************************************************************/
PROMPT "Creating audit triggers..."
@create_audit_trigger.sql

PROMPT *********************************************************************************/
PROMPT "Update schema version to 3.0.0"
PROMPT
--UPDATE ia_db_info
--SET value = '3.0.0'
--WHERE UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;



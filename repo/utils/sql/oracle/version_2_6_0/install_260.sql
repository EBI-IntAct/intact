set serveroutput on size 1000000

spool install_260.log

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
@005_check_schema_version.sql


PROMPT *********************************************************************************/
@010_alter_columns.sql

PROMPT *********************************************************************************/
PROMPT "Update schema version to 2.6.0"
PROMPT
--UPDATE ia_db_info
--SET value = '2.6.0'
--WHERE UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;



set serveroutput on size 1000000

spool install_210.log

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
PROMPT "Creating new table ia_confidence..."
@010_create_tables.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit tables for new tables..."
@020_create_audit_table.sql


PROMPT *********************************************************************************/
PROMPT "Creating grants on created tables..."
@023_create_grants.sql


PROMPT *********************************************************************************/
PROMPT "Creating public synonyms for newly created tables..."
@026_create_public_synonyms.sql


PROMPT *********************************************************************************/
PROMPT "Create audit triggers for the parameter tables..."
PROMPT
@030_create_audit_trigger.sql


PROMPT *********************************************************************************/
PROMPT "Add indexes for the parameters..."
PROMPT
@040_add_index.sql


PROMPT *********************************************************************************/
PROMPT "Extending length of Xref.primaryId..."
PROMPT
@050_extend_xref_primaryid.sql



PROMPT *********************************************************************************/
PROMPT "Update schema version to 2.1.0"
PROMPT
UPDATE ia_db_info
SET value = '2.1.0'
WHERE UPPER(dbi_key) ='SCHEMA_VERSION';

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;



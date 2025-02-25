set serveroutput on size 1000000

spool install_180.log

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
PROMPT
@010_create_tables.sql

PROMPT *********************************************************************************/
PROMPT "Creating new table ia_confidence_audit..."
PROMPT
@030_create_confidence_audit_table.sql

PROMPT *********************************************************************************/
PROMPT "Creating new trigger for confidence audit..."
PROMPT
@060_create_confidence_audit_trigger.sql

PROMPT *********************************************************************************/
PROMPT "Adding new field in ia_controlledvocab..."
PROMPT
@100_update_cv_table.sql

PROMPT *********************************************************************************/
PROMPT "Updating ia_controlledvocab audit table..."
PROMPT
@110_update_cv_audit_table.sql

PROMPT *********************************************************************************/
PROMPT "Updating ia_controlledvocab audit trigger..."
PROMPT
@120_create_triggers.sql

PROMPT *********************************************************************************/
PROMPT "Updating MI identifier in table ia_controlledvocab..."
PROMPT
@130_update_mi_identifier.sql

PROMPT *********************************************************************************/
PROMPT "Add index on MI identifier..."
PROMPT
@140_add_index.sql

PROMPT *********************************************************************************/
PROMPT "Creating public synonyms..."
PROMPT
@150_create_public_synonyms.sql

PROMPT *********************************************************************************/
PROMPT "Updating privileges..."
PROMPT
@160_update_privileges.sql

PROMPT *********************************************************************************/
PROMPT "Update schema version to 1.8.0"
PROMPT
UPDATE ia_db_info
set value = '1.8.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;

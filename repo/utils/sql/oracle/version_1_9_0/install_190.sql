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
PROMPT "Update the mi_identifier column in ia_controlledvocab table"
PROMPT

@050_rename_identifier_field.sql


PROMPT *********************************************************************************/
PROMPT "Re-creating the trigger on ia_controlledvocab to reflect the field change..."
@055_replace_trigger.sql


PROMPT *********************************************************************************/
PROMPT "Update the data stored in ia_controlledvocab.identifier"
PROMPT

@060_update_identifier_field.sql


PROMPT *********************************************************************************/
PROMPT "Update the length of the dbi_key and dbi_value in the table ia_db_info"
PROMPT

@070_update_db_info_table.sql

PROMPT *********************************************************************************/
PROMPT "Creating table ia_component2exprole"
PROMPT

@080_create_component2exprole_table.sql


PROMPT *********************************************************************************/
PROMPT "Creating audit table for ia_component2exprole"
PROMPT

@090_create_component2exprole_audit_table.sql


PROMPT *********************************************************************************/
PROMPT "Creating grants for ia_component2exprole"
PROMPT

@100_create_component2exprole_grant.sql


PROMPT *********************************************************************************/
PROMPT "Creating public synonym for ia_component2exprole"
PROMPT

@110_create_component2exprole_public_synonyms.sql

PROMPT *********************************************************************************/
PROMPT "Creating audit trigger for ia_component2exprole"
PROMPT

@120_create_component2exprole_audit_trigger.sql

PROMPT *********************************************************************************/
PROMPT "Adding index for for ia_component2exprole"
PROMPT

@130_add_component2exprole_index.sql

PROMPT *********************************************************************************/
PROMPT "Insert data into ia_component2exprole from component table"
PROMPT

@140_insert_datainto_component2exprole.sql

PROMPT *********************************************************************************/
PROMPT "Creating grants for component2exprole_audit" 
PROMPT

@150_create_component2exprole_audit_grant.sql


PROMPT *********************************************************************************/
PROMPT "Creating public synonyms for component2exprole_audit" 
PROMPT

@160_create_component2exprole_audit_public_synonyms.sql


PROMPT *********************************************************************************/
PROMPT "Creating sequence for ia_controlledvocab" 
PROMPT

@180_create_cvlocalseq_sequence.sql


PROMPT *********************************************************************************/
PROMPT "Creating public synonyms for cv_local_seq" 
PROMPT

@190_create_cvlocalseq_public_synonym.sql

PROMPT *********************************************************************************/
PROMPT "Creating grants for cv_local_seq" 
PROMPT

@200_create_cvlocalseq_grants.sql


PROMPT *********************************************************************************/
PROMPT "Resize shortlabel column length to 256" 
PROMPT

@210_enlarge_shortlabel_columns.sql


--DO NOT EXECUTE THIS STATEMENT
PROMPT *********************************************************************************/
PROMPT "Altering table component" 
PROMPT
--DO NOT EXECUTE THIS STATEMENT
--@170_alter_component_table.sql
--DO NOT EXECUTE THIS STATEMENT



PROMPT *********************************************************************************/
PROMPT "Update schema version to 1.9.0"
PROMPT
UPDATE ia_db_info
SET value = '1.9.0'
WHERE UPPER(dbi_key) ='SCHEMA_VERSION';

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;



set serveroutput on size 1000000

spool install_130.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

-- Define here global parameters used across installation files
DEFINE intactMainTablespace         = INTACT_TAB

-- D003
DEFINE intactIndexTablespace        = INTACT_TAB

-- ZPRO
-- DEFINE intactIndexTablespace        = INTACT_IDX


PROMPT *********************************************************************************/
PROMPT "Creating new Alias tables (where we will store subset of IA_ALIAS)"
PROMPT
@010_create_alias_tables.sql

PROMPT *********************************************************************************/
PROMPT "Update IA_COMPONENT - components are now Annotated Objects"
PROMPT "                      Add IA_COMPONENT_ALIAS"
PROMPT "                      Add IA_COMPONENT_XREF"
PROMPT "                      Add IA_COMPONENT2ANNOT"
PROMPT
@020_component_modifications.sql

PROMPT *********************************************************************************/
PROMPT "Creating IA_COMPONENT2ANNOT audit table and trigger."
PROMPT
@025_create_component2annot_audit.sql

PROMPT *********************************************************************************/
PROMPT "Creating Alias deletion trigger - avoids constraint violation."
PROMPT
@030_create_alias_deletion_triggers.sql

PROMPT *********************************************************************************/
PROMPT "Creating Xref deletion trigger - avoids constraint violation."
PROMPT
@040_create_xref_deletion_triggers.sql

PROMPT *********************************************************************************/
PROMPT "Creating Alias audit tables (7 of them)."
PROMPT
@050_create_alias_audit_tables.sql

PROMPT *********************************************************************************/
PROMPT "Creating Xref audit trigger."
PROMPT
@060_create_alias_audit_triggers.sql

PROMPT *********************************************************************************/
PROMPT "Creating Xref audit tables (7 of them)."
PROMPT
@070_create_xref_audit_tables.sql

PROMPT *********************************************************************************/
PROMPT "Creating Alias audit trigger."
PROMPT
@080_create_xref_audit_triggers.sql

PROMPT *********************************************************************************/
PROMPT "Grant roles - read to INTACT_SELECT, read/write to INTACT_CURATOR."
PROMPT
@090_grant_permissions.sql

PROMPT *********************************************************************************/
PROMPT "Creating public synonyms."
PROMPT
@100_create_public_synonyms.sql

PROMPT *********************************************************************************/
PROMPT "Creating procedure to split IA_ALIAS into specific alias tables."
PROMPT
@110_create_procedure_split_alias.sql

PROMPT *********************************************************************************/
PROMPT "Creating procedure to split IA_ALIAS_AUDIT into specific alias audit tables."
PROMPT
@120_create_procedure_split_alias_audit.sql

PROMPT *********************************************************************************/
PROMPT "Creating procedure to split IA_XREF into specific xref tables."
PROMPT
@130_create_procedure_split_xref.sql

PROMPT *********************************************************************************/
PROMPT "Creating procedure to split IA_XREF_AUDIT into specific xref audit tables."
PROMPT
@140_create_procedure_split_xref_audit.sql

PROMPT *********************************************************************************/
PROMPT "Migrating IA_ALIAS data into specific tables."
PROMPT
@150_run_alias_table_split.sql

PROMPT *********************************************************************************/
PROMPT "Migrating IA_XREF data into specific tables."
PROMPT
@160_run_xref_table_split.sql

PROMPT *********************************************************************************/
PROMPT "Migrating IA_ALIAS_AUDIT data into specific tables."
PROMPT
@170_run_alias_audit_split.sql

PROMPT *********************************************************************************/
PROMPT "Migrating IA_XREF_AUDIT data into specific tables."
PROMPT
@180_run_xref_audit_split.sql


UPDATE ia_db_info
set value = '1.3.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

set serveroutput on size 1000000

spool install_160.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

-- Define here global parameters used across installation files
DEFINE intactMainTablespace         = INTACT_TAB

-- D003
DEFINE intactIndexTablespace        = INTACT_TAB

-- ZPRO
-- DEFINE intactIndexTablespace        = INTACT_IDX


PROMPT *********************************************************************************/
PROMPT "disabling the component audit trigger"
PROMPT
@100_disable_component_table_audit_trigger.sql


PROMPT *********************************************************************************/
PROMPT "Change ia_component.role to ia_component.experimentalrole_ac"
PROMPT
@110_rename_role_field_in_ia_component.sql

PROMPT *********************************************************************************/
PROMPT "Change ia_component_audit.role to ia_component_audit.experimentalrole_ac"
PROMPT
@120_rename_role_field_in_ia_component_audit.sql

PROMPT *********************************************************************************/
PROMPT "Add ia_component.biologicalrole_ac and ia_component.ia_identmethod_ac fields"
PROMPT
@130_update_component.sql

PROMPT ******************************************************************************************/
PROMPT "Add ia_component_audit.biologicalrole_ac and ia_component_audit.ia_identmethod_ac fields"
PROMPT
@140_update_component_audit.sql

PROMPT *********************************************************************************/
PROMPT "Replace the component audit trigger so that it take into account biologicalrole_ac, experimentrole_ac and identmethod_ac"
PROMPT
@150_replace_component_audit_triggers.sql

PROMPT *********************************************************************************/
PROMPT "enable component audit trigger"
PROMPT
@160_enable_component_table_audit_trigger.sql

PROMPT *********************************************************************************/
PROMPT "Update the objclass from uk.ac.ebi.intact.model.CvComponentRole to either :     "
PROMPT "     uk.ac.ebi.intact.model.CvExperimentalRole                                  "
PROMPT "     uk.ac.ebi.intact.model.CvBiologicalRole                                    "
PROMPT
@170_update_objclass_CvComponentRole.sql

-- This is not necessary anymore !!
-- PROMPT *********************************************************************************/
-- PROMPT "Add CvBiologicalRole( 'unspecified role' )    "
-- PROMPT
-- @180_insert_unspecified_as_biologicalRole.sql

PROMPT *********************************************************************************/
PROMPT "Add CvExperimentalRole( 'unspecified role' )    "
PROMPT
@185_insert_unspecified_as_experimentalRole.sql

PROMPT *********************************************************************************/
PROMPT "Add CvExperimentalRole( 'self' )    "
PROMPT
@187_insert_self_as_experimentalRole.sql

PROMPT *********************************************************************************/
PROMPT "Create the component split procedure "
PROMPT
@190_create_split_role_procedure.sql

PROMPT *********************************************************************************/
PROMPT "Run the component split...    "
PROMPT
@200_run_split_role.sql

PROMPT *********************************************************************************/
PROMPT "Update schema version"
PROMPT
UPDATE ia_db_info
set value = '1.6.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off

exit;

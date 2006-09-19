set serveroutput on size 1000000

spool install_130.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

PROMPT *********************************************************************************/
PROMPT Component related changes
PROMPT
@component_modifications.sql

PROMPT *********************************************************************************/
PROMPT Adding multiple Alias tables
PROMPT
@add_multiple_alias_tables.sql

PROMPT *********************************************************************************/
PROMPT Granting permissions
PROMPT
@grant_permissions.sql

PROMPT *********************************************************************************/
PROMPT Creating synonyms
PROMPT
@create_synonyms.sql

UPDATE ia_db_info
set value = '1.3.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off
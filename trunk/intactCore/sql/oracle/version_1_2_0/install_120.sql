set serveroutput on size 1000000

spool install_120.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

PROMPT *********************************************************************************/
PROMPT Setting up global settings...
PROMPT
@setup_tablespaces.sql


PROMPT *********************************************************************************/
PROMPT Creating tables...
PROMPT
@create_tables.sql


PROMPT *********************************************************************************/
PROMPT Creating audit tables...
PROMPT
@create_audit_tables.sql


PROMPT *********************************************************************************/
PROMPT Creating audit triggers...
PROMPT
@create_audit_trigger.sql


PROMPT *********************************************************************************/
PROMPT Updating tables privileges...
PROMPT
@update_privileges.sql


PROMPT *********************************************************************************/
PROMPT Creating public synonyms...
PROMPT
@create_public_synonyms.sql

UPDATE ia_db_info
set value = '1.2.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off
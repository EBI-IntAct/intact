set serveroutput on size 1000000

spool install_123.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

PROMPT *********************************************************************************/
PROMPT Creating synonym
PROMPT
@create_synonyms.sql

UPDATE ia_db_info
set value = '1.2.3'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off
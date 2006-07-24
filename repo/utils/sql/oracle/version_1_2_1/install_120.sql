set serveroutput on size 1000000

spool install_121.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

PROMPT *********************************************************************************/
PROMPT Adding extra column in ia_xref (parentClass)
PROMPT
@add_column.sql


PROMPT *********************************************************************************/
PROMPT Updating the new column
PROMPT
@update_ia_xref_parentClass.sql


UPDATE ia_db_info
set value = '1.2.1'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off
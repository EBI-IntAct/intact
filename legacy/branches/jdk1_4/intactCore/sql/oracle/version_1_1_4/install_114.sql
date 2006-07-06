set serveroutput on size 1000000

spool install_114.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;


@@replace_triggers.sql


PROMPT UDPATE IA_DB_INFO
UPDATE ia_db_info
set value = '1.1.4'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;


select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;


spool off
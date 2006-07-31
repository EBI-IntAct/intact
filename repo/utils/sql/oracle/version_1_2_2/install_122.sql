set serveroutput on size 1000000

spool install_122.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

PROMPT *********************************************************************************/
PROMPT Adding multiple Xref tables
PROMPT
@add_multiple_xref_tables.sql

PROMPT *********************************************************************************/
PROMPT Creating synonims and Grants
PROMPT
@create_synonyms_and_grants.sql

UPDATE ia_db_info
set value = '1.2.2'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;

spool off
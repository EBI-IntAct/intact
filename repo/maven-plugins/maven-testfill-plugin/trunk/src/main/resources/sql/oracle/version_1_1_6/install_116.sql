set serveroutput on size 1000000

spool install_116.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;


create index i_xref$qualifier_ac on IA_XREF (qualifier_ac) TABLESPACE INTACT_IDX;


@@cr_view.sql


UPDATE ia_db_info
set value = '1.1.6'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;


select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;


spool off
--  IN AUDIT TABLES JUST COPY OF THE OLD VALUES OF THE RECORD WHIICH IS CHANGED OR DELETED MAIN TABLE

set serveroutput on

spool cr_user_tracking_triggers.sql
set term off
set doc off

begin

   dbms_output.enable(1000000000000);

   dbms_output.put_line('set serveroutput on');
   dbms_output.put_line('spool cr_user_tracking_triggers.sql');
   dbms_output.put_line (chr(10));

   for r_tab in (select lower(table_name) table_name
                 from   user_tables
                 where  table_name not like '%AUDIT'
                 and    table_name not in
                        ( 'IA_GODENS_BINARY'
                         ,'IA_GODENS_GODAG'
                         ,'IA_GODENS_GODAGDENORM'
                         ,'IA_GODENS_GOPROT'
                         ,'IA_GODENS_DENSITY'
                         ,'PROTEIN_TO_DELETE'
                         ,'VIEW_CV'
                         ,'VIEW_INTERACTOR_COUNT'
                         ,'IA_DB_INFO'
                         ,'PMID_TO_EXCLUDE'
                         ,'IA_PUBMED'
                         ,'IA_IMEX_IMPORT_PUTB'
                         ,'IA_IMEX_IMPORT'

                         ,'IA_PAYG'
                         ,'IA_PAYG_CURRENT_EDGE'
                         ,'IA_PAYG_TEMP_NODE'

                         ,'IA_INTERACTIONS'

                         ,'IA_STATISTICS'
                         ,'IA_EXPSTATISTICS'
                         ,'IA_BIOSOURCESTATISTICS'
                         ,'IA_DETECTIONMETHODSSTATISTICS'

                         ,'IA_SEARCH'
                         ,'PLAN_TABLE'
                         ,'IA_KEY_ASSIGNER_REQUEST'
                        )
   ) loop

      dbms_output.put_line ('PROMPT Creating user tracking trigger for '||r_tab.table_name);
      dbms_output.put_line ('create or replace trigger '|| substr ('trgTrk_'||r_tab.table_name,1,30) );
      dbms_output.put_line (chr(9)||'before insert or update');
      dbms_output.put_line (chr(9)||'on '||r_tab.table_name);
      dbms_output.put_line (chr(9)||'for each row');
      dbms_output.put_line (' ');
      dbms_output.put_line ('begin');
      dbms_output.put_line ('');
      
      dbms_output.put_line (chr(9)||'if :new.userstamp is null then    ');
      dbms_output.put_line (chr(9)||chr(9)||'select USER into :new.userstamp from dual;');
      dbms_output.put_line (chr(9)||'end if;');
      
      dbms_output.put_line ('');
      
      dbms_output.put_line (chr(9)||'if inserting and :new.created_user is null then    ');
      dbms_output.put_line (chr(9)||chr(9)||'select USER into :new.created_user from dual;');
      dbms_output.put_line (chr(9)||'end if;');

      dbms_output.put_line ('');

      dbms_output.put_line ('end;        ');
      dbms_output.put_line ('/           ');
      dbms_output.put_line ('show error');

      dbms_output.put_line ('');
      dbms_output.put_line ('');

   end loop;

   dbms_output.put_line('spool off');
end;
/

spool off
set term on

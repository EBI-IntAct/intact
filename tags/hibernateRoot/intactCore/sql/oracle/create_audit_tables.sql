
spool &spoolFile
set term off
set doc off

DECLARE
      cursor c_cols (p_table_name user_tables.table_name%type) is
      select cols.column_name
            ,decode (DATA_TYPE, 'NUMBER'   , 'NUMBER(' ||DATA_PRECISION|| DECODE (DATA_SCALE, 0, ')', ','||DATA_SCALE||')' )
                              , 'VARCHAR2' , 'VARCHAR2(' ||DATA_LENGTH||')'
                              ,  DATA_TYPE ) datatype
            ,decode (nullable, 'Y' ,' NULL',' NOT NULL') nullInd
            ,pkColumns.column_name pk_col
      from   user_tab_columns  cols
            ,( select pkcols.column_name column_name
                     ,pkcols.table_name  table_name
                from  user_cons_columns pkcols
                     ,user_constraints   con
               where  pkcols.constraint_name = con.constraint_name
                 and  con.constraint_type = 'P') pkColumns
      where  cols.column_name = pkColumns.column_name (+)
      and    cols.table_name  = pkColumns.table_name  (+)
      and    cols.table_name = p_table_name
      order  by cols.table_name, cols.column_id
      ;
   l_comma char(1):=',';
   v_table_name user_tables.table_name%TYPE;
   v_table_count INTEGER;

begin

   dbms_output.enable(1000000000000);

   for r_tab in (select table_name 
                 from   user_tables 
                 where  table_name not like '%AUDIT' 
                 and    table_name not in
                        ( 'IA_GODENS_BINARY'
                         ,'IA_GODENS_GODAG'
                         ,'IA_GODENS_GODAGDENORM'
                         ,'IA_GODENS_GOPROT'
                         ,'IA_GODENS_DENSITY'

                         ,'IA_PAYG'
                         ,'IA_PAYG_CURRENT_EDGE'
                         ,'IA_PAYG_TEMP_NODE'

                         ,'IA_INTERACTIONS'

                         ,'IA_STATISTICS'
                         ,'IA_EXPSTATISTICS'
                         ,'IA_BIOSOURCESTATISTICS'

                         ,'IA_SEARCH'
                        )
                )
   loop

      -- check if that audit table already exists, if not, try to create it.
      v_table_name := r_tab.table_name || '_audit';

      select count(1) into v_table_count
      from   user_tables
      where  UPPER(table_name) = UPPER(v_table_name);

      if (v_table_count = 0) then

          dbms_output.put_line ('PROMPT Creating table "'|| v_table_name ||'"');
          dbms_output.put_line ('create table '|| v_table_name);
          dbms_output.put_line (chr(9)||'(');

          dbms_output.put_line (chr(9)||chr(9)||' EVENT                              CHAR(1) NOT NULL ');
          for r_cols in c_cols (r_tab.table_name) loop
             dbms_output.put_line (chr(9)||chr(9)||l_comma||rpad(r_cols.column_name,35)||r_cols.datatype||r_cols.nullInd);
          end loop;
          dbms_output.put_line (chr(9)||')');
          dbms_output.put_line ('tablespace &intactAuditTablespace');
          dbms_output.put_line ('/');
          dbms_output.put_line (' ');

          l_comma := '';

      else

          dbms_output.put_line ('PROMPT table "'|| v_table_name ||'" already exists. Delete it first if you want to recreate it.');

      end if;

      -- FOR PERFORMANCE REASON, WE DON'T CREATE INDEX ON THE AUDIT TABLE.

   end loop;
end;
/

spool off
set term on
@&spoolFile ;


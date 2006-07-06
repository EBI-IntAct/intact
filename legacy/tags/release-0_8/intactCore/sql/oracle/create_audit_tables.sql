
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
begin

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
                        )      
                )
   loop

      dbms_output.put_line ('PROMPT Creating table "'||lower(r_tab.table_name)||'_audit"');
      dbms_output.put_line ('create table '||r_tab.table_name||'_audit');
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

      dbms_output.put_line ('CREATE INDEX i_'||r_tab.table_name||'_audit');
      dbms_output.put_line ('ON '||r_tab.table_name||'_audit' );
      dbms_output.put_line (chr(9)||'(');
      for r_cols in c_cols (r_tab.table_name) loop
         if r_cols.pk_col is not null then
            dbms_output.put_line (chr(9)||chr(9)||l_comma||r_cols.column_name);
            l_comma := ',';
         end if;
      end loop;
      dbms_output.put_line (chr(9)||')');
      dbms_output.put_line ('TABLESPACE &intactIndexAuditTablespace ');
      dbms_output.put_line ('/ ');
   end loop;
end;
/

spool off
set term on
@&spoolFile ;


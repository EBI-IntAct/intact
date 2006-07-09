

declare
  analysis varchar2(4000);
begin
  for r_analyze in (select table_name from user_tables) loop
    analysis := 'analyze table '||r_analyze.table_name||' compute statistics for table for all indexes for all indexed columns';
    execute immediate (analysis);
  end loop;
end;
/


-- Perform a check after the alias split script has been run, it counts
-- the sum of all aliases tables. That count should be matching the count of
-- rows in IA_ALIAS. The tables sub tables are :
--    * IA_biosource_alias
--    * IA_controlledvocab_alias
--    * IA_feature_alias
--    * IA_interactor_alias
--    * IA_experiment_alias
--    * IA_publication_alias
--    * IA_component_alias
DECLARE

  v_count_alias INTEGER;
  v_diff        INTEGER;
  v_count       INTEGER;
  v_total       INTEGER;
  
BEGIN

  dbms_output.enable(1000000000000);

  v_total := 0;

  SELECT count(1) into v_count_alias
  FROM IA_ALIAS;  
  dbms_output.put_line( '-----------------------------------------------' );
  dbms_output.put_line( 'IA_ALIAS: ' || v_count_alias || ' row(s)' );
  dbms_output.put_line( '-----------------------------------------------' );
  
  SELECT count(1) into v_count
  FROM IA_biosource_alias;  
  dbms_output.put_line( 'IA_biosource_alias:       ' || v_count || ' row(s)' );
  v_total := v_total + v_count;

  SELECT count(1) into v_count
  FROM IA_controlledvocab_alias;  
  dbms_output.put_line( 'IA_controlledvocab_alias: ' || v_count || ' row(s)' );
  v_total := v_total + v_count;
  
  SELECT count(1) into v_count
  FROM IA_feature_alias;  
  dbms_output.put_line( 'IA_feature_alias:         ' || v_count || ' row(s)' );
  v_total := v_total + v_count;

  SELECT count(1) into v_count
  FROM IA_interactor_alias;  
  dbms_output.put_line( 'IA_interactor_alias:      ' || v_count || ' row(s)' );
  v_total := v_total + v_count;
  
  SELECT count(1) into v_count
  FROM IA_experiment_alias;  
  dbms_output.put_line( 'IA_experiment_alias:      ' || v_count || ' row(s)' );
  v_total := v_total + v_count;
  
  SELECT count(1) into v_count
  FROM IA_publication_alias;  
  dbms_output.put_line( 'IA_publication_alias:     ' || v_count || ' row(s)' );
  v_total := v_total + v_count;
  
  SELECT count(1) into v_count
  FROM IA_component_alias;  
  dbms_output.put_line( 'IA_component_alias:       ' || v_count || ' row(s)' );
  v_total := v_total + v_count;

  dbms_output.put_line( '===============================================' );
  dbms_output.put_line( 'Sum of all sub-tables: ' || v_total || ' row(s)' );
  
  v_diff := v_count_alias - v_total;
  if ( v_diff = 0 ) then
     dbms_output.put_line( 'All aliases were transfered successfully.' );
  elsif ( v_diff > 0 ) then
     dbms_output.put_line( v_diff || ' row(s) were not transfered to sub-tables.'  );
  else
     -- v_diff < 0
     dbms_output.put_line( 'Sub tables contain more rows that the original table: ' || ( v_diff * -1 ) );
  end if;  
  
END;
/


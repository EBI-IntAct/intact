--PROMPT Updating table "ia_component2exprole"
set serveroutput on

DECLARE
  component_ac 			ia_component2exprole.component_ac%TYPE;
  experimentalrole_ac 		ia_component2exprole.experimentalrole_ac%TYPE;
 
  v_count_exprole         INTEGER := 0;


  CURSOR cursor_component IS
    SELECT ac,experimentalrole_ac  
    FROM ia_component;
    
BEGIN
  DBMS_OUTPUT.ENABLE(1000000000000);

  OPEN cursor_component;
  
  LOOP
  
  	FETCH cursor_component INTO component_ac, experimentalrole_ac;
  	EXIT WHEN cursor_component%NOTFOUND;
    v_count_exprole := v_count_exprole+1;    
  	INSERT INTO ia_component2exprole(component_ac, experimentalrole_ac) VALUES (component_ac, experimentalrole_ac); 

  
  END LOOP;	
  
  CLOSE cursor_component; 

DBMS_OUTPUT.PUT_LINE('Count of rows inserted: ' || v_count_exprole);
end;
/


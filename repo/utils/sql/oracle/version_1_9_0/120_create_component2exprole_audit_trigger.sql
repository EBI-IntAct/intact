create or replace trigger trgAud_ia_component2exprole
	before update or delete
	on ia_component2exprole
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_component2exprole_audit 
		( event 
		, component_ac
		, experimentalrole_ac
		, created
		, updated
		, userstamp
		, created_user
		)
	values
		( l_event 
		, :old.component_ac
		, :old.experimentalrole_ac
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.created_user
		);
end;        
/


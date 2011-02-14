set serveroutput on
spool cr_audit_triggers.sql

PROMPT Creating audit trigger for ia_component_confidence

create or replace trigger trgAud_ia_component_confidence
	before update or delete
	on ia_component_confidence
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_component_confidence_audit 
		( event 
		, ac
		, created
		, created_user
		, updated
		, userstamp
		, deprecated
		, value
		, confidencetype_ac
		, component_ac
		)
	values
		( l_event 
		, :old.ac
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.deprecated
		, :old.value
		, :old.confidencetype_ac
		, :old.component_ac
		);
end;        
/         


show error




spool off

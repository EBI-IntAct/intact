
PROMPT Creating table "IA_COMPONENT2ANNOT_AUDIT"
create table IA_COMPONENT2ANNOT_AUDIT
	(
		 EVENT                              CHAR(1)      NOT NULL 
		,COMPONENT_AC                       VARCHAR2(30) NOT NULL
		,ANNOTATION_AC                      VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/



PROMPT "Creating audit trigger for IA_COMPONENT2ANNOT"
create or replace trigger trgAud_ia_component2annot
	before update or delete
	on ia_component2annot
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_component2annot_audit 
		( event 
		, component_ac
		, annotation_ac
		)
	values
		( l_event 
		, :old.component_ac
		, :old.annotation_ac
		);
end;        
/           
show error



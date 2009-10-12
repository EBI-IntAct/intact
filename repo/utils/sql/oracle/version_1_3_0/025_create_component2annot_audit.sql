
PROMPT Creating table "IA_COMPONENT2ANNOT_AUDIT"
create table IA_COMPONENT2ANNOT_AUDIT
	(
	    EVENT                   CHAR(1)        NOT NULL 
	 ,  COMPONENT_AC            VARCHAR2(30)   NOT NULL
	 ,  ANNOTATION_AC           VARCHAR2(30)   NOT NULL
         ,  created                 DATE           DEFAULT SYSDATE
         ,  updated                 DATE           DEFAULT SYSDATE
         ,  userstamp               VARCHAR2(30)   DEFAULT USER
         ,  created_user            VARCHAR2(30)   DEFAULT USER NOT NULL ENABLE 
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
		, created
		, updated
		, userstamp
		, created_user
		)
	values
		( l_event 
		, :old.component_ac
		, :old.annotation_ac
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.created_user
		);
end;        
/ 
show error



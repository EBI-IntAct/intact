set serveroutput on
-- spool cr_audit_triggers.sql

PROMPT Creating audit trigger for ia_biosource_alias
create or replace trigger trgAud_ia_biosource_alias
	before update or delete
	on ia_biosource_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_biosource_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_component_alias
create or replace trigger trgAud_ia_component_alias
	before update or delete
	on ia_component_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_component_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_controlledvocab_alias
create or replace trigger trgAud_ia_cv_alias
	before update or delete
	on ia_controlledvocab_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_controlledvocab_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_experiment_alias
create or replace trigger trgAud_ia_experiment_alias
	before update or delete
	on ia_experiment_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_experiment_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_feature_alias
create or replace trigger trgAud_ia_feature_alias
	before update or delete
	on ia_feature_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_feature_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_interactor_alias
create or replace trigger trgAud_ia_interactor_alias
	before update or delete
	on ia_interactor_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_interactor_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_publication_alias
create or replace trigger trgAud_ia_publication_alias
	before update or delete
	on ia_publication_alias
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
		:new.updated := sysdate; 
		:new.userstamp := user;  
	end if ;  
	
	
	insert into ia_publication_alias_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;        
/           
show error


-- spool off


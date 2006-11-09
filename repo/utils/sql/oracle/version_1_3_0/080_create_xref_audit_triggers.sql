set serveroutput on
-- spool cr_audit_triggers.sql


PROMPT Creating audit trigger for ia_biosource_xref
create or replace trigger trgAud_ia_biosource_xref
	before update or delete
	on ia_biosource_xref
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
	
	
	insert into ia_biosource_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_component_xref
create or replace trigger trgAud_ia_component_xref
	before update or delete
	on ia_component_xref
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
	
	
	insert into ia_component_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_controlledvocab_xref
create or replace trigger trgAud_ia_controlledvocab_xref
	before update or delete
	on ia_controlledvocab_xref
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
	
	
	insert into ia_controlledvocab_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_experiment_xref
create or replace trigger trgAud_ia_experiment_xref
	before update or delete
	on ia_experiment_xref
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
	
	
	insert into ia_experiment_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_feature_xref
create or replace trigger trgAud_ia_feature_xref
	before update or delete
	on ia_feature_xref
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
	
	
	insert into ia_feature_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_interactor_xref
create or replace trigger trgAud_ia_interactor_xref
	before update or delete
	on ia_interactor_xref
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
	
	
	insert into ia_interactor_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_publication_xref
create or replace trigger trgAud_ia_publication_xref
	before update or delete
	on ia_publication_xref
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
	
	
	insert into ia_publication_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;        
/           
show error




PROMPT Creating audit trigger for ia_xref
create or replace trigger trgAud_ia_xref
	before update or delete
	on ia_xref
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
	
	
	insert into ia_xref_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, owner_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		, parentclass
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		, :old.parentclass
		);
end;        
/           
show error


--spool off


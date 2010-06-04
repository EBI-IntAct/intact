set serveroutput on


PROMPT Creating auto owner trigger for table 'ia_alias'
create or replace trigger trgOwn_ia_alias
	before insert or update
	on ia_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_annotation'
create or replace trigger trgOwn_ia_annotation
	before insert or update
	on ia_annotation
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_biosource'
create or replace trigger trgOwn_ia_biosource
	before insert or update
	on ia_biosource
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_biosource_alias'
create or replace trigger trgOwn_ia_biosource_alias
	before insert or update
	on ia_biosource_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_biosource_xref'
create or replace trigger trgOwn_ia_biosource_xref
	before insert or update
	on ia_biosource_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_component'
create or replace trigger trgOwn_ia_component
	before insert or update
	on ia_component
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_component_alias'
create or replace trigger trgOwn_ia_component_alias
	before insert or update
	on ia_component_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_component_parameter'
create or replace trigger trgOwn_ia_component_parameter
	before insert or update
	on ia_component_parameter
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_component_xref'
create or replace trigger trgOwn_ia_component_xref
	before insert or update
	on ia_component_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_confidence'
create or replace trigger trgOwn_ia_confidence
	before insert or update
	on ia_confidence
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_controlledvocab'
create or replace trigger trgOwn_ia_controlledvocab
	before insert or update
	on ia_controlledvocab
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_controlledvocab_alias'
create or replace trigger trgOwn_ia_controlledvocab_alia
	before insert or update
	on ia_controlledvocab_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_controlledvocab_xref'
create or replace trigger trgOwn_ia_controlledvocab_xref
	before insert or update
	on ia_controlledvocab_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_experiment_alias'
create or replace trigger trgOwn_ia_experiment_alias
	before insert or update
	on ia_experiment_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_experiment_xref'
create or replace trigger trgOwn_ia_experiment_xref
	before insert or update
	on ia_experiment_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_feature'
create or replace trigger trgOwn_ia_feature
	before insert or update
	on ia_feature
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_feature_alias'
create or replace trigger trgOwn_ia_feature_alias
	before insert or update
	on ia_feature_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_feature_xref'
create or replace trigger trgOwn_ia_feature_xref
	before insert or update
	on ia_feature_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_institution'
create or replace trigger trgOwn_ia_institution
	before insert or update
	on ia_institution
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_institution_alias'
create or replace trigger trgOwn_ia_institution_alias
	before insert or update
	on ia_institution_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_institution_xref'
create or replace trigger trgOwn_ia_institution_xref
	before insert or update
	on ia_institution_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_intactnode'
create or replace trigger trgOwn_ia_intactnode
	before insert or update
	on ia_intactnode
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_interaction_parameter'
create or replace trigger trgOwn_ia_interaction_paramete
	before insert or update
	on ia_interaction_parameter
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_interactor_alias'
create or replace trigger trgOwn_ia_interactor_alias
	before insert or update
	on ia_interactor_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_interactor_xref'
create or replace trigger trgOwn_ia_interactor_xref
	before insert or update
	on ia_interactor_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_publication_alias'
create or replace trigger trgOwn_ia_publication_alias
	before insert or update
	on ia_publication_alias
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_publication_xref'
create or replace trigger trgOwn_ia_publication_xref
	before insert or update
	on ia_publication_xref
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error




PROMPT Creating auto owner trigger for table 'ia_range'
create or replace trigger trgOwn_ia_range
	before insert or update
	on ia_range
	for each row
 
declare
begin
 
	if inserting and :new.owner_ac is NULL then    
		:new.owner_ac := 'EBI-10';
	elsif updating and :new.owner_ac is NULL then 
		:new.owner_ac := 'EBI-10';
	end if ;  
	
end;        
/           
show error



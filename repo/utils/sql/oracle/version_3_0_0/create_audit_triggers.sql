set serveroutput on
spool cr_audit_triggers.sql

PROMPT Creating audit trigger for ia_experimental_causal_relationship_audit

create or replace trigger trgAud_ia_experimental_causal_relationship_audit
	before update or delete
	on ia_experimental_causal_relationship
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_experimental_causal_relationship_audit 
		( event 
		, id
		, created
		, created_user
		, updated
		, userstamp
		, deprecated
		, relation_type_ac
		, experimental_target_ac
		, modelled_target_ac
                , source_ac
		)
	values
		( l_event 
		, :old.id
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.deprecated
		, :old.relation_type_ac
		, :old.experimental_target_ac
		, :old.modelled_target_ac
                , :old.source_ac
		);
end; 

PROMPT Creating audit trigger for ia_modelled_causal_relationship_audit

create or replace trigger trgAud_ia_modelled_causal_relationship_audit
	before update or delete
	on ia_modelled_causal_relationship_audit
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_modelled_causal_relationship_audit 
		( event 
		, id
		, created
		, created_user
		, updated
		, userstamp
		, deprecated
		, relation_type_ac
		, target_ac
                , source_ac
		)
	values
		( l_event 
		, :old.id
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.deprecated
		, :old.relation_type_ac
		, :old.target_ac
                , :old.source_ac
		);
end;  

PROMPT Creating audit trigger for ia_institution_audit

create or replace TRIGGER "INTACT".trgAud_ia_institution
	before update or delete
	on ia_institution
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_institution_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, owner_ac
		, shortlabel
		, fullname
		, created_user
                , publication_ac
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.owner_ac
		, :old.shortlabel
		, :old.fullname
		, :old.created_user
                , :old.publication_ac
		);
end; 

PROMPT Creating audit trigger for ia_feature_audit
create or replace TRIGGER "INTACT".trgAud_ia_feature
	before update or delete
	on ia_feature
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_feature_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, component_ac
		, identification_ac
		, featuretype_ac
		, linkedfeature_ac
		, shortlabel
		, fullname
		, owner_ac
		, created_user
                , role_ac
                , category
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.component_ac
		, :old.identification_ac
		, :old.featuretype_ac
		, :old.linkedfeature_ac
		, :old.shortlabel
		, :old.fullname
		, :old.owner_ac
		, :old.created_user
                , :old.role_ac
                , :old.category
		);
end; 

PROMPT Creating audit trigger for ia_modelled_feature2linked_feature_audit_audit

create or replace trigger trgAud_ia_modelled_feature2linked_feature_audit_audit
	before update or delete
	on ia_modelled_feature2linked_feature
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_modelled_feature2linked_feature_audit 
		( event 
		, modelled_feature_ac
		, linked_feature_ac
                , created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event 
		, :old.modelled_feature_ac
		, :old.linked_feature_ac
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end; 

TRIGGER "INTACT".trgTrk_ia_modelled_feature2linked_feature
	before insert or update
	on ia_modelled_feature2linked_feature
	for each row
 
begin

	if :new.userstamp is null then    
		select USER into :new.userstamp from dual;
	end if;

	if inserting and :new.created_user is null then    
		select USER into :new.created_user from dual;
	end if;

end;

PROMPT Creating audit trigger for ia_feature_evidence2linked_feature_audit

create or replace trigger trgAud_ia_feature_evidence2linked_feature_audit
	before update or delete
	on ia_feature_evidence2linked_feature
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_feature_evidence2linked_feature_audit 
		( event 
		, feature_evidence_ac
		, linked_feature_ac
                , created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event 
		, :old.feature_evidence_ac
		, :old.linked_feature_ac
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end;

TRIGGER "INTACT".trgTrk_feature_evidence2linked_feature
	before insert or update
	on ia_feature_evidence2linked_feature
	for each row
 
begin

	if :new.userstamp is null then    
		select USER into :new.userstamp from dual;
	end if;

	if inserting and :new.created_user is null then    
		select USER into :new.created_user from dual;
	end if;

end;

PROMPT Creating audit trigger for ia_feature2detection_method_audit

create or replace trigger trgAud_ia_feature2detection_method_audit
	before update or delete
	on ia_feature2detection_method_audit
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_feature2detection_method_audit 
		( event 
		, method_ac
		, feature_ac
                , created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event 
		, :old.method_ac
		, :old.feature_ac
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end;

TRIGGER "INTACT".trgTrk_feature2detection_method
	before insert or update
	on ia_feature2detection_method
	for each row
 
begin

	if :new.userstamp is null then    
		select USER into :new.userstamp from dual;
	end if;

	if inserting and :new.created_user is null then    
		select USER into :new.created_user from dual;
	end if;

end;

PROMPT Creating audit trigger for ia_feature_parameter_audit

create or replace trigger trgAud_ia_feature_parameter_audit
	before update or delete
	on ia_feature_parameter
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_feature_parameter_audit 
		( event 
		, ac
		, created
		, created_user
		, updated
		, userstamp
		, base
		, uncertainty
		, exponent
                , factor
                , parametertype_ac
                , parameterunit_ac
                , parent_ac
		)
	values
		( l_event 
		, :old.ac
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.base
		, :old.uncertainty
		, :old.exponent
		, :old.factor
                , :old.parametertype_ac
                , :old.parameterunit_ac
                , :old.parent_ac
		);
end; 

PROMPT Creating audit trigger for ia_range_audit

create or replace TRIGGER "INTACT".trgAud_ia_range
	before update or delete
	on ia_range
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


	insert into ia_range_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, undetermined
		, link
		, feature_ac
		, owner_ac
		, fromintervalstart
		, fromintervalend
		, fromfuzzytype_ac
		, tointervalstart
		, tointervalend
		, tofuzzytype_ac
		, sequence
		, created_user
		, full_sequence
		, upstream_sequence
		, downstream_sequence
                , resulting_sequence
                , modelled_participant_ac
                , participant_evidence_ac
		)
	values
		( l_event
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.undetermined
		, :old.link
		, :old.feature_ac
		, :old.owner_ac
		, :old.fromintervalstart
		, :old.fromintervalend
		, :old.fromfuzzytype_ac
		, :old.tointervalstart
		, :old.tointervalend
		, :old.tofuzzytype_ac
		, :old.sequence
		, :old.created_user
		, :old.full_sequence
		, :old.upstream_sequence
		, :old.downstream_sequence
                , :old.resulting_sequence
                , :old.modelled_participant_ac
                , :old.participant_evidence_ac
		);
end;

PROMPT Creating audit trigger for ia_modelled_resulting_sequence_xref_audit
create or replace TRIGGER "INTACT".trgAud_ia_modelled_resulting_sequence_xref
	before update or delete
	on ia_modelled_resulting_sequence_xref
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_modelled_resulting_sequence_xref_audit 
		( event 
		, ac
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;

PROMPT Creating audit trigger for ia_experimental_resulting_sequence_xref_audit
create or replace TRIGGER "INTACT".trgAud_ia_experimental_resulting_sequence_xref
	before update or delete
	on ia_experimental_resulting_sequence_xref
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_experimental_resulting_sequence_xref_audit 
		( event 
		, ac
		, created
		, updated
		, userstamp
		, qualifier_ac
		, database_ac
		, parent_ac
		, primaryid
		, secondaryid
		, dbrelease
		, created_user
		)
	values
		( l_event 
		, :old.ac
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.qualifier_ac
		, :old.database_ac
		, :old.parent_ac
		, :old.primaryid
		, :old.secondaryid
		, :old.dbrelease
		, :old.created_user
		);
end;

PROMPT Creating audit trigger for ia_interactor_audit
create or replace TRIGGER "INTACT".trgAud_ia_interactor
	before update or delete
	on ia_interactor
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_interactor_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, kd
		, crc64
		, formof
		, proteinform_ac
		, objclass
		, biosource_ac
		, interactiontype_ac
		, shortlabel
		, fullname
		, owner_ac
		, interactortype_ac
		, created_user
                , status_ac
                , owner_pk
                , reviewer_pk
                , evidence_type_ac
                , category
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.kd
		, :old.crc64
		, :old.formof
		, :old.proteinform_ac
		, :old.objclass
		, :old.biosource_ac
		, :old.interactiontype_ac
		, :old.shortlabel
		, :old.fullname
		, :old.owner_ac
		, :old.interactortype_ac
		, :old.created_user
                , :old.status_ac
                , :old.owner_pk
                , :old.reviewer_pk
                , :old.evidence_type_ac
                , :old.category
		);
end;

PROMPT Creating audit trigger for ia_interactor_pool2interactor_audit

create or replace trigger trgAud_ia_interactor_pool2interactor_audit
	before update or delete
	on ia_interactor_pool2interactor_audit
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_interactor_pool2interactor_audit 
		( event 
		, interactor_pool_ac
		, interactor_ac
                , created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event 
		, :old.interactor_pool_ac
		, :old.interactor_ac
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end;

TRIGGER "INTACT".trgTrk_interactor_pool2interactor
	before insert or update
	on ia_interactor_pool2interactor
	for each row
 
begin

	if :new.userstamp is null then    
		select USER into :new.userstamp from dual;
	end if;

	if inserting and :new.created_user is null then    
		select USER into :new.created_user from dual;
	end if;

end;

PROMPT Creating audit trigger for ia_component_audit

create or replace TRIGGER "INTACT".trgAud_ia_component
	before update or delete
	on ia_component
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_component_audit 
		( event 
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, interactor_ac
		, interaction_ac
		, experimentalrole_ac
		, expressedin_ac
		, owner_ac
		, stoichiometry
		, created_user
		, shortlabel
		, fullname
		, biologicalrole_ac
		, identmethod_ac
                , maxstoichiometry
                , category
		)
	values
		( l_event 
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.interactor_ac
		, :old.interaction_ac
		, :old.experimentalrole_ac
		, :old.expressedin_ac
		, :old.owner_ac
		, :old.stoichiometry
		, :old.created_user
		, :old.shortlabel
		, :old.fullname
		, :old.biologicalrole_ac
		, :old.identmethod_ac
                , :old.maxstoichiometry
                , :old.category
		);
end;  

PROMPT Creating audit trigger for ia_variable_parameter_audit

create or replace TRIGGER "INTACT".trgAud_ia_variable_parameter
	before update or delete
	on ia_variable_parameter
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_variable_parameter_audit 
		( event 
		, id
		, created
		, updated
		, userstamp
		, created_user
                , description
                , experiment_ac
                , unit_ac
		)
	values
		( l_event 
		, :old.id
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.created_user
                , :old.description
                , :old.experiment_ac
                , :old.unit_ac
		);
end; 

PROMPT Creating audit trigger for ia_variable_parameter_value_audit

create or replace TRIGGER "INTACT".trgAud_ia_variable_parameter_value
	before update or delete
	on ia_variable_parameter_value
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_variable_parameter_audit 
		( event 
		, id
		, created
		, updated
		, userstamp
		, created_user
                , variableorder
                , value
                , parameter_id
		)
	values
		( l_event 
		, :old.id
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.created_user
                , :old.variableorder
                , :old.value
                , :old.parameter_id
		);
end; 

PROMPT Creating audit trigger for ia_interaction_var_parameters_audit

create or replace TRIGGER "INTACT".trgAud_ia_interaction_var_parameters_value
	before update or delete
	on ia_interaction_var_parameters
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_interaction_var_parameters_value_audit 
		( event 
		, id
		, created
		, updated
		, userstamp
		, created_user
                , parent_ac
		)
	values
		( l_event 
		, :old.id
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.created_user
                , :old.parent_ac
		);
end; 

PROMPT Creating audit trigger for ia_varset2parametervalue_audit

create or replace trigger trgAud_ia_varset2parametervalue_audit
	before update or delete
	on ia_varset2parametervalue_audit
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_varset2parametervalue_audit 
		( event 
		, varset_id
		, parametervalue_id
                , created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event 
		, :old.varset_id
		, :old.parametervalue_id
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end;

TRIGGER "INTACT".trgTrk_varset2parametervalue
	before insert or update
	on ia_varset2parametervalue
	for each row
 
begin

	if :new.userstamp is null then    
		select USER into :new.userstamp from dual;
	end if;

	if inserting and :new.created_user is null then    
		select USER into :new.created_user from dual;
	end if;

end; 

PROMPT Creating audit trigger for ia_ia_complex_lifecycle_event_audit

create or replace trigger trgAud_ia_complex_lifecycle_event_audit
	before update or delete
	on ia_complex_lifecycle_event_audit
	for each row
 
declare
	l_event char(1);
begin
 
	if deleting then    
		l_event := 'D';
	elsif updating then 
		l_event := 'U';
	end if ;  
	
	
	insert into ia_complex_lifecycle_event_audit 
		( event 
                , created
		, created_user
		, updated
		, userstamp
                , note
                , when_date
                , event_ac
                , user_ac
                , complex_ac
		)
	values
		( l_event 
                , :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
                , :old.note
                , :old.when_date
                , :old.event_ac
                , :old.user_ac
                , :old.complex_ac
		);
end;   
/         

show error




spool off

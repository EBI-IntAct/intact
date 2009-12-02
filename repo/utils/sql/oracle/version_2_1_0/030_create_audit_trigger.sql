set serveroutput on
spool cr_audit_triggers.sql


PROMPT Creating audit trigger for ia_component_parameter
create or replace trigger trgAud_ia_component_parameter
	before update or delete
	on ia_component_parameter
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


	insert into ia_component_parameter_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, component_ac
		, parametertype_ac
		, parameterunit_ac
		, owner_ac
		, experiment_ac
		, base
		, exponent
		, uncertainty
		, factor
		, created_user
		)
	values
		( l_event
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.component_ac
		, :old.parametertype_ac
		, :old.parameterunit_ac
		, :old.owner_ac
		, :old.experiment_ac
		, :old.base
		, :old.exponent
		, :old.uncertainty
		, :old.factor
		, :old.created_user
		);
end;
/
show error




PROMPT Creating audit trigger for ia_interaction_parameter
create or replace trigger trgAud_ia_interaction_paramete
	before update or delete
	on ia_interaction_parameter
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


	insert into ia_interaction_parameter_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, interaction_ac
		, parametertype_ac
		, parameterunit_ac
		, owner_ac
		, experiment_ac
		, base
		, exponent
		, uncertainty
		, factor
		, created_user
		)
	values
		( l_event
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.interaction_ac
		, :old.parametertype_ac
		, :old.parameterunit_ac
		, :old.owner_ac
		, :old.experiment_ac
		, :old.base
		, :old.exponent
		, :old.uncertainty
		, :old.factor
		, :old.created_user
		);
end;
/
show error




spool off
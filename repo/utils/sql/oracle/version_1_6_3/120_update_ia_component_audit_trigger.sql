
PROMPT Creating audit trigger for ia_component
create or replace trigger trgAud_ia_component
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
		:new.updated := sysdate;
		:new.userstamp := user;
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
		);
end;
/
show error

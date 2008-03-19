PROMPT Creating audit trigger for ia_confidence

create or replace trigger trgAud_ia_confidence
	before update or delete
	on ia_confidence
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


	insert into ia_confidence_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, interaction_ac
		, confidencetype_ac
		, owner_ac
		, value
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
		, :old.confidencetype_ac
		, :old.owner_ac
		, :old.value
		, :old.created_user
		);
end;
/
show error
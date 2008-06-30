PROMPT Creating audit trigger for ia_controlledvocab
create or replace trigger trgAud_ia_controlledvocab
	before update or delete
	on ia_controlledvocab
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


	insert into ia_controlledvocab_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, owner_ac
		, objclass
		, shortlabel
		, fullname
		, created_user
		, identifier
		)
	values
		( l_event
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.owner_ac
		, :old.objclass
		, :old.shortlabel
		, :old.fullname
		, :old.created_user
		, :old.identifier
		);
end;
/
show error
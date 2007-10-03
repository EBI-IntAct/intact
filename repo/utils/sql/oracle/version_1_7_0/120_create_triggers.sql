PROMPT Creating audit trigger for ia_imex_import
create or replace trigger trgAud_ia_imex_import
	before update or delete
	on ia_imex_object
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


	insert into ia_imex_import_audit
		( event
		, id
		, original_filename
		, status
		, pmid
		, message
		, provider_ac
		, created
		, created_user
		, updated
		, userstamp
		)
	values
		( l_event
		, :old.id
		, :old.original_filename
		, :old.status
		, :old.pmid
		, :old.message
		, :old.provider_ac
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		);
end;
/
show error
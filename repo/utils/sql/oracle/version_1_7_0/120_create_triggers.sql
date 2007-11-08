PROMPT Creating audit trigger for ia_imex_import
create or replace trigger trgAud_ia_imex_import
	before update or delete
	on ia_imex_import
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
		, created
		, created_user
		, updated
		, userstamp
		, activationtype
		, count_total
		, count_not_found
		, count_failed
		, import_date
		, repository
		)
	values
		( l_event
		, :old.id
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.activationtype
		, :old.count_total
		, :old.count_not_found
		, :old.count_failed
		, :old.import_date
		, :old.repository
		);
end;
/
show error




PROMPT Creating audit trigger for ia_imex_import_pub
create or replace trigger trgAud_ia_imex_import_pub
	before update or delete
	on ia_imex_import_pub
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


	insert into ia_imex_import_pub_audit
		( event
		, pmid
		, created
		, created_user
		, updated
		, userstamp
		, message
		, original_filename
		, release_date
		, status
		, imeximport_id
		, provider_ac
		)
	values
		( l_event
		, :old.pmid
		, :old.created
		, :old.created_user
		, :old.updated
		, :old.userstamp
		, :old.message
		, :old.original_filename
		, :old.release_date
		, :old.status
		, :old.imeximport_id
		, :old.provider_ac
		);
end;
/
show error
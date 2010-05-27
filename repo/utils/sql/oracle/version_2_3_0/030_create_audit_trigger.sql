PROMPT Creating audit trigger for ia_range
create or replace trigger trgAud_ia_range
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
		, fromintervalestart
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
		, :old.fromintervalestart
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
		);
end;
/
show error
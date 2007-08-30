
PROMPT Creating audit trigger for ia_component2exp_preps
create or replace trigger trgAud_ia_component2exp_preps
	before update or delete
	on ia_component2exp_preps
	for each row

declare
	l_event char(1);
begin

	if deleting then
		l_event := 'D';
	elsif updating then
		l_event := 'U';
		:new.userstamp := user;
		:new.updated := sysdate;
	end if ;


	insert into ia_component2exp_preps_audit
		( event
		, component_ac
		, cvobject_ac
		, deprecated
		, created
		, userstamp
		, updated
		, created_user
		)
	values
		( l_event
		, :old.component_ac
		, :old.cvobject_ac
		, :old.deprecated
		, :old.created
		, :old.userstamp
		, :old.updated
		, :old.created_user
		);
end;
/
show error




PROMPT Creating audit trigger for ia_component2part_detect
create or replace trigger trgAud_ia_component2part_detec
	before update or delete
	on ia_component2part_detect
	for each row

declare
	l_event char(1);
begin

	if deleting then
		l_event := 'D';
	elsif updating then
		l_event := 'U';
		:new.userstamp := user;
		:new.updated := sysdate;
	end if ;


	insert into ia_component2part_detect_audit
		( event
		, component_ac
		, cvobject_ac
		, deprecated
		, created
		, userstamp
		, updated
		, created_user
		)
	values
		( l_event
		, :old.component_ac
		, :old.cvobject_ac
		, :old.deprecated
		, :old.created
		, :old.userstamp
		, :old.updated
		, :old.created_user
		);
end;
/
show error




PROMPT Creating audit trigger for ia_institution2annot
create or replace trigger trgAud_ia_institution2annot
	before update or delete
	on ia_institution2annot
	for each row

declare
	l_event char(1);
begin

	if deleting then
		l_event := 'D';
	elsif updating then
		l_event := 'U';
		:new.userstamp := user;
		:new.updated := sysdate;
	end if ;


	insert into ia_institution2annot_audit
		( event
		, institution_ac
		, annotation_ac
		, deprecated
		, created
		, userstamp
		, updated
		, created_user
		)
	values
		( l_event
		, :old.institution_ac
		, :old.annotation_ac
		, :old.deprecated
		, :old.created
		, :old.userstamp
		, :old.updated
		, :old.created_user
		);
end;
/
show error




PROMPT Creating audit trigger for ia_institution_alias
create or replace trigger trgAud_ia_institution_alias
	before update or delete
	on ia_institution_alias
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


	insert into ia_institution_alias_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, aliastype_ac
		, parent_ac
		, owner_ac
		, name
		, created_user
		)
	values
		( l_event
		, :old.ac
		, :old.deprecated
		, :old.created
		, :old.updated
		, :old.userstamp
		, :old.aliastype_ac
		, :old.parent_ac
		, :old.owner_ac
		, :old.name
		, :old.created_user
		);
end;
/
show error




PROMPT Creating audit trigger for ia_institution_xref
create or replace trigger trgAud_ia_institution_xref
	before update or delete
	on ia_institution_xref
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


	insert into ia_institution_xref_audit
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

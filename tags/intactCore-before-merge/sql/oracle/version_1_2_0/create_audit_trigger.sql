-----------------------------
-- Creating audit triggers
-----------------------------

@setup_tablespaces.sql

PROMPT Creating audit trigger for ia_publication
create or replace trigger trgAud_ia_publication
	before update or delete
	on ia_publication
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


	insert into ia_publication_audit
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
		, pmid
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
		, :old.pmid
		);
end;
/
show error

----------------------------------------------------------------------
----------------------------------------------------------------------
----------------------------------------------------------------------

PROMPT Creating audit trigger for ia_pub2annot
create or replace trigger trgAud_ia_pub2annot
	before update or delete
	on ia_pub2annot
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


	insert into ia_pub2annot_audit
		( event
		, publication_ac
		, annotation_ac
		, deprecated
		, created
		, userstamp
		, updated
		, created_user
		)
	values
		( l_event
		, :old.publication_ac
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

----------------------------------------------------------------------
----------------------------------------------------------------------
----------------------------------------------------------------------

PROMPT Creating audit trigger for ia_experiment
create or replace trigger trgAud_ia_experiment
	before update or delete
	on ia_experiment
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


	insert into ia_experiment_audit
		( event
		, ac
		, deprecated
		, created
		, updated
		, userstamp
		, biosource_ac
		, detectmethod_ac
		, identmethod_ac
		, relatedexperiment_ac
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
		, :old.biosource_ac
		, :old.detectmethod_ac
		, :old.identmethod_ac
		, :old.relatedexperiment_ac
		, :old.owner_ac
		, :old.shortlabel
		, :old.fullname
		, :old.created_user
		, :old.publication_ac
		);
end;
/
show error

----------------------------------------------------------------------
----------------------------------------------------------------------
----------------------------------------------------------------------

PROMPT Creating audit trigger for ia_key_assigner_request
create or replace trigger trgAud_ia_key_assigner_request
	before update or delete
	on ia_key_assigner_request
	for each row

declare
	l_event char(1);
begin

	if deleting then
		l_event := 'D';
	elsif updating then
		l_event := 'U';
	end if ;


	insert into ia_key_assigner_request_audit
		( event
		, submission
		, fromid
		, toid
		, partner
		, service_url
		, created
		, created_user
		)
	values
		( l_event
		, :old.submission
		, :old.fromid
		, :old.toid
		, :old.partner
		, :old.service_url
		, :old.created
		, :old.created_user
		);
end;
/
show error

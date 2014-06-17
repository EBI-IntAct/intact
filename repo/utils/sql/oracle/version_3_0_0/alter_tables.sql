alter table ia_institution drop column url;  -- drop institution url
alter table ia_institution_audit drop column url;  -- drop institution url

alter table ia_institution drop column postaladdress;  -- drop institution postal address
alter table ia_institution_audit drop column postaladdress;  -- drop institution postal address

alter table ia_feature add (role_ac varchar2(30 char), category varchar2(31 char)); -- add role ac foreign key and category
alter table ia_feature_audit add (role_ac varchar2(30 char), category varchar2(31 char)); -- add role ac foreign key and category
alter table ia_feature add constraint FK_feature2role foreign key (role_ac) references ia_controlledvocab;

alter table ia_range add (resulting_sequence clob, modelled_participant_ac varchar2(30 char), experimental_participant_ac varchar2(30 char)); -- add resulting sequence and participant ref in range
alter table ia_range_audit add (resulting_sequence clob, modelled_participant_ac varchar2(30 char), experimental_participant_ac varchar2(30 char)); -- add resulting sequence and participant refin range
alter table ia_range add constraint FK_range2modelledparticipant foreign key (modelled_participant_ac) references ia_component;
alter table ia_range add constraint FK_feature2participantevidence foreign key (participant_evidence_ac) references ia_component;

alter table ia_interactor add (status_ac varchar2(30 char), owner_pk varchar2(30 char), reviewer_pk varchar2(30 char), evidence_type_ac varchar2(30 char), category varchar2(31 char)); -- add complex lifecycle properties and evidence type and category
alter table ia_interactor_audit add (status_ac varchar2(30 char), owner_pk varchar2(30 char), reviewer_pk varchar2(30 char), evidence_type_ac varchar2(30 char), category varchar2(31 char)); -- add complex lifecycle properties and evidence type and category
alter table ia_interactor add constraint FK_complex2status foreign key (status_ac) references ia_controlledvocab;
alter table ia_interactor add constraint FK_complex2owner foreign key (owner_pk) references ia_user;
alter table ia_interactor add constraint FK_complex2reviewer foreign key (reviewer_pk) references ia_user;
alter table ia_interactor add constraint FK_complex2evidencetype foreign key (evidence_type_ac) references ia_controlledvocab;

alter table ia_component add (maxstoichiometry number(10,0), category varchar2(31 char)); -- add max stoichiometry for participant and category
alter table ia_component_audit add (maxstoichiometry number(10,0), category varchar2(31 char)); -- add max stoichiometry for participant and category

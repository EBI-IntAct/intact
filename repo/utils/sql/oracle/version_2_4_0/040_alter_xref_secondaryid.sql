-------------------------------------
-- Alter secondaryId               --
-------------------------------------

PROMPT Creating table "Increasing secondaryId in xrefs to 256"
alter table ia_publication_xref modify (secondaryId varchar2(256));
alter table ia_interactor_xref modify (secondaryId varchar2(256));
alter table ia_experiment_xref modify (secondaryId varchar2(256));
alter table ia_feature_xref modify (secondaryId varchar2(256));
alter table ia_biosource_xref modify (secondaryId varchar2(256));
alter table ia_component_xref modify (secondaryId varchar2(256));
alter table ia_institution_xref modify (secondaryId varchar2(256));

alter table ia_publication_xref_audit modify (secondaryId varchar2(256));
alter table ia_interactor_xref_audit modify (secondaryId varchar2(256));
alter table ia_experiment_xref_audit modify (secondaryId varchar2(256));
alter table ia_feature_xref_audit modify (secondaryId varchar2(256));
alter table ia_biosource_xref_audit modify (secondaryId varchar2(256));
alter table ia_component_xref_audit modify (secondaryId varchar2(256));
alter table ia_institution_xref_audit modify (secondaryId varchar2(256));

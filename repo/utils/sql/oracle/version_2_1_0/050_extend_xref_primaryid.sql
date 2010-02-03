PROMPT "Extending the length of Xref.primaryId from 30 to 50 chars"

alter table IA_BIOSOURCE_XREF modify (primaryid varchar2(50));
alter table IA_COMPONENT_XREF modify (primaryid varchar2(50));
alter table IA_CONTROLLEDVOCAB_XREF modify (primaryid varchar2(50));
alter table IA_EXPERIMENT_XREF modify (primaryid varchar2(50));
alter table IA_FEATURE_XREF modify (primaryid varchar2(50));
alter table IA_INSTITUTION_XREF modify (primaryid varchar2(50));
alter table IA_INTERACTOR_XREF modify (primaryid varchar2(50));
alter table IA_PUBLICATION_XREF modify (primaryid varchar2(50));


-- grant permissions to the new tables



-- Alias tables
PROMPT 'Granting on Alias tables for INTACT_SELECT and INTACT_CURATOR ...'
grant select on IA_BioSource_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_BioSource_Alias to INTACT_CURATOR ;

grant select on IA_Experiment_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Experiment_Alias to INTACT_CURATOR ;

grant select on IA_ControlledVocab_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_ControlledVocab_Alias to INTACT_CURATOR ;

grant select on IA_Feature_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Feature_Alias to INTACT_CURATOR ;

grant select on IA_Interactor_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Interactor_Alias to INTACT_CURATOR ;

grant select on IA_Publication_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Publication_Alias to INTACT_CURATOR ;


-- Component's tables
PROMPT 'Granting on Component tables for INTACT_SELECT and INTACT_CURATOR ...'
grant select on IA_Component_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component_Alias to INTACT_CURATOR ;

grant select on IA_Component_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component_Xref to INTACT_CURATOR ;

grant select on IA_Component2Annot to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component2Annot to INTACT_CURATOR ;



-- Xref audit tables
PROMPT 'Granting on Xref tables for INTACT_SELECT and INTACT_CURATOR ...'
grant select on IA_BIOSOURCE_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_BIOSOURCE_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_COMPONENT_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_COMPONENT_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_CONTROLLEDVOCAB_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_CONTROLLEDVOCAB_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_EXPERIMENT_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_EXPERIMENT_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_FEATURE_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_FEATURE_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_INTERACTOR_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_INTERACTOR_XREF_AUDIT to INTACT_CURATOR ;
 
grant select on IA_PUBLICATION_XREF_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_PUBLICATION_XREF_AUDIT to INTACT_CURATOR ;



-- Alias audit tables
grant select on IA_BIOSOURCE_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_BIOSOURCE_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_COMPONENT_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_COMPONENT_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_CONTROLLEDVOCAB_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_CONTROLLEDVOCAB_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_EXPERIMENT_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_EXPERIMENT_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_FEATURE_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_FEATURE_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_INTERACTOR_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_INTERACTOR_ALIAS_AUDIT to INTACT_CURATOR ;
 
grant select on IA_PUBLICATION_ALIAS_AUDIT to INTACT_SELECT ;
grant select, insert,update,delete on IA_PUBLICATION_ALIAS_AUDIT to INTACT_CURATOR ;


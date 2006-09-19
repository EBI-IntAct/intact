-- grant permissions to the new tables
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

grant select on IA_Component_Alias to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component_Alias to INTACT_CURATOR ;

grant select on IA_Component_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component_Xref to INTACT_CURATOR ;

grant select on IA_Component2Annot to INTACT_SELECT ;
grant select, insert,update,delete on IA_Component2Annot to INTACT_CURATOR ;

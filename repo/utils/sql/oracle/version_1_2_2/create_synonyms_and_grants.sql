-- creates synonims and grants permissions for the xref tables

grant select on IA_BioSource_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_BioSource_Xref to INTACT_CURATOR ;

grant select on IA_Experiment_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Experiment_Xref to INTACT_CURATOR ;

grant select on IA_ControlledVocab_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_ControlledVocab_Xref to INTACT_CURATOR ;

grant select on IA_Feature_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Feature_Xref to INTACT_CURATOR ;

grant select on IA_Interactor_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Interactor_Xref to INTACT_CURATOR ;

grant select on IA_Publication_Xref to INTACT_SELECT ;
grant select, insert,update,delete on IA_Publication_Xref to INTACT_CURATOR ;

create public synonym IA_BioSource_Xref for INTACT.IA_BioSource_Xref;
create public synonym IA_Experiment_Xref for INTACT.IA_Experiment_Xref;
create public synonym IA_ControlledVocab_Xref for INTACT.IA_ControlledVocab_Xref;
create public synonym IA_Feature_Xref for INTACT.IA_Feature_Xref;
create public synonym IA_Interactor_Xref for INTACT.IA_Interactor_Xref;
create public synonym IA_Publication_Xref for INTACT.IA_Publication_Xref;

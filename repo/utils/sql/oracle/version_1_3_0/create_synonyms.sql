-- creates synonyms for the new tables

-- alias synonyms
create public synonym IA_BioSource_Alias for INTACT.IA_BioSource_Alias;
create public synonym IA_Experiment_Alias for INTACT.IA_Experiment_Alias;
create public synonym IA_ControlledVocab_Alias for INTACT.IA_ControlledVocab_Alias;
create public synonym IA_Feature_Alias for INTACT.IA_Feature_Alias;
create public synonym IA_Interactor_Alias for INTACT.IA_Interactor_Alias;
create public synonym IA_Publication_Alias for INTACT.IA_Publication_Alias;

-- new component tables
create public synonym IA_Component_Alias for INTACT.IA_Component_Alias;
create public synonym IA_Component_Xref for INTACT.IA_Component_Xref;
create public synonym IA_Component2Annot for INTACT.IA_Component2Annot;

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
create public synonym ia_component2annot_audit for INTACT.ia_component2annot_audit;

-- alias audit tables
create public synonym IA_BIOSOURCE_ALIAS_AUDIT for INTACT.IA_BIOSOURCE_ALIAS_AUDIT;
create public synonym IA_COMPONENT_ALIAS_AUDIT for INTACT.IA_COMPONENT_ALIAS_AUDIT;
create public synonym IA_CONTROLLEDVOCAB_ALIAS_AUDIT for INTACT.IA_CONTROLLEDVOCAB_ALIAS_AUDIT;
create public synonym IA_EXPERIMENT_ALIAS_AUDIT for INTACT.IA_EXPERIMENT_ALIAS_AUDIT;
create public synonym IA_FEATURE_ALIAS_AUDIT for INTACT.IA_FEATURE_ALIAS_AUDIT;
create public synonym IA_INTERACTOR_ALIAS_AUDIT for INTACT.IA_INTERACTOR_ALIAS_AUDIT;
create public synonym IA_PUBLICATION_ALIAS_AUDIT for INTACT.IA_PUBLICATION_ALIAS_AUDIT;

-- xref audit tables
create public synonym IA_BIOSOURCE_XREF_AUDIT for INTACT.IA_BIOSOURCE_XREF_AUDIT;
create public synonym IA_COMPONENT_XREF_AUDIT for INTACT.IA_COMPONENT_XREF_AUDIT;
create public synonym IA_CONTROLLEDVOCAB_XREF_AUDIT for INTACT.IA_CONTROLLEDVOCAB_XREF_AUDIT;
create public synonym IA_EXPERIMENT_XREF_AUDIT for INTACT.IA_EXPERIMENT_XREF_AUDIT;
create public synonym IA_FEATURE_XREF_AUDIT for INTACT.IA_FEATURE_XREF_AUDIT;
create public synonym IA_INTERACTOR_XREF_AUDIT for INTACT.IA_INTERACTOR_XREF_AUDIT;
create public synonym IA_PUBLICATION_XREF_AUDIT for INTACT.IA_PUBLICATION_XREF_AUDIT;
 

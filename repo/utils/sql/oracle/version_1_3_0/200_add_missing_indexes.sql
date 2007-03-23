PROMPT "Adding INDEX on Annotation indirection tables (7 in total)..."

CREATE INDEX i_fk_bio2annot$annot_ac ON ia_biosource2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_component2annot$annot_ac ON ia_component2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_cvobj2annot$annot_ac ON ia_cvobject2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_exp2annot$annot_ac ON ia_exp2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_feature2annot$annot_ac ON ia_feature2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_int2annot$annot_ac ON ia_int2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_pub2annot$annot_ac ON ia_pub2annot(ANNOTATION_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on ia_cv2cv(CHILD_AC)..."

CREATE INDEX i_fk_cv2cv$child ON ia_cv2cv(CHILD_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on ia_int2exp(EXPERIMENT_AC)..."

CREATE INDEX i_fk_int2exp$experiment ON ia_int2exp(EXPERIMENT_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on OWNER_AC columns (27 in total)..."

CREATE INDEX i_fk_alias$owner ON ia_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_annotation$owner ON ia_annotation(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_biosource$owner ON ia_biosource(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_biosource_alias$owner ON ia_biosource_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_biosourcexref$owner ON ia_biosource_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_component$owner ON ia_component(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_component_alias$owner ON ia_component_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_componentxref$owner ON ia_component_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_controlledvocab$owner ON ia_controlledvocab(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_cv_alias$owner ON ia_controlledvocab_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_cvobjectxref$owner ON ia_controlledvocab_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_experiment$owner ON ia_experiment(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_experiment_alias$owner ON ia_experiment_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_experimentxref$owner ON ia_experiment_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_feature$owner ON ia_feature(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_feature_alias$owner ON ia_feature_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_featurexref$owner ON ia_feature_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_institution$owner ON ia_institution(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_intactnode$owner ON ia_intactnode(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_interactor$owner ON ia_interactor(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_interactor_alias$owner ON ia_interactor_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_interactorxref$owner ON ia_interactor_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_publication$owner ON ia_publication(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_publication_alias$owner ON ia_publication_alias(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_publicationxref$owner ON ia_publication_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_range$owner ON ia_range(OWNER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_xref$owner ON ia_xref(OWNER_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on ia_interactor(PROTEINFORM_AC)..."

CREATE INDEX i_fk_interactor$proteinform_ac ON ia_interactor(PROTEINFORM_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on Xref's qualifier (7 tables in total)..."

CREATE INDEX i_fk_biosourcexref$qualifier ON ia_biosource_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_componentxref$qualifier ON ia_component_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_cvobjectxref$qualifier ON ia_controlledvocab_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_experimentxref$qualifier ON ia_experiment_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_featurexref$qualifier ON ia_feature_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_interactorxref$qualifier ON ia_interactor_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_fk_publicationxref$qualifier ON ia_publication_xref(QUALIFIER_AC) TABLESPACE &&intactIndexTablespace;


PROMPT "Adding INDEX on ia_experiment(RELATEDEXPERIMENT_AC)..."

CREATE INDEX i_fk_experiment$relatedexp ON ia_experiment(RELATEDEXPERIMENT_AC) TABLESPACE &&intactIndexTablespace;


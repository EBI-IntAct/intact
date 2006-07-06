CREATE INDEX i_xref_qualifier_ac on IA_XREF (qualifier_ac);

CREATE INDEX i_XREF_database_ac ON IA_XREF(database_ac);
CREATE INDEX i_XREF_primaryid   ON IA_XREF(primaryid)  ;

CREATE INDEX i_ALIAS_aliastype_ac ON IA_ALIAS(aliastype_ac);

CREATE INDEX i_ANNOTATION_topic_ac ON IA_ANNOTATION(topic_ac);

CREATE INDEX i_BIOSOURCE_tissue_ac   ON IA_BIOSOURCE(tissue_ac)  ;
CREATE INDEX i_BIOSOURCE_celltype_ac ON IA_BIOSOURCE(celltype_ac);

CREATE INDEX i_COMPONENT_expressedin_ac ON IA_COMPONENT(expressedin_ac);
CREATE INDEX i_COMPONENT_role           ON IA_COMPONENT(role)          ;

CREATE INDEX i_EXPERIMENT_biosource_ac     ON IA_EXPERIMENT(biosource_ac)   ;
CREATE INDEX i_EXPERIMENT_detectmethod_ac  ON IA_EXPERIMENT(detectmethod_ac);
CREATE INDEX i_EXPERIMENT_identmethod_ac   ON IA_EXPERIMENT(identmethod_ac) ;

CREATE INDEX i_INTERACTOR_objclass            ON IA_INTERACTOR(objclass)           ;
CREATE INDEX i_INTERACTOR_interactortype_ac   ON IA_INTERACTOR(interactortype_ac)  ;

CREATE INDEX i_INTERACTOR_interactiontypeac  ON IA_INTERACTOR(interactiontype_ac) ;

CREATE INDEX i_INTERACTIONS_protein1_ac      ON IA_INTERACTIONS(protein1_ac)    ;
CREATE INDEX i_INTERACTIONS_protein2_ac      ON IA_INTERACTIONS(protein2_ac)    ;
CREATE INDEX i_INTERACTIONS_interaction_ac   ON IA_INTERACTIONS(interaction_ac) ;
CREATE INDEX i_INTERACTIONS_experiment_ac    ON IA_INTERACTIONS(experiment_ac)  ;
CREATE INDEX i_INTERACTIONS_detectmethod_ac  ON IA_INTERACTIONS(detectmethod_ac);

CREATE INDEX i_PUBMEDprimaryid  ON IA_PUBMED(primaryid) ;
CREATE INDEX i_PUBMEDstatus     ON IA_PUBMED(status)    ;


--
-- Update database schema version

UPDATE ia_db_info
set value = '1.1.7'
where UPPER(dbi_key) ='SCHEMA_VERSION';

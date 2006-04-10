--
-- Add missing index on IntAct tables.
-- Most of which are on foreign key column.
--
-- Author: Samuel Kerrien (skerrien@ebi.ac,uk)
-- Date:   2006-04-06
--

set serveroutput on size 1000000

DEFINE intactIndexTablespace = INTACT_IDX


PROMPT Adding missing index on IA_XREF
CREATE INDEX i_XREF$database_ac ON IA_XREF(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_XREF$primaryid   ON IA_XREF(primaryid)   TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_ALIAS
CREATE INDEX i_ALIAS$aliastype_ac ON IA_ALIAS(aliastype_ac) TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_ANNOTATION
CREATE INDEX i_ANNOTATION$topic_ac ON IA_ANNOTATION(topic_ac) TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_BIOSOURCE
CREATE INDEX i_BIOSOURCE$tissue_ac   ON IA_BIOSOURCE(tissue_ac)   TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_BIOSOURCE$celltype_ac ON IA_BIOSOURCE(celltype_ac) TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_COMPONENT
CREATE INDEX i_COMPONENT$expressedin_ac ON IA_COMPONENT(expressedin_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_COMPONENT$role           ON IA_COMPONENT(role)           TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_EXPERIMENT
CREATE INDEX i_EXPERIMENT$biosource_ac     ON IA_EXPERIMENT(biosource_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_EXPERIMENT$detectmethod_ac  ON IA_EXPERIMENT(detectmethod_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_EXPERIMENT$identmethod_ac   ON IA_EXPERIMENT(identmethod_ac)  TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_INTERACTOR
CREATE INDEX i_INTERACTOR$objclass            ON IA_INTERACTOR(objclass)            TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_INTERACTOR$interactortype_ac   ON IA_INTERACTOR(interactortype_ac)   TABLESPACE &&intactIndexTablespace;
-- the name is truncated as Oracle doesn't like it that long :(
CREATE INDEX i_INTERACTOR$interactiontypeac  ON IA_INTERACTOR(interactiontype_ac)  TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_INTERACTIONS
CREATE INDEX i_INTERACTIONS$protein1_ac      ON IA_INTERACTIONS(protein1_ac)     TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_INTERACTIONS$protein2_ac      ON IA_INTERACTIONS(protein2_ac)     TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_INTERACTIONS$interaction_ac   ON IA_INTERACTIONS(interaction_ac)  TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_INTERACTIONS$experiment_ac    ON IA_INTERACTIONS(experiment_ac)   TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_INTERACTIONS$detectmethod_ac  ON IA_INTERACTIONS(detectmethod_ac) TABLESPACE &&intactIndexTablespace;


PROMPT Adding missing index on IA_PUBMED
CREATE INDEX i_PUBMED$primaryid  ON IA_PUBMED(primaryid)  TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_PUBMED$status     ON IA_PUBMED(status)     TABLESPACE &&intactIndexTablespace;

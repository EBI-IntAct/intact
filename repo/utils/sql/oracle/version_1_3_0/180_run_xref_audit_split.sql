
set serveroutput on

-- create required index to speed up execution
PROMPT 'Adding indexes on column AC of tables: %_xref_audit to speed up processing...'
CREATE INDEX i_interactor_xref_audit$ac    ON ia_interactor_xref_audit(ac)      TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_experiment_xref_audit$ac ON ia_experiment_xref_audit(ac)      TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_cv_xref_audit$ac         ON ia_controlledvocab_xref_audit(ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_biosource_xref_audit$ac  ON ia_biosource_xref_audit(ac)       TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_feature_xref_audit$ac    ON ia_feature_xref_audit(ac)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_pub_xref_audit$ac        ON ia_publication_xref_audit(ac)     TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_component_xref_audit$ac  ON ia_component_xref_audit(ac)       TABLESPACE &&intactIndexTablespace;


execute  PROC_SPLIT_XREF_AUDIT();
/


-- Drop indexes, we do not need them
PROMPT 'Dropping indexes...'
DROP INDEX i_interactor_xref_audit$ac;
DROP INDEX i_ia_experiment_xref_audit$ac;
DROP INDEX i_ia_cv_xref_audit$ac;
DROP INDEX i_ia_biosource_xref_audit$ac;
DROP INDEX i_ia_feature_xref_audit$ac;
DROP INDEX i_ia_pub_xref_audit$ac;
DROP INDEX i_ia_component_xref_audit$ac


set serveroutput on

-- create required index to speed up execution
PROMPT 'Adding indexes on column AC of tables: %_alias_audit to speed up processing...'
CREATE INDEX i_interactor_alias_audit$ac    ON ia_interactor_alias_audit(ac)      TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_experiment_alias_audit$ac ON ia_experiment_alias_audit(ac)      TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_cv_alias_audit$ac         ON ia_controlledvocab_alias_audit(ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_biosource_alias_audit$ac  ON ia_biosource_alias_audit(ac)       TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_feature_alias_audit$ac    ON ia_feature_alias_audit(ac)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_pub_alias_audit$ac        ON ia_publication_alias_audit(ac)     TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ia_component_alias_audit$ac  ON ia_component_alias_audit(ac)       TABLESPACE &&intactIndexTablespace;


-- Run procedure
execute  PROC_SPLIT_ALIAS_AUDIT();
/


-- Drop indexes, we do not need them
PROMPT 'Dropping indexes...'
DROP INDEX i_interactor_alias_audit$ac;
DROP INDEX i_ia_experiment_alias_audit$ac;
DROP INDEX i_ia_cv_alias_audit$ac;
DROP INDEX i_ia_biosource_alias_audit$ac;
DROP INDEX i_ia_feature_alias_audit$ac;
DROP INDEX i_ia_pub_alias_audit$ac;
DROP INDEX i_ia_component_alias_audit$ac;



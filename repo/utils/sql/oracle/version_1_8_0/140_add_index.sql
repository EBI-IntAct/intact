PROMPT Adding index on IA_CONTROLLEDVOCAB
CREATE INDEX i_CVOBJECT$mi_identifier ON IA_CONTROLLEDVOCAB(mi_identifier) TABLESPACE &&intactIndexTablespace;
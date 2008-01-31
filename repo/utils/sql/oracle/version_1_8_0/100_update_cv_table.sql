PROMPT Add new column (MI_IDENTIFIER) in the ia_controlledvocab
ALTER TABLE ia_controlledvocab  ADD(MI_IDENTIFIER VARCHAR2(10)  NULL);

CREATE INDEX i_ControlledVocab$mi on IA_ControlledVocab(mi_identifier) TABLESPACE &&intactMainTablespace;
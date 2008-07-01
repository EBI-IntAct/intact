PROMPT "Renaming ia_controlledvocab.mi_identifier to cv_identifier"
ALTER TABLE ia_controlledvocab
RENAME COLUMN mi_identifier TO cv_identifier;


PROMPT "Increase field length of ia_controlledvocab.cv_identifier to 30"
ALTER TABLE ia_controlledvocab
MODIFY cv_identifier VARCHAR2(30);
PROMPT "Renaming ia_controlledvocab.mi_identifier to identifier"
ALTER TABLE ia_controlledvocab
RENAME COLUMN mi_identifier TO identifier;


PROMPT "Increase field length of ia_controlledvocab.identifier to 30"
ALTER TABLE ia_controlledvocab
MODIFY identifier VARCHAR2(30);
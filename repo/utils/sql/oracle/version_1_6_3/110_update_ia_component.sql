
-- PROMPT Remove column handling single participant identification method from IA_COMPONENT.
-- ALTER TABLE ia_component DROP COLUMN IDENTMETHOD_AC;

PROMPT Add missing columns (shortLabel, fullName) in the ia_component_audit table
ALTER TABLE ia_component_audit  ADD(shortLabel VARCHAR2(20)  NULL);
ALTER TABLE ia_component_audit  ADD(fullName   VARCHAR2(250) NULL);

-- PROMPT Remove column handling single participant identification method from IA_COMPONENT_AUDIT.
-- ALTER TABLE ia_component_audit DROP COLUMN IDENTMETHOD_AC;
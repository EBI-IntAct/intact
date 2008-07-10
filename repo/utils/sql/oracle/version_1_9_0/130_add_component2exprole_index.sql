PROMPT Creating index for "ia_component2exprole"
CREATE INDEX i_comp2exprole$comp_ac on ia_component2exprole(component_ac)   TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_comp2exprole$role_ac on ia_component2exprole(experimentalrole_ac)   TABLESPACE &&intactIndexTablespace;

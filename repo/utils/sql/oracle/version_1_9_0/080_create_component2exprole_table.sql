PROMPT Creating table "ia_component2exprole"
CREATE TABLE ia_component2exprole
(      component_ac            VARCHAR2(30)   NOT NULL   CONSTRAINT fk_comp2exprl$compnt REFERENCES IA_COMPONENT(ac) ON DELETE CASCADE
    ,  experimentalrole_ac     VARCHAR2(30)   NOT NULL   CONSTRAINT fk_comp2exprl$exprl  REFERENCES IA_CONTROLLEDVOCAB(ac) ON DELETE CASCADE
    ,  created                 DATE           DEFAULT SYSDATE
    ,  updated                 DATE           DEFAULT SYSDATE
    ,  userstamp               VARCHAR2(30)   DEFAULT USER
    ,  created_user            VARCHAR2(30)   DEFAULT USER NOT NULL ENABLE 
)
TABLESPACE &&intactMainTablespace;

set term off
    COMMENT ON TABLE ia_component2exprole IS
    'Represents a associative table between the component and experimental role.';
    COMMENT ON COLUMN ia_component2exprole.component_ac IS
    'Refers to the Unique auto-generated accession number of ia_component-ac.';
    COMMENT ON COLUMN ia_component2exprole.experimentalrole_ac IS
    'Experimental role of that component eg. bait, prey, ...';
set term on


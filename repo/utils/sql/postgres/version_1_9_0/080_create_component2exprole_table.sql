-- PROMPT Creating table "ia_component2exprole"
CREATE TABLE ia_component2exprole
(      component_ac            VARCHAR(30)   NOT NULL   CONSTRAINT fk_comp2exprl$compnt REFERENCES IA_COMPONENT(ac)  ON DELETE CASCADE
    ,  experimentalrole_ac     VARCHAR(30)   NOT NULL   CONSTRAINT fk_comp2exprl$exprl  REFERENCES IA_CONTROLLEDVOCAB(ac)  ON DELETE CASCADE
)
;


COMMENT ON TABLE ia_component2exprole IS
'Represents a associative table between the component and experimental role.';
COMMENT ON COLUMN ia_component2exprole.component_ac IS
'Refers to the Unique auto-generated accession number of ia_component-ac.';
COMMENT ON COLUMN ia_component2exprole.experimentalrole_ac IS
    'Experimental role of that component eg. bait, prey, ...';



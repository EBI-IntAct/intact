ALTER TABLE IA_Component ADD(biologicalrole_ac  VARCHAR2(30)  CONSTRAINT fk_component$biologicalrole  REFERENCES IA_ControlledVocab(ac));
ALTER TABLE IA_Component ADD(identmethod_ac     VARCHAR2(30)  CONSTRAINT fk_component$identmethod     REFERENCES IA_ControlledVocab(ac));
commit;


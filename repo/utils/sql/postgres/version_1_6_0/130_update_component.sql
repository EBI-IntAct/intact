ALTER TABLE IA_Component ADD COLUMN biologicalrole_ac  VARCHAR(30)  CONSTRAINT fk_component$biologicalrole  REFERENCES IA_ControlledVocab(ac);
ALTER TABLE IA_Component ADD COLUMN identmethod_ac  VARCHAR(30)  CONSTRAINT fk_component$identmethod  REFERENCES IA_ControlledVocab(ac);
commit;

